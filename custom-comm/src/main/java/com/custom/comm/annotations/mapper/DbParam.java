package com.custom.comm.annotations.mapper;

import java.lang.annotation.*;

/**
 * 适用于Dao层的参数注解
 * @author  Xiao-Bai
 * @since 2022/6/5 23:47
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
