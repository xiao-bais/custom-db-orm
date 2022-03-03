package com.home.customtest.entity;

import com.custom.annotations.*;
import com.custom.enums.DbMediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/27 15:37
 * @Desc：
 **/
@DbTable(table = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DbJoinTables({
        @DbJoinTable("left join dept dept on dept.id = a.dept_id"),
})
public class Employee {


    @DbKey
    private Integer id;

    @DbField("emp_name")
    private String empName;

    @DbField
    private boolean sex;

    @DbField
    private Integer age;

    @DbField
    private String address;

    @DbField
    private Date birthday;

    @DbField("dept_id")
    private Integer deptId;

    @DbField("area_id")
    private Integer areaId;

    @DbField
    private int state;

    @DbMap("dept.name")
    private String deptName;

//    @DbRelated(joinTable = "location", joinAlias = "lo", condition = "lo.id = a.area_id", field = "area")
//    private String area1;
//    @DbRelated(joinTable = "location", joinAlias = "lo2", condition = "lo2.id = lo.parent_id", field = "area")
//    private String area2;
//    @DbRelated(joinTable = "location", joinAlias = "lo3", condition = "lo3.id = lo2.parent_id", field = "area")
//    private String area3;




}
