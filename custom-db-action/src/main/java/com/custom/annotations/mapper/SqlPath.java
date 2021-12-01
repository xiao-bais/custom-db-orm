package com.custom.annotations.mapper;

import com.custom.enums.ExecuteMethod;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/25 15:33
 * @Desc：
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SqlPath {

    /**
    * sql文件的路径：sql/test.sql
    */
    String value();
    
    /**
    * 执行的类型（增，删，改，查）
    */
    ExecuteMethod method() default ExecuteMethod.SELECT;

    /**
     * true：参数以放置顺序来匹配sql中'?'的位置
     * false: 以参数的名称来替换sql中对应的参数名称（例如：以 name 将 #{name} 替换成 '?'）
     * 两者不可同时作用在一条sql上
     */
    boolean isOrder() default false;
    
}
