package com.custom.annotations;

import com.custom.enums.FillStrategy;
import com.custom.enums.DbMediaType;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/6/30
 * @Description
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbField {

    /**
     * 字段名称
     * @return
     */
    String value() default "";

    /**
     * 字段类型
     * @return
     */
    DbMediaType fieldType() default DbMediaType.DbVarchar;

    /**
     * 字段说明
     * @return
     */
    String desc() default "";

    /**
     * 是否为空
     * @return
     */
    boolean isNull() default true;


}
