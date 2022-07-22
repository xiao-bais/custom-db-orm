package com.custom.taskmanager.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/10 14:45
 * @Desc
 */
@Getter
@Setter
@ApiModel("任务-请求参数")
public class TaskRecordRequest {

    @ApiModelProperty("模糊搜索")
    private String keyword;

    @ApiModelProperty("当前进度: 1-未开始，2-进行中，3-暂未完成，4-已完成待测验，5-已结束")
    private List<Integer> currentProgress = new ArrayList<>();

    @ApiModelProperty("优先级: 1-低，2-中，3-高")
    private List<Integer> priority = new ArrayList<>();

    @ApiModelProperty("难度: 1-不难，2-有点难，3-特别难")
    private List<Integer> difficulty = new ArrayList<>();

    @ApiModelProperty("时间类型：1-开始时间，2-结束时间")
    private Integer timeType;

    @ApiModelProperty("预计开始时间")
    private Integer startTime;

    @ApiModelProperty("预计结束时间")
    private Integer endTime;

    @ApiModelProperty("任务是否过期")
    private Boolean expireStatus = false;

    @ApiModelProperty("页数")
    private Integer pageIndex = 1;

    @ApiModelProperty("显示量")
    private Integer pageSize = 10;

}
