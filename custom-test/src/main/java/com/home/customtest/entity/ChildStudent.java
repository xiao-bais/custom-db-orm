package com.home.customtest.entity;

import com.custom.action.annotations.DbField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Xiao-Bai
 * @date 2022/3/29 22:52
 * @desc:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
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
