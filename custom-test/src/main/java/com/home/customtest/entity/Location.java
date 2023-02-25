package com.home.customtest.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbOneToMany;
import com.custom.comm.annotations.DbTable;
import com.custom.comm.enums.MultiStrategy;
import lombok.Data;

import java.util.List;

/**
 * @author  Xiao-Bai
 * @since  2021/12/3 20:12
 * @desc:
 */
@Data
@DbTable(value = "location")
public class Location {

    @DbKey
    private Integer id;

    @DbField
    private String name;

    @DbField("city_id")
    private Integer cityId;

    @DbOneToMany(joinField = "areaId", strategy = MultiStrategy.RECURSION)
    private List<Street> streetList;

}
