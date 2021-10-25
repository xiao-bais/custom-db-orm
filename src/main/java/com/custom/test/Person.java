package com.custom.test;

import com.custom.annotations.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/17
 * @Description
 */
@DbTable(table = "student")
//@DbJoinTables({
//        @DbJoinTable("left join classes cls on cls.clsId = a.cls_id"),
//        @DbJoinTable("left join leader t on t.id = a.teach_id"),
//})
@Data
public class Person {

    @DbKey("stu_id")
    private int stuId;
    @DbField("stu_name")
    private String name;
    @DbField("stu_age")
    private int age;
    @DbField("stu_sex")
    private boolean sex;
    @DbField("stu_birth")
    private Date birthDay;
//    @DbField
    private String explain;
//    @DbField("cls_id")
    private int clsId;
//    @DbField("teach_id")
    private int teachId;


//    @DbMap("cls.clsName")
//    @DbRelated(joinTable = "classes", joinAlias = "cls", field = "clsName", condition = "cls.clsId = a.cls_id")
    private String className;
//    @DbMap("t.leader_name")
//    @DbRelated(joinTable = "teacher", joinAlias = "t", field = "name", condition = "t.id = a.teach_id")
    private String leaderName;



    @Override
    public String toString() {
        return "Person{" +
                "stuId=" + stuId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", birthDay=" + birthDay +
                ", explain='" + explain + '\'' +
                ", clsId=" + clsId +
                ", teachId=" + teachId +
                ", className='" + className + '\'' +
                ", leaderName='" + leaderName + '\'' +
                '}';
    }
}

