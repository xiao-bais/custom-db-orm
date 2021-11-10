package com.custom.test;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/28 9:19
 * @Desc：
 **/
@DbTable(table = "employee")
@Data
@ToString
public class Employee {

    @DbKey
    private int id;

    @DbField(value = "emp_name", desc = "员工姓名")
    private String empName;

    private String pppName;


    @DbField(desc = "姓名")
    private boolean sex;

    @DbField(desc = "员工年龄")
    private int age;

    @DbField(desc = "员工地址")
    private int address;

    @DbField(desc = "生日")
    private Date birthday;

    @DbField
    private int state;


}
