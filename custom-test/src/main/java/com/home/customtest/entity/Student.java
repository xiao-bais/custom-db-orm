package com.home.customtest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.custom.action.activerecord.ActiveModel;
import com.custom.comm.annotations.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author  Xiao-Bai
 * @since  2022/3/10 10:00
 * @Desc：
 **/

@EqualsAndHashCode(callSuper = true)
@Data
@DbTable(table = "student", desc = "学生信息表")
@TableName("student")
@DbJoinTable("left join province pv on pv.id = a.pro_id")
public class Student extends ActiveModel<Student, Integer> {

    private Boolean sex;

    private String phone;

    @DbKey
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

//    @DbOneToOne(thisField = "proId", strategy = MultiStrategy.RECURSION)
//    private Province province;

    @DbField("nick_code")
    private String nickName;

    private String password;

//    @DbRelated(joinTable = "employee", joinAlias = "emp", condition = "emp.id = a.id", field = "emp_name")
//    private String proName;


    private Integer age;


    private BigDecimal money;


    private String address;

    @DbField("birthday")
    private Date birth;


    private Integer state;


    private Integer proId;


    private Integer cityId;


    private Integer areaId;

    @DbField(exist = false)
    private String city;

    @DbField(exist = false)
    private String area;

    @DbNotField
    private List<Street> modelList;

    @DbJoinField("pv.name")
    private String provinceName;





}
