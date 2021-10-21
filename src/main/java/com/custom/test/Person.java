package com.custom.test;

import com.custom.annotations.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/17
 * @Description
 */
@DbTable(table = "student")
@DbJoinTables({
        @DbJoinTable("left join classes cls on cls.clsId = a.cls_id"),
        @DbJoinTable("left join teacher t on t.id = a.teach_id"),
})
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
    private Date birthDay;
    @DbField
    private String explain;
    @DbField("cls_id")
    private int clsId;
    @DbField("teach_id")
    private int teachId;


    @DbMap("cls.clsName")
//    @DbRelated(joinTable = "classes", joinAlias = "cls", field = "clsName", condition = "cls.clsId = a.cls_id")
    private String clsName;
    @DbMap("t.name")
//    @DbRelated(joinTable = "teacher", joinAlias = "t", field = "name", condition = "t.id = a.teach_id")
    private String teachName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClsId() {
        return clsId;
    }

    public void setClsId(int clsId) {
        this.clsId = clsId;
    }

    public int getTeachId() {
        return teachId;
    }

    public void setTeachId(int teachId) {
        this.teachId = teachId;
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

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public String getTeachName() {
        return teachName;
    }

    public void setTeachName(String teachName) {
        this.teachName = teachName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", birthDay=" + birthDay +
                ", explain='" + explain + '\'' +
                ", clsName='" + clsName + '\'' +
                ", teachName='" + teachName + '\'' +
                '}';
    }
}

