package com.custom.taskmanager.view;

import com.custom.taskmanager.entity.TaskImgPath;
import com.custom.taskmanager.entity.TaskRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/10 1:37
 * @Desc
 */
@Getter
@Setter
public class TaskRecordModel extends TaskRecord {

    @ApiModelProperty("当前进度（标识）")
    private String currentProgressStr;

    @ApiModelProperty("优先级（标识）")
    private String priorityStr;

    @ApiModelProperty("难度（标识）")
    private String difficultyStr;

    @ApiModelProperty("预计开始时间（标识）")
    private String startTimeStr;

    @ApiModelProperty("预计结束时间（标识）")
    private String endTimeStr;

    @ApiModelProperty("创建时间")
    private String createTimeStr;

    @ApiModelProperty("操作时间")
    private String operatorTimeStr;

    @ApiModelProperty("任务图片")
    private List<TaskImgPath> taskImgs = new ArrayList<>();

    @ApiModelProperty("测验结果附件")
    private List<TaskImgPath> testResultImgs = new ArrayList<>();

}
