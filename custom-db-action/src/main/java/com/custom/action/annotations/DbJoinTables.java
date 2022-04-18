package com.custom.action.annotations;

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
public @interface DbJoinTables {

    /**
    * 关联条件组
    */
    DbJoinTable[] value();
}
