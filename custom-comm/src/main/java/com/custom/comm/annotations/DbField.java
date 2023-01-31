package com.custom.comm.annotations;

import com.custom.comm.utils.Constants;
import com.custom.comm.enums.DbType;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/6/30
 * @Description
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbField {

    /**
     * 字段名称
     * @return value
     */
    String value() default Constants.EMPTY;

    /**
     * 数据类型
     * @return dataType
     */
    DbType dataType() default DbType.DbVarchar;


    /**
     * 字段说明
     * @return desc
     */
    String desc() default Constants.EMPTY;


    /**
     * 是否为空，只在创建表的时候用到
     * @return isNull
     */
    boolean isNull() default true;


    /**
     * 是否存在该表字段，作用与{@link DbNotField}一致
     */
    boolean exist() default true;


}
