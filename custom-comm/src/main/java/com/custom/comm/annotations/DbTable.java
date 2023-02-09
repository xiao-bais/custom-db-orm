package com.custom.comm.annotations;

import com.custom.comm.utils.Constants;

import java.lang.annotation.*;

/**
 * 表注解，表示这是一个表对应的实体
 * @author  Xiao-Bai
 * @since 2021/6/28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbTable {

    /**
     * 表名称
     * @return table
     */
    String table();


    /**
     * 指定表的别名
     * @return alias
     */
    String alias() default "a";


    /**
     * 指定表的说明
     * @return desc
     */
    String desc() default Constants.EMPTY;


    /**
     * 若存在动态数据源，则指定该值与dataSource中的order一致即可
     * @return
     */
    int order() default 1;


}
