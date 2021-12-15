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

    private String eName;

    public List<Integer> getAgeList() {
        return ageList;
    }

    public void setAgeList(List<Integer> ageList) {
        this.ageList = ageList;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }
}
