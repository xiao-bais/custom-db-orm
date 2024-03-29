package com.custom.comm.annotations.mapper;

import java.lang.annotation.*;

/**
 * 适用于dao层动态代理方法上使用
 * @author  Xiao-Bai
 * @since 2021/11/19 16:51
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Query {

    /**
    * 示例：select * from tableName
    */
    String value();

    /**
     * <li>true：参数以放置顺序来匹配sql中'?'的位置</li>
     * <li>false: 以参数的名称来替换sql中对应的参数名称(例如：以 name 将 #{name} 替换成 '?')</li>
     * <li>两者不可同时作用在一条sql上</li>
    */
    boolean order() default false;

}
