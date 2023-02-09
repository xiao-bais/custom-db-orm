package com.custom.taskmanager.service;

import com.custom.comm.page.DbPageRows;
import com.custom.taskmanager.params.TaskRecordRequest;
import com.custom.taskmanager.view.TaskRecordModel;

/**
 * @author  Xiao-Bai
 * @since  2022/7/10 1:34
 * @Desc
 */
public interface TaskRecordService {

    /**
     * 任务列表查询
     */
    DbPageRows<TaskRecordModel> taskList(TaskRecordRequest request);

    /**
     * 查询单个任务详情
     */
    TaskRecordModel selectTaskById(Integer taskId);

    /**
     * 编辑任务
     */
    void editTask(TaskRecordModel model);

    /**
     * 删除任务
     */
    void deleteTask(Integer taskId);

    /**
     * 添加任务
     */
    void addTask(TaskRecordModel model);




}
