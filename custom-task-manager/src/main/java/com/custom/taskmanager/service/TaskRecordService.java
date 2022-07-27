package com.custom.taskmanager.service;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.condition.Conditions;
import com.custom.comm.JudgeUtil;
import com.custom.comm.date.DateTimeUtils;
import com.custom.comm.page.DbPageRows;
import com.custom.taskmanager.BException;
import com.custom.taskmanager.entity.TaskImgPath;
import com.custom.taskmanager.entity.TaskRecord;
import com.custom.taskmanager.enums.TaskDifficultyEnum;
import com.custom.taskmanager.enums.TaskImgTypeEnum;
import com.custom.taskmanager.enums.TaskPriorityEnum;
import com.custom.taskmanager.enums.TaskProgressEnum;
import com.custom.taskmanager.params.TaskRecordRequest;
import com.custom.taskmanager.view.TaskRecordModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/10 1:34
 * @Desc
 */
@Service
@SuppressWarnings("unchecked")
public class TaskRecordService {

    @Resource
    private JdbcDao jdbcDao;


    /**
     * 任务列表查询
     */
    public DbPageRows<TaskRecordModel> taskList(TaskRecordRequest request) {

        // 查询任务列表
        DbPageRows<TaskRecordModel> dbPageRows = jdbcDao.selectPageRows(Conditions.lambdaQuery(TaskRecordModel.class)
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

        for (TaskRecordModel row : dbPageRows.getData()) {
            // 加载展示信息
            this.loadViewInfo(row);
        }
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

    public void editTask(TaskRecordModel model) {
        if (model == null) {
            throw new BException("未知的任务");
        }
        model.setOperatorTime(DateTimeUtils.getThisTime());

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




}
