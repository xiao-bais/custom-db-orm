package com.custom.annotations;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/19 14:59
 * @Desc：
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbJoinTable {

    /**
    * 关联的sql
    */
    String value();
}
