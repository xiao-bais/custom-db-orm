package com.home.customtest.entity;

import com.custom.action.annotations.DbField;
import com.custom.action.annotations.DbKey;
import com.custom.action.annotations.DbTable;
import lombok.Data;

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
    private String address;

    @DbField("birthday")
    private Date birth;

    @DbField
    private Integer state;

    @DbField
    private Integer createTime;
    @DbField
    private Integer updateTime;
    @DbField
    private String explain;
}
