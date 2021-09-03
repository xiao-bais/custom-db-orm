package com.custom.annotations;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/6/28
 * @Description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbTable {

    /**
     * 表名称
     * @return
     */
    String table() default "";

    /**
     * 指定表的别名
     * @return
     */
    String alias() default "a";


}
