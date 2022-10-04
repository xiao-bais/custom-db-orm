package com.custom.comm.annotations.mapper;

import java.lang.annotation.*;

/**
 * @author Xiao-Bai
 * @date 2022/6/5 23:47
 * @desc:适用于Dao层的参数注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbParam {

    /**
     * parameter name is value
     */
    String value();
}
