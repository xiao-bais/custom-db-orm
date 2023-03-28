package com.home.customtest.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import lombok.Data;

import java.util.List;

/**
 * @author  Xiao-Bai
 * @since  2022/3/10 9:55
 * @Desc：
 **/
@Data
@DbTable(value = "city")
public class City {

    @DbKey
    private Integer id;

    @DbField
    private String name;

    @DbField("province_id")
    private Integer provinceId;

    private List<Location> locationList;
}
