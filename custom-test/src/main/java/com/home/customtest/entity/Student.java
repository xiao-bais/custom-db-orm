package com.home.customtest.entity;

import com.custom.comm.annotations.*;
import com.custom.comm.enums.DbType;
import com.custom.comm.enums.KeyStrategy;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/10 10:00
 * @Desc：
 **/
@Data
@DbJoinTables({
        @DbJoinTable("left join province pro on pro.id = a.pro_id"),
        @DbJoinTable("left join city cy on cy.id = a.city_id"),
})
@DbTable(table = "student_info", desc = "学生信息表", enabledDefaultValue = true)
public class Student {

    @DbKey(value = "id", strategy = KeyStrategy.AUTO, dbType = DbType.DbInt)
    private Integer id;

    @DbField(defaultValue = "abc")
    private String name;

    @DbField
    private Boolean sex;

    @DbField("nick_code")
    private String nickName;

    @DbField
    private String password;

    @DbField(defaultValue = "0")
    private Integer age;

    @DbField
    private BigDecimal money;

    @DbField
    private String address;

    @DbField("birthday")
    private Date birth;

    @DbField
    private Integer state;

    @DbField
    private Integer proId;

    @DbField
    private Integer cityId;

    @DbField
    private Integer areaId;

    @DbMapper("pro.name")
    private String province;

    @DbMapper("cy.name")
    private String city;

    private String area;

    private List<Street> modelList;



}
