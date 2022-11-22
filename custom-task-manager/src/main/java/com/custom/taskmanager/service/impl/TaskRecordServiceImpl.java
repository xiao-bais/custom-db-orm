package com.custom.taskmanager.service.impl;

import com.custom.action.condition.Conditions;
import com.custom.action.core.JdbcDao;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.date.DateTimeUtils;
import com.custom.comm.page.DbPageRows;
import com.custom.comm.utils.StrUtils;
import com.custom.taskmanager.exception.BException;
import com.custom.taskmanager.entity.TaskImgPath;
import com.custom.taskmanager.entity.TaskRecord;
import com.custom.taskmanager.enums.TaskDifficultyEnum;
import com.custom.taskmanager.enums.TaskImgTypeEnum;
import com.custom.taskmanager.enums.TaskPriorityEnum;
import com.custom.taskmanager.enums.TaskProgressEnum;
import com.custom.taskmanager.params.TaskRecordRequest;
import com.custom.taskmanager.service.TaskRecordService;
import com.custom.taskmanager.view.TaskRecordModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/28 20:22
 * @Desc
 */
@Service
@SuppressWarnings("unchecked")
public class TaskRecordServiceImpl implements TaskRecordService {

    @Resource
    private JdbcDao jdbcDao;


    /**
     * 任务列表查询
     */
    @Override
    public DbPageRows<TaskRecordModel> taskList(TaskRecordRequest request) {

        // 查询任务列表
        DbPageRows<TaskRecordModel> dbPageRows = jdbcDao.selectPage(Conditions.lambdaQuery(TaskRecordModel.class)
                .pageParams(request.getPageIndex(), request.getPageSize())

                // 模糊搜索，主要查询任务标题
                .like(JudgeUtil.isNotEmpty(request.getKeyword()), TaskRecordModel::getTaskTitle, request.getKeyword())

                // 若为1， 则查询开始时间
                .between(JudgeUtil.isValid(request.getTimeType()) && request.getTimeType() == 1, TaskRecordModel::getStartTime, request.getStartTime(), request.getEndTime())

                // 若为2， 则查询结束时间
                .between(JudgeUtil.isValid(request.getTimeType()) && request.getTimeType() == 2, TaskRecordModel::getEndTime, request.getStartTime(), request.getEndTime())

                // 查询当前进度
                .in(JudgeUtil.isNotEmpty(request.getCurrentProgress()), TaskRecord::getCurrentProgress, request.getCurrentProgress())

                // 查询任务难度
                .in(JudgeUtil.isNotEmpty(request.getDifficulty()), TaskRecordModel::getDifficulty, request.getDifficulty())

                // 查询任务的优先级
                .in(JudgeUtil.isNotEmpty(request.getPriority()), TaskRecordModel::getPriority, request.getPriority())

                // 查询任务是否过期
                .eq(TaskRecordModel::getExpireStatus, false)
                .orderByDesc(TaskRecordModel::getCreateTime)

        );

        if (dbPageRows.getData().isEmpty()) {
            return dbPageRows;
        }

        dbPageRows.getData().forEach(this::loadViewInfo);
        return dbPageRows;
    }

    /**
     * 加载展示信息
     */
    private void loadViewInfo(TaskRecordModel row) {
        row.setDifficultyStr(TaskDifficultyEnum.getName(row.getDifficulty()));
        row.setCurrentProgressStr(TaskProgressEnum.getName(row.getCurrentProgress()));
        row.setPriorityStr(TaskPriorityEnum.getName(row.getPriority()));
        row.setStartTimeStr(DateTimeUtils.getFormatByTimeStamp(row.getStartTime()));
        row.setEndTimeStr(DateTimeUtils.getFormatByTimeStamp(row.getEndTime()));
        row.setCreateTimeStr(DateTimeUtils.getFormatByTimeStamp(row.getCreateTime()));
        row.setOperatorTimeStr(DateTimeUtils.getFormatByTimeStamp(row.getOperatorTime()));
    }

