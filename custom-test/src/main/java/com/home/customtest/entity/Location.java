package com.home.customtest.entity;

import com.custom.action.annotations.DbField;
import com.custom.action.annotations.DbKey;
import com.custom.action.annotations.DbTable;
import lombok.Data;

/**
 * @author Xiao-Bai
 * @date 2021/12/3 20:12
 * @desc:
 */
@Data
@DbTable(table = "location")
public class Location {

    @DbKey
    private Integer id;

    @DbField
    private String name;

    @DbField("city_id")
    private Integer cityId;

}
