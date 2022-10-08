package com.custom.taskmanager.controller;

import com.custom.comm.utils.BackResult;
import com.custom.comm.page.DbPageRows;
import com.custom.taskmanager.enums.TaskDifficultyEnum;
import com.custom.taskmanager.enums.TaskPriorityEnum;
import com.custom.taskmanager.enums.TaskProgressEnum;
import com.custom.taskmanager.params.TaskRecordRequest;
import com.custom.taskmanager.service.TaskRecordService;
import com.custom.taskmanager.view.TaskRecordModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/10 1:33
 * @Desc
 */
@Api(tags = "主任务管理")
@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    private TaskRecordService taskRecordService;


    @ApiOperation("主任务列表查询")
    @PostMapping("/main_task_list")
    public BackResult<DbPageRows<TaskRecordModel>> mainTaskList(TaskRecordRequest request) {
        DbPageRows<TaskRecordModel> pageRows = taskRecordService.taskList(request);
        return BackResult.bySuccess(pageRows);
    }


    @ApiOperation("下拉框")
    @GetMapping("/main_task_select")
    public BackResult<Map<String, Object>> mainTaskList() {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("taskDifficulty", TaskDifficultyEnum.values());
        resMap.put("taskPriority", TaskPriorityEnum.values());
        resMap.put("taskProgress", TaskProgressEnum.values());
        return BackResult.bySuccess(resMap);
    }


    @ApiOperation("查询单个任务详情")
    @GetMapping("/select_one")
    public BackResult<TaskRecordModel> selectOne(@RequestParam Integer taskId) {
        if (taskId == null) {
            return BackResult.byError("空的任务ID");
        }
        TaskRecordModel taskRecordModel = taskRecordService.selectTaskById(taskId);
        return BackResult.bySuccess(taskRecordModel);
    }



    @ApiOperation("编辑任务详情")
    @PostMapping("/edit_task_detail")
    public BackResult editTaskDetail(@RequestBody TaskRecordModel model) {
        taskRecordService.editTask(model);
        return BackResult.bySuccess();
    }

    @ApiOperation("删除")
    @GetMapping("/delete_one")
    public BackResult deleteOne(@RequestParam Integer taskId) {
        taskRecordService.deleteTask(taskId);
        return BackResult.bySuccess();
    }





}
