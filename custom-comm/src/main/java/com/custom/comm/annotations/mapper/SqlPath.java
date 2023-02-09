package com.custom.comm.annotations.mapper;

import com.custom.comm.enums.ExecuteMethod;

import java.lang.annotation.*;

/**
 * 适用于dao层接口方法上使用
 * @author Xiao-Bai
 * @since 2021/11/25 15:33
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
    
}
