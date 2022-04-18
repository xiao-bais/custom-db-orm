package com.home.customtest.entity;

import com.custom.action.annotations.DbField;
import com.custom.action.annotations.DbKey;
import com.custom.action.annotations.DbTable;
import lombok.Data;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/10 9:56
 * @Desc：
 **/
@Data
@DbTable(table = "street")
public class Street {

    @DbKey
    private Integer id;

    @DbField
    private String name;

    @DbField("area_id")
    private Integer areaId;
}
