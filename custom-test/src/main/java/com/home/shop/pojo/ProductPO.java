package com.home.shop.pojo;

 import com.custom.comm.annotations.DbField;
 import com.custom.comm.annotations.DbKey;
 import com.custom.comm.annotations.DbTable;
 import lombok.Data;
 import io.swagger.annotations.ApiModelProperty;
 import com.custom.comm.enums.KeyStrategy;

import java.math.BigDecimal;

/**
 * @Author Xiao-Bai
 * @Date 2022-04-23 22:11
 */

@Data
@DbTable(table = "shop_product")
public class ProductPO {

    /**
     * 
     */
    @DbKey
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 商品名称
     */
    @DbField
    @ApiModelProperty(value = "商品名称")
    private String name;

    /**
     * 商品说明
     */
    @DbField
    @ApiModelProperty(value = "商品说明")
    private String explain;

    /**
     * 上级种类ID
     */
    @DbField
    @ApiModelProperty(value = "上级种类ID")
    private Integer parentCategoryId;

    /**
     * 种类ID
     */
    @DbField
    @ApiModelProperty(value = "种类ID")
    private Integer categoryId;

    /**
     * 原价
     */
    @DbField
    @ApiModelProperty(value = "原价")
    private BigDecimal price;

    /**
     * 折扣价
     */
    @DbField
    @ApiModelProperty(value = "折扣价")
    private BigDecimal discountPrice;

    /**
     * 库存量
     */
    @DbField
    @ApiModelProperty(value = "库存量")
    private Integer reserve;

    /**
     * 是否上架？ 0-下架，1-上架
     */
    @DbField
    @ApiModelProperty(value = "是否上架？ 0-下架，1-上架")
    private Boolean shelvesFlag;

    /**
     * 最后上架时间
     */
    @DbField
    @ApiModelProperty(value = "最后上架时间")
    private Integer lastShelvesTime;

    /**
     * 商品样式图
     */
    @DbField
    @ApiModelProperty(value = "商品样式图")
    private String coverImage;

    /**
     * 创建人
     */
    @DbField
    @ApiModelProperty(value = "创建人")
    private Integer createId;

    /**
     * 创建时间
     */
    @DbField
    @ApiModelProperty(value = "创建时间")
    private Integer createTime;

    /**
     * 修改时间
     */
    @DbField
    @ApiModelProperty(value = "修改时间")
    private Integer updateTime;

    /**
     * 状态：0-正常，1-已删除
     */
    @DbField
    @ApiModelProperty(value = "状态：0-正常，1-已删除")
    private Integer state;



}