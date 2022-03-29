package com.home.customtest.entity;

import com.custom.annotations.DbField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Xiao-Bai
 * @date 2022/3/29 22:52
 * @desc:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChildStudent extends Student {


    @DbField
    private Boolean sex;

    @DbField
    private String phone;

    private Integer sumAge;
    private Integer ifNullAge;
    private Integer countAge;
    private Integer minAge;
    private Integer maxAge;
    private Integer avgAge;

}
