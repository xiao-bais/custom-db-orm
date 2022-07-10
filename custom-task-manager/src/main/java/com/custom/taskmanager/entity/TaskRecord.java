package com.custom.taskmanager.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/10 0:45
 * @Desc
 */
@Getter
@Setter
@DbTable(table = "task_record", desc = "任务记录表", enabledDefaultValue = true)
public class TaskRecord extends BaseEntity {

    @DbKey
    private Integer id;

    @DbField(desc = "任务代码")
    @ApiModelProperty("任务代码")
    private String taskCode;

    @DbField(desc = "任务标题")
    @ApiModelProperty("任务标题")
    private String taskTitle;

    @DbField(desc = "任务内容")
    @ApiModelProperty("任务内容")
    private String taskContent;

    @DbField(desc = "当前进度: 1-未开始，2-进行中，3-未完成，4-已完成待测验，5-已结束")
    @ApiModelProperty("当前进度: 1-未开始，2-进行中，3-未完成，4-已完成待测验，5-已结束")
    private Integer currentProgress;

    @DbField(desc = "测验结果")
    @ApiModelProperty("测验结果")
    private String testResult;

    @DbField(desc = "优先级: 1-低，2-中，3-高")
    @ApiModelProperty("优先级: 1-低，2-中，3-高")
    private Integer priority;

    @DbField(desc = "难度: 1-不难，2-有点难，3-特别难")
    @ApiModelProperty("难度: 1-不难，2-有点难，3-特别难")
    private Integer difficulty;

    @DbField(desc = "预计开始时间")
    @ApiModelProperty("预计开始时间")
    private Integer startTime;

    @DbField(desc = "预计结束时间")
    @ApiModelProperty("预计结束时间")
    private Integer endTime;

    @DbField(desc = "任务是否过期")
    @ApiModelProperty("任务是否过期")
    private Boolean expireStatus;


}
