package com.home.customtest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/10 10:00
 * @Desc：
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@DbTable(table = "student", desc = "学生信息表", enabledDefaultValue = true)
@TableName("student")
public class Student extends ActiveModel<Student, Integer> implements Serializable {

    private Boolean sex;

    private String phone;

    @DbKey
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

//    @DbOneToOne(thisField = "proId", joinTarget = Province.class)
//    private Map<String, Object> province;

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

    @DbMapper("cy.name")
    private String city;

    @DbField(exist = false)
    private String area;

    @DbIgnore
    private List<Street> modelList;





}
