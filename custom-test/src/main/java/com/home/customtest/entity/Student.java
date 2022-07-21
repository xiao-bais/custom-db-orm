package com.home.customtest.entity;

import com.custom.action.activerecord.ActiveModel;
import com.custom.comm.annotations.*;
import com.custom.comm.enums.DbType;
import com.custom.comm.enums.KeyStrategy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/10 10:00
 * @Desc：
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@DbJoinTables({
        @DbJoinTable("left join province pro on pro.id = a.pro_id"),
        @DbJoinTable("left join city cy on cy.id = a.city_id"),
})
@DbTable(table = "student", desc = "学生信息表", enabledDefaultValue = true)
public class Student extends ActiveModel<Student, Integer> implements Serializable {



    @DbKey
    private Integer id;

    private String name;

    private Boolean sex;

    @DbField("nick_code")
    private String nickName;

//    @DbIgnore
    private String password;


    private Integer age;


    private BigDecimal money;


    private String address;

    @DbField("birthday")
    private Date birth;


    private Integer state;


    private Integer proId;


    private Integer cityId;


    private Integer areaId;

    @DbMapper("pro.name")
    private String province;

    @DbMapper("cy.name")
    private String city;

    @DbIgnore
    private String area;

    @DbIgnore
    private List<Street> modelList;





}
