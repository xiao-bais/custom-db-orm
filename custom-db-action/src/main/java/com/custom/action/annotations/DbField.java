package com.custom.action.annotations;

import com.custom.action.enums.DbMediaType;
import com.custom.comm.SymbolConst;

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
    String desc() default SymbolConst.EMPTY;

    /**
     * 是否为空
     * @return
     */
    boolean isNull() default true;


}
