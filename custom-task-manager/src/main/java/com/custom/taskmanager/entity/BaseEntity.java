package com.custom.taskmanager.entity;

import com.custom.comm.annotations.DbField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author  Xiao-Bai
 * @since  2022/7/10 0:54
 * @Desc
 */
@Getter
@Setter
public class BaseEntity {

    @DbField(desc = "创建时间")
    private Integer createTime;

    @DbField(desc = "操作人ID")
    private Integer operatorId;

    @DbField(desc = "操作时间")
    private Integer operatorTime;

    @DbField(desc = "数据状态？ 0-未删除，1-已删除")
    private Integer state;

}
