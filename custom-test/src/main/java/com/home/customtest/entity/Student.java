package com.home.customtest.entity;

import com.custom.annotations.*;
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
@DbTable(table = "student")
@DbJoinTables({
        @DbJoinTable("left join province pro on pro.id = a.pro_id"),
        @DbJoinTable("left join city cy on cy.id = a.city_id"),
        @DbJoinTable("left join location lo on lo.id = a.area_id")
})
public class Student {

    @DbKey
    private Integer id;

    @DbField
    private String name;

    @DbField("nick_code")
    private String nickName;

    @DbField
    private String password;

    @DbField
    private Integer age;

    @DbField
    private Boolean sex;

    @DbField
    private String phone;

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

    @DbMapper("lo.name")
    private String area;

    private Integer sumAge;
    private Integer ifNullAge;
    private Integer countAge;
    private Integer minAge;
    private Integer maxAge;
    private Integer avgAge;

    private List<Street> modelList;

}