    @Override
    public TaskRecordModel selectTaskById(Integer taskId) {

        TaskRecordModel taskRecordModel = jdbcDao.selectByKey(TaskRecordModel.class, taskId);
        this.loadViewInfo(taskRecordModel);

        // 查询任务对应的附件图片
        List<TaskImgPath> taskImgPaths = jdbcDao.selectList(Conditions.lambdaQuery(TaskImgPath.class)
                .eq(TaskImgPath::getTaskCode, taskRecordModel.getTaskCode())
        );

        if (!taskImgPaths.isEmpty()) {
            List<TaskImgPath> imgPaths = taskImgPaths.stream().filter(x -> x.getImgType().equals(TaskImgTypeEnum.TASK.getCode())).collect(Collectors.toList());
            taskRecordModel.setTaskImgs(imgPaths);

            List<TaskImgPath> testResult = taskImgPaths.stream().filter(x -> x.getImgType().equals(TaskImgTypeEnum.TEST_RESULT.getCode())).collect(Collectors.toList());
            taskRecordModel.setTestResultImgs(testResult);
        }

        return taskRecordModel;

    }

    @Override
    public void editTask(TaskRecordModel model) {
        if (model == null) {
            throw new BException("未知的任务");
        }

        TaskRecordModel oldTaskModel = jdbcDao.selectByKey(TaskRecordModel.class, model.getId());
        TaskProgressEnum oldTaskProgress = TaskProgressEnum.valueOf(oldTaskModel.getCurrentProgress());
        if (oldTaskProgress == null) {
            throw new BException("未知的进度");
        }
        TaskProgressEnum newTaskProgress = TaskProgressEnum.valueOf(oldTaskModel.getCurrentProgress());
        if (newTaskProgress == null) {
            throw new BException("未知的进度");
        }

        // 若进度是已完成待检测，或者是完成，则不允许修改
        if (TaskProgressEnum.isNotAllowEdit(oldTaskProgress)) {
            throw new BException(String.format("当前任务进度为[%s], 不允许进行修改", oldTaskProgress.getName()));
        }

        // 检查标题是否存在重复
        long count = jdbcDao.selectCount(Conditions.lambdaQuery(TaskRecordModel.class)
                .eq(TaskRecordModel::getTaskTitle, model.getTaskTitle())
                .ne(TaskRecordModel::getId, model.getId())
        );
        if (count > 0) {
            throw new BException("该标题已存在");
        }

        if (newTaskProgress == TaskProgressEnum.UN_FINISHED && StrUtils.isBlank(model.getReason())) {
            throw new BException("未完成时，需填写未完成原因");
        }

        if (newTaskProgress == TaskProgressEnum.ENDED && StrUtils.isBlank(model.getTestResult())) {
            throw new BException("任务完成后，需填写检测结果");
        }

        jdbcDao.save(model);

        if (!model.getTaskImgs().isEmpty()) {
            for (TaskImgPath taskImg : model.getTaskImgs()) {
                jdbcDao.save(taskImg);
            }
        }

        if (!model.getTestResultImgs().isEmpty()) {
            for (TaskImgPath testResultImg : model.getTestResultImgs()) {
                jdbcDao.save(testResultImg);
            }
        }


    }

    @Override
    public void deleteTask(Integer taskId) {
        TaskRecordModel taskRecordModel = jdbcDao.selectByKey(TaskRecordModel.class, taskId);
        if (taskRecordModel == null) {
            throw new BException("未知的任务ID");
        }

        TaskProgressEnum taskProgressEnum = TaskProgressEnum.valueOf(taskRecordModel.getCurrentProgress());
        Asserts.notNull(taskProgressEnum, "未知的进度");
        if (TaskProgressEnum.isNotAllowEdit(taskProgressEnum)) {
            throw new BException(String.format("该任务当前进度为[%s], 不允许删除", taskProgressEnum.getName()));
        }

        // 删除
        jdbcDao.deleteByKey(TaskRecordModel.class, taskId);

        List<TaskImgPath> taskImgPaths = jdbcDao.selectList(Conditions.lambdaQuery(TaskImgPath.class)
                .eq(TaskImgPath::getTaskCode, taskRecordModel.getTaskCode())
        );
        for (TaskImgPath taskImgPath : taskImgPaths) {
            jdbcDao.deleteByKey(TaskImgPath.class, taskImgPath.getId());
        }

    }

    @Override
    public void addTask(TaskRecordModel model) {
        long count = jdbcDao.selectCount(Conditions.lambdaQuery(TaskRecordModel.class)
                .eq(TaskRecordModel::getTaskTitle, model.getTaskTitle())
        );
        if (count > 0) {
            throw new BException(String.format("已存在标题为[%s]的任务", model.getTaskTitle()));
        }
        model.setTaskCode(CustomUtil.getUUID());
        jdbcDao.insert(model);

        if (!model.getTaskImgs().isEmpty()) {
            jdbcDao.insertBatch(model.getTaskImgs());
        }
    }

}
