package com.home.customtest.entity;

import com.custom.comm.annotations.*;
import lombok.Data;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/10 9:53
 * @Desc：
 **/
@Data
@DbTable(table = "province")
public class Province {

    @DbKey
    private Integer id;

    @DbField
    private String name;

    @DbOneToMany(joinField = "proId")
    private List<Student> students;


}
