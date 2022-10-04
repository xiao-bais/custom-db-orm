package com.home.customtest.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author Xiao-Bai
 * @date 2022/10/4 12:33
 * @desc
 */
@ToString
@Getter
@Setter
@DbTable(table = "aklis")
public class Aklis {

    @DbKey
    private Integer id;

    private String name;

    private String nickCode;

    private String password;

    private int age;

    private String sex;

    private String phone;

    @DbField("address")
    private String addr;

    private Date birthday;

    private Integer state;

    private Integer createTime;

    private Integer updateTime;

    private String  explain;


}
