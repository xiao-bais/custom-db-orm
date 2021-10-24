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
@DbJoinTables({
        @DbJoinTable("left join classes cls on cls.clsId = a.cls_id"),
        @DbJoinTable("left join leader t on t.id = a.teach_id"),
})
@Data
public class Person {

    @DbKey
    private int stuId;
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
    private String className;
    @DbMap("t.leader_name")
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

