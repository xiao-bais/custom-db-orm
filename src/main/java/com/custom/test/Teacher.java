package com.custom.test;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/20
 * @Description
 */
@DbTable
public class Teacher {

    @DbKey
    private int id;

    @DbField
    private String name;

    @DbField
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
