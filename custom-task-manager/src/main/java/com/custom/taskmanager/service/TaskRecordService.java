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
    DbPageRows<TaskRecordModel> taskList(TaskRecordRequest request) throws Exception;

    /**
     * 查询单个任务详情
     */
    TaskRecordModel selectTaskById(Integer taskId) throws Exception;

    /**
     * 编辑任务
     */
    void editTask(TaskRecordModel model) throws Exception;

    /**
     * 删除任务
     */
    void deleteTask(Integer taskId) throws Exception;

    /**
     * 添加任务
     */
    void addTask(TaskRecordModel model) throws Exception;




}
