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

@DbTable(table = "shop_cart")
public class CartPO {

    /**
     * 
     */
    @DbKey(value = "id")
    private Integer id;

    /**
     * 商品ID
     */
    @DbField(value = "product_id")
    private Integer productId;

    /**
     * 商品单价
     */
    @DbField(value = "price")
    private BigDecimal price;

    /**
     * 购买数量
     */
    @DbField(value = "count")
    private Integer count;

    /**
     * 消费者ID
     */
    @DbField(value = "consumer_id")
    private Integer consumerId;

    /**
     * 下单时间
     */
    @DbField(value = "order_time")
    private Integer orderTime;

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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Integer consumerId) {
        this.consumerId = consumerId;
    }

    public Integer getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Integer orderTime) {
        this.orderTime = orderTime;
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