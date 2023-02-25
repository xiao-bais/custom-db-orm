package com.home.customtest.entity;

import com.custom.comm.annotations.*;
import com.custom.comm.enums.MultiStrategy;
import lombok.Data;

import java.util.List;

/**
 * @author  Xiao-Bai
 * @since  2022/3/10 9:53
 * @Desc：
 **/
@Data
@DbTable(value = "province")
public class Province {

    @DbKey
    private Integer id;

    @DbField
    private String name;

//    @DbOneToMany(joinField = "proId", sortField = "age", strategy = MultiStrategy.ERROR)
//    private List<Student> students;

    @DbOneToMany(joinField = "provinceId", strategy = MultiStrategy.RECURSION)
    private List<City> cityList;

//    @DbOneToMany(joinField = "proId", sortField = "age", strategy = MultiStrategy.RECURSION)
//    private List<Student> students2;


}
