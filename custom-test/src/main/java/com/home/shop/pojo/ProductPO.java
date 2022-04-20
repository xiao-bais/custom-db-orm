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

@DbTable(table = "shop_product")
public class ProductPO {

    /**
     * 
     */
    @DbKey(value = "id")
    private Integer id;

    /**
     * 商品名称
     */
    @DbField(value = "name")
    private String name;

    /**
     * 商品说明
     */
    @DbField(value = "explain")
    private String explain;

    /**
     * 上级种类ID
     */
    @DbField(value = "parent_category_id")
    private Integer parentCategoryId;

    /**
     * 种类ID
     */
    @DbField(value = "category_id")
    private Integer categoryId;

    /**
     * 原价
     */
    @DbField(value = "price")
    private BigDecimal price;

    /**
     * 折扣价
     */
    @DbField(value = "discount_price")
    private BigDecimal discountPrice;

    /**
     * 库存量
     */
    @DbField(value = "reserve")
    private Integer reserve;

    /**
     * 是否上架？ 0-下架，1-上架
     */
    @DbField(value = "shelves_flag")
    private Boolean shelvesFlag;

    /**
     * 最后上架时间
     */
    @DbField(value = "last_shelves_time")
    private Integer lastShelvesTime;

    /**
     * 商品样式图
     */
    @DbField(value = "cover_image")
    private String coverImage;

    /**
     * 创建人
     */
    @DbField(value = "create_id")
    private Integer createId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Integer getReserve() {
        return reserve;
    }

    public void setReserve(Integer reserve) {
        this.reserve = reserve;
    }

    public Boolean getShelvesFlag() {
        return shelvesFlag;
    }

    public void setShelvesFlag(Boolean shelvesFlag) {
        this.shelvesFlag = shelvesFlag;
    }

    public Integer getLastShelvesTime() {
        return lastShelvesTime;
    }

    public void setLastShelvesTime(Integer lastShelvesTime) {
        this.lastShelvesTime = lastShelvesTime;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Integer getCreateId() {
        return createId;
    }

    public void setCreateId(Integer createId) {
        this.createId = createId;
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