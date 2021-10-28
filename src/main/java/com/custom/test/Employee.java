package com.custom.test;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;

import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/28 9:19
 * @Desc：
 **/
@DbTable(table = "employee")
public class Employee {

    @DbKey
    private int id;

    @DbField(value = "emp_name", desc = "员工姓名")
    private String empName;

    @DbField(desc = "姓名")
    private boolean sex;

    @DbField(desc = "员工年龄")
    private int age;

    @DbField(desc = "员工地址")
    private int address;

    @DbField(desc = "生日")
    private Date birthday;

    public Employee(int id, String empName, boolean sex, int age, int address, Date birthday) {
        this.id = id;
        this.empName = empName;
        this.sex = sex;
        this.age = age;
        this.address = address;
        this.birthday = birthday;
    }

    public Employee() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
