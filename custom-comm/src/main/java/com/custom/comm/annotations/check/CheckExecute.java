package com.custom.comm.annotations.check;

import com.custom.comm.enums.ExecuteMethod;

import java.lang.annotation.*;

/**
 * 在被注解的方法执行前，进行一系列的参数检查，例如不规范，不合法的参数，或者null
 * @author Xiao-Bai
 * @since 2021/11/17 10:08
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckExecute {

    /**
    * 默认不做处理
    */
    ExecuteMethod target() default ExecuteMethod.NONE;
}
