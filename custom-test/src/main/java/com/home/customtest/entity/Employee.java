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
        @DbJoinTable("left join emp_dept dept on dept.id = a.dept_id"),
        @DbJoinTable("left join emp_dept dept1 on dept.id = a.dept_id"),
        @DbJoinTable("left join emp_dept dept2 on dept.id = a.dept_id"),
        @DbJoinTable("left join emp_dept dept3 on dept.id = a.dept_id"),
        @DbJoinTable("left join emp_dept dept4 on dept.id = a.dept_id"),
        @DbJoinTable("left join emp_dept dept5 on dept.id = a.dept_id"),
})
public class Employee {


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

    @DbField
    private int state;

    @DbMap("dept.name")
    private String deptName2;

    @DbRelated(joinTable = "emp_dept", joinAlias = "dept", condition = "dept.id = a.dept_id", field = "dept.name")
    private String dpetName;


}
