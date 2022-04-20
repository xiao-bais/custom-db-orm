package com.home.shop.pojo;

 import com.custom.comm.annotations.DbField;
 import com.custom.comm.annotations.DbKey;
 import com.custom.comm.annotations.DbTable;
 import com.custom.comm.enums.KeyStrategy;

import java.math.BigDecimal;

/**
 * @Author Xiao-Bai
 *
 */

@DbTable(table = "shop_order")
public class OrderPO {

    /**
     * 
     */
    @DbKey(value = "id")
    private Integer id;

    /**
     * 订单号
     */
    @DbField(value = "order_num")
    private String orderNum;

    /**
     * 商品ID
     */
    @DbField(value = "product_id")
    private String productId;

    /**
     * 消费者ID
     */
    @DbField(value = "consumer_id")
    private Integer consumerId;

    /**
     * 商品种类ID
     */
    @DbField(value = "category_id")
    private String categoryId;

    /**
     * 下单数量
     */
    @DbField(value = "pro_count")
    private Integer proCount;

    /**
     * 商品单价
     */
    @DbField(value = "price")
    private BigDecimal price;

    /**
     * 最终成交价
     */
    @DbField(value = "deal_price")
    private BigDecimal dealPrice;

    /**
     * 下单时间
     */
    @DbField(value = "order_time")
    private Integer orderTime;

    /**
     * 是否是退单？ 0-否，1-是
     */
    @DbField(value = "refund_flag")
    private Boolean refundFlag;

    /**
     * 消费者寄送地址
     */
    @DbField(value = "address")
    private String address;

    /**
     * 退款原因
     */
    @DbField(value = "refund_reason")
    private String refundReason;

    /**
     * 创建时间
     */
    @DbField(value = "create_time")
    private Integer createTime;

    /**
     * 修改时间
     */
    @DbField(value = "update_time")
    private Integer updateTime;

    /**
     * 状态：0-正常，1-已删除
     */
    @DbField(value = "state")
    private Integer state;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Integer consumerId) {
        this.consumerId = consumerId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getProCount() {
        return proCount;
    }

    public void setProCount(Integer proCount) {
        this.proCount = proCount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public Integer getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Integer orderTime) {
        this.orderTime = orderTime;
    }

    public Boolean getRefundFlag() {
        return refundFlag;
    }

    public void setRefundFlag(Boolean refundFlag) {
        this.refundFlag = refundFlag;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }


}