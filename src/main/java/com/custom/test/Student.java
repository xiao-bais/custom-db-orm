package com.custom.test;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;

import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/8/29
 * @Description
 */
@DbTable(table = "student")
public class Student {

    @DbKey
    private int id;

    @DbField
    private String name;

    @DbField
    private int age;

    @DbField
    private Date birthday;

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

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
