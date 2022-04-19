package com.custom.comm.annotations;

import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.DbMediaType;

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
     * 数据类型
     * @return
     */
    DbMediaType dataType() default DbMediaType.DbVarchar;

    /**
     * 字段说明
     * @return
     */
    String desc() default SymbolConstant.EMPTY;

    /**
     * 是否为空
     * @return
     */
    boolean isNull() default true;


}
