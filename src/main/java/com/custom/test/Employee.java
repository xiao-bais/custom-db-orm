package com.custom.test;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import com.custom.enums.KeyStrategy;

import java.math.BigDecimal;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/7
 * @Description
 */
@DbTable(table = "one_employee")
public class Employee {

    @DbKey(strategy = KeyStrategy.UUID)
    private String key;

    @DbField("emp_name")
    private String empName;

    @DbField("emp_sex")
    private boolean empSex;

    @DbField("emp_age")
    private int empAge;

    @DbField
    private BigDecimal money;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public boolean isEmpSex() {
        return empSex;
    }

    public void setEmpSex(boolean empSex) {
        this.empSex = empSex;
    }

    public int getEmpAge() {
        return empAge;
    }

    public void setEmpAge(int empAge) {
        this.empAge = empAge;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
