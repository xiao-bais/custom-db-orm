package com.custom.comm.annotations;

import java.lang.annotation.*;

/**
 * @author  Xiao-Bai
 * @since 2021/10/19 14:59
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbJoinTables {

    /**
    * 关联条件组
    */
    DbJoinTable[] value();
}
