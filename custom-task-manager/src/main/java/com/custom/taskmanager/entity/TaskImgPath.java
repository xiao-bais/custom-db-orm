package com.custom.taskmanager.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/10 1:13
 * @Desc
 */
@Getter
@Setter
@DbTable(table = "task_img_path", desc = "任务图片表")
public class TaskImgPath extends BaseEntity {

    @DbKey
    private Integer id;

    @DbField(desc = "任务代码")
    private String taskCode;

    @DbField(desc = "图片类型: 1-任务图片，2-测验结果")
    private Integer imgType;

    @DbField(desc = "图片地址")
    private String imgPath;


}
