package com.custom.test;

import com.custom.annotations.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/17
 * @Description
 */
@DbTable(table = "student1")
@DbJoinTables({
        @DbJoinTable(""),
        @DbJoinTable("")
})
public class Person {

    @DbKey
    private int id;
    @DbField
    private String name;
    @DbField("nick_code")
    private String nickCode;
    @DbField
    private String password;
    @DbField
    private boolean sex;
    @DbField
    private BigDecimal money;
    @DbField
    private Date birthday;
    @DbField("dept_Id")
    private int classId;
    @DbRelated(joinTable = "classes", joinAlias = "cls", condition = "cls.cs_id = a.dept_id", field = "cls_name")
    private String clsName;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nickCode='" + nickCode + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", money=" + money +
                ", birthday=" + birthday +
                ", classId=" + classId +
                ", clsName='" + clsName + '\'' +
                '}';
    }

    public Person() {
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

    public String getNickCode() {
        return nickCode;
    }

    public void setNickCode(String nickCode) {
        this.nickCode = nickCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }


}
