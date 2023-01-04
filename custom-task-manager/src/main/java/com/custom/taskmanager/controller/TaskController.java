package com.custom.taskmanager.controller;

import com.custom.jdbc.back.BackResult;
import com.custom.comm.page.DbPageRows;
import com.custom.taskmanager.enums.TaskDifficultyEnum;
import com.custom.taskmanager.enums.TaskPriorityEnum;
import com.custom.taskmanager.enums.TaskProgressEnum;
import com.custom.taskmanager.params.TaskRecordRequest;
import com.custom.taskmanager.service.TaskRecordService;
import com.custom.taskmanager.view.TaskRecordModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/query_task_list")
    public BackResult<DbPageRows<TaskRecordModel>> mainTaskList(@RequestBody TaskRecordRequest request) {
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
        return BackResult.execCall(op -> {
            TaskRecordModel taskRecordModel = taskRecordService.selectTaskById(taskId);
            op.setData(taskRecordModel);
        });

    }



    @ApiOperation("编辑任务详情")
    @PostMapping("/edit_task_detail")
    public BackResult editTaskDetail(@RequestBody TaskRecordModel model) {
        taskRecordService.editTask(model);
        return BackResult.bySuccess();
    }

    @ApiOperation("删除单个任务")
    @GetMapping("/delete_one")
    public BackResult deleteOne(@RequestParam Integer taskId) {
        taskRecordService.deleteTask(taskId);
        return BackResult.bySuccess();
    }

    @ApiOperation("添加任务")
    @PostMapping("add_task")
    public BackResult addTask(@RequestBody TaskRecordModel model) {
        taskRecordService.addTask(model);
        return BackResult.bySuccess();
    }





}
