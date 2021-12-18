package com.home.customtest.entity;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/27 15:37
 * @Desc：
 **/
@DbTable(table = "employee_temp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTemp {


    @DbKey
    private int id;

    @DbField("emp_name")
    private String empName;

    @DbField
    private boolean sex;

    @DbField
    private int age;

    @DbField
    private String address;

    @DbField
    private Date birthday;

    @DbField("dept_id")
    private int deptId;

    @DbField("area_id")
    private int areaId;

    @DbField
    private int state;

//    @DbMap("dept.name")
//    private String deptName;

//    @DbRelated(joinTable = "location", joinAlias = "lo", condition = "lo.id = a.area_id", field = "area")
//    private String area1;
//    @DbRelated(joinTable = "location", joinAlias = "lo2", condition = "lo2.id = lo.parent_id", field = "area")
//    private String area2;
//    @DbRelated(joinTable = "location", joinAlias = "lo3", condition = "lo3.id = lo2.parent_id", field = "area")
//    private String area3;




}
