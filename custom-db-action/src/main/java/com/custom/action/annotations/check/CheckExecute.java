package com.custom.action.annotations.check;

import com.custom.action.enums.ExecuteMethod;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/17 10:08
 * @Desc：在被注解的方法执行前，进行一系列的参数检查，例如不规范，不合法的参数，或者null
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CheckExecute {

    /**
    * 默认不做处理
    */
    ExecuteMethod target() default ExecuteMethod.NONE;
}
