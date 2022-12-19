package com.home.customtest.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbOneToMany;
import com.custom.comm.annotations.DbTable;
import com.custom.comm.enums.MultiStrategy;
import lombok.Data;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/10 9:55
 * @Desc：
 **/
@Data
@DbTable(table = "city")
public class City {

    @DbKey
    private Integer id;

    @DbField
    private String name;

    @DbField("province_id")
    private Integer provinceId;

    @DbOneToMany(joinField = "cityId", strategy = MultiStrategy.RECURSION)
    private List<Location> locationList;
}
