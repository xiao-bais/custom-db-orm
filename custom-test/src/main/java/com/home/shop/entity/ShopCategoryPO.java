package com.home.shop.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author  Xiao-Bai
 * @since  2021/10/25
 * @Description 产品种类表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@DbTable(value = "shop_category")
public class ShopCategoryPO extends BaseEntity {

    @DbKey
    private int id;

    @DbField(desc = "产品种类名称")
    private String name;

    @DbField(desc = "种类说明")
    private String categoryDesc;

    @DbField(desc = "父级种类ID")
    private int parentId;

    @DbField(desc = "种类样式图")
    private String image;

    private List<ShopCategoryPO> children;


}
