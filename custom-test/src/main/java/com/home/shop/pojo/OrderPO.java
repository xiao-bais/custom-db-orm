package com.home.shop.pojo;

 import com.custom.comm.annotations.DbField;
 import com.custom.comm.annotations.DbKey;
 import com.custom.comm.annotations.DbTable;
 import lombok.Data;
 import io.swagger.annotations.ApiModelProperty;
 import com.custom.comm.enums.KeyStrategy;

import java.math.BigDecimal;

@Data
@DbTable(table = "shop_order")
public class OrderPO {

    /**
     * 
     */
    @DbKey(value = "id")
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 订单号
     */
    @DbField(value = "order_num")
    @ApiModelProperty(value = "订单号")
    private String orderNum;

    /**
     * 商品ID
     */
    @DbField(value = "product_id")
    @ApiModelProperty(value = "商品ID")
    private String productId;

    /**
     * 消费者ID
     */
    @DbField(value = "consumer_id")
    @ApiModelProperty(value = "消费者ID")
    private Integer consumerId;

    /**
     * 商品种类ID
     */
    @DbField(value = "category_id")
    @ApiModelProperty(value = "商品种类ID")
    private String categoryId;

    /**
     * 下单数量
     */
    @DbField(value = "pro_count")
    @ApiModelProperty(value = "下单数量")
    private Integer proCount;

    /**
     * 商品单价
     */
    @DbField(value = "price")
    @ApiModelProperty(value = "商品单价")
    private BigDecimal price;

    /**
     * 最终成交价
     */
    @DbField(value = "deal_price")
    @ApiModelProperty(value = "最终成交价")
    private BigDecimal dealPrice;

    /**
     * 下单时间
     */
    @DbField(value = "order_time")
    @ApiModelProperty(value = "下单时间")
    private Integer orderTime;

    /**
     * 是否是退单？ 0-否，1-是
     */
    @DbField(value = "refund_flag")
    @ApiModelProperty(value = "是否是退单？ 0-否，1-是")
    private Boolean refundFlag;

    /**
     * 消费者寄送地址
     */
    @DbField(value = "address")
    @ApiModelProperty(value = "消费者寄送地址")
    private String address;

    /**
     * 退款原因
     */
    @DbField(value = "refund_reason")
    @ApiModelProperty(value = "退款原因")
    private String refundReason;

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