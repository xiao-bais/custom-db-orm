package com.custom.taskmanager.controller;

import com.custom.comm.BackResult;
import com.custom.comm.page.DbPageRows;
import com.custom.taskmanager.enums.TaskDifficultyEnum;
import com.custom.taskmanager.enums.TaskPriorityEnum;
import com.custom.taskmanager.enums.TaskProgressEnum;
import com.custom.taskmanager.params.TaskRecordRequest;
import com.custom.taskmanager.service.TaskRecordService;
import com.custom.taskmanager.view.TaskRecordModel;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/10 1:33
 * @Desc
 */
@Controller
@RequestMapping("/task")
public class TaskController {


    @Resource
    TaskRecordService taskRecordService;


    @ApiOperation("主任务列表查询")
    @PostMapping("/main_task_list")
    public BackResult<DbPageRows<TaskRecordModel>> mainTaskList(TaskRecordRequest request) {
        DbPageRows<TaskRecordModel> pageRows = taskRecordService.taskList(request);
        return BackResult.bySuccess(pageRows);
    }

    @SneakyThrows
    @ApiOperation("主任务列表查询")
    @GetMapping("/main_task_select")
    public BackResult<Map<String, Object>> mainTaskList() {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("taskDifficulty", TaskDifficultyEnum.values());
        resMap.put("taskPriority", TaskPriorityEnum.values());
        resMap.put("taskProgress", TaskProgressEnum.values());
        return BackResult.bySuccess(resMap);
    }


}
