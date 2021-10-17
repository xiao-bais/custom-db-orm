package com.custom.test;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;

import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/17
 * @Description
 */
@DbTable
public class Person {

    @DbKey
    private int id;
    @DbField
    private String name;
    @DbField
    private int age;
    @DbField
    private boolean sex;
    @DbField
    private Date birthday;
    @DbField
    private String explain;

    public Person() {
    }

    public Person(String name, int age, boolean sex, Date birthday, String explain) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.birthday = birthday;
        this.explain = explain;
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

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", birthday=" + birthday +
                ", explain='" + explain + '\'' +
                '}';
    }
}
