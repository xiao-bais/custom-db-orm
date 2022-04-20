package com.home.shop.pojo;

 import com.custom.comm.annotations.DbField;
 import com.custom.comm.annotations.DbKey;
 import com.custom.comm.annotations.DbTable;
 import lombok.Data;
 import io.swagger.annotations.ApiModelProperty;
 import com.custom.comm.enums.KeyStrategy;

import java.math.BigDecimal;

@Data
@DbTable(table = "shop_cart")
public class CartPO {

    /**
     * 
     */
    @DbKey(value = "id")
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 商品ID
     */
    @DbField(value = "product_id")
    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    /**
     * 商品单价
     */
    @DbField(value = "price")
    @ApiModelProperty(value = "商品单价")
    private BigDecimal price;

    /**
     * 购买数量
     */
    @DbField(value = "count")
    @ApiModelProperty(value = "购买数量")
    private Integer count;

    /**
     * 消费者ID
     */
    @DbField(value = "consumer_id")
    @ApiModelProperty(value = "消费者ID")
    private Integer consumerId;

    /**
     * 下单时间
     */
    @DbField(value = "order_time")
    @ApiModelProperty(value = "下单时间")
    private Integer orderTime;

    /**
     * 创建时间
     */
    @DbField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Integer createTime;

    /**
     * 修改时间
     */
    @DbField(value = "update_time")
    @ApiModelProperty(value = "修改时间")
    private Integer updateTime;

    /**
     * 状态：0-正常，1-已删除
     */
    @DbField(value = "state")
    @ApiModelProperty(value = "状态：0-正常，1-已删除")
    private Integer state;



}