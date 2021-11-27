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
@DbTable(table = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
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


}
