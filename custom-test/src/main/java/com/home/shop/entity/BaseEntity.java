package com.home.shop.entity;

import com.custom.comm.annotations.DbField;
import lombok.Data;

/**
 * @author  Xiao-Bai
 * @since  2021/10/25
 * @Description 基础信息
 */
@Data
public class BaseEntity {

    @DbField(value = "create_time", desc = "创建时间")
    private int createTime;

    @DbField(value = "update_time", desc = "修改时间")
    private int updateTime;

    @DbField(desc = "状态：0-正常，1-已删除")
    private int state;

}
