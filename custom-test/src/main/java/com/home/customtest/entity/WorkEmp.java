package com.home.customtest.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2021/12/15 21:34
 * @desc:
 */
public class WorkEmp {

    private List<Integer> ageList = new ArrayList<>();

    private String empName;

    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Integer> getAgeList() {
        return ageList;
    }

    public void setAgeList(List<Integer> ageList) {
        this.ageList = ageList;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }
}
