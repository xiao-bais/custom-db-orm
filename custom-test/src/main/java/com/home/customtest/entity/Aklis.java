package com.home.customtest.entity;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/21 15:52
 * @Desc：
 **/
@Data
@DbTable(table = "aklis")
public class Aklis {

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
    private Integer createTime;
    @DbField
    private Integer updateTime;
}
