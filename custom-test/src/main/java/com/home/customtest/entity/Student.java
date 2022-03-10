package com.home.customtest.entity;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/10 10:00
 * @Desc：
 **/
@Data
@DbTable(table = "student")
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
    private String age;

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

    @DbField("pro_id")
    private Integer proId;

    @DbField("city_id")
    private Integer cityId;

    @DbField("area_id")
    private Integer areaId;

}
