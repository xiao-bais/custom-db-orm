package com.custom.annotations;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/20
 * @Description
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbMap {

    /**
     * 映射的字段 例如：a.name
     */
    String value();
}
