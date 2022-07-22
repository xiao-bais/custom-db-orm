package com.custom.taskmanager.service;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.action.condition.Conditions;
import com.custom.comm.JudgeUtil;
import com.custom.comm.date.DateTimeUtils;
import com.custom.comm.page.DbPageRows;
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

        );

        if (dbPageRows.getData().isEmpty()) {
            return dbPageRows;
        }

        // 主任务代码唯一标识集合
        List<String> taskCodes = dbPageRows.getData().stream().map(TaskRecordModel::getTaskCode).distinct().collect(Collectors.toList());

        // 查询任务对应的附件图片
        List<TaskImgPath> taskImgPaths = jdbcDao.selectList(Conditions.lambdaQuery(TaskImgPath.class)
                .in(TaskImgPath::getTaskCode, taskCodes)
        );


        for (TaskRecordModel row : dbPageRows.getData()) {
            row.setDifficultyStr(TaskDifficultyEnum.getName(row.getDifficulty()));
            row.setCurrentProgressStr(TaskProgressEnum.getName(row.getCurrentProgress()));
            row.setPriorityStr(TaskPriorityEnum.getName(row.getPriority()));
            row.setStartTimeStr(DateTimeUtils.getFormatByTimeStamp(row.getStartTime()));
            row.setEndTimeStr(DateTimeUtils.getFormatByTimeStamp(row.getEndTime()));
            row.setCreateTimeStr(DateTimeUtils.getFormatByTimeStamp(row.getCreateTime()));
            row.setOperatorTimeStr(DateTimeUtils.getFormatByTimeStamp(row.getOperatorTime()));

            if (taskImgPaths.isEmpty()) {
                continue;
            }

            // 任务图片附件
            List<TaskImgPath> taskImgList = taskImgPaths.stream().filter(x -> x.getTaskCode().equals(row.getTaskCode()) && x.getImgType().equals(TaskImgTypeEnum.TASK.getCode())).collect(Collectors.toList());
            if (!taskImgList.isEmpty()) {
                row.setTaskImgs(taskImgList);
            }

            // 测压结果附件
            List<TaskImgPath> testResultImgList = taskImgPaths.stream().filter(x -> x.getTaskCode().equals(row.getTaskCode()) && x.getImgType().equals(TaskImgTypeEnum.TEST_RESULT.getCode())).collect(Collectors.toList());
            if (!testResultImgList.isEmpty()) {
                row.setTestResultImgs(testResultImgList);
            }
        }
        return dbPageRows;
    }



}
