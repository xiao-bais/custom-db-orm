package com.home.shop.pojo;

 import com.custom.comm.annotations.DbField;
 import com.custom.comm.annotations.DbKey;
 import com.custom.comm.annotations.DbTable;
 import lombok.Data;
 import io.swagger.annotations.ApiModelProperty;
 import com.custom.comm.enums.KeyStrategy;


/**
 * @author  Xiao-Bai
 * @since  2022-12-16 15:11
 */

@Data
@DbTable(table = "shop_category")
public class CategoryPO {

    /**
     * 
     */
    @DbKey
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 产品种类名称
     */
    @DbField
    @ApiModelProperty(value = "产品种类名称")
    private String name;

    /**
     * 种类说明
     */
    @DbField
    @ApiModelProperty(value = "种类说明")
    private String categoryDesc;

    /**
     * 父级种类ID
     */
    @DbField
    @ApiModelProperty(value = "父级种类ID")
    private Integer parentId;

    /**
     * 种类样式图
     */
    @DbField
    @ApiModelProperty(value = "种类样式图")
    private String image;

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