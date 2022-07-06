package com.custom.comm.annotations;

import com.custom.comm.SymbolConstant;
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
@Inherited
public @interface DbField {

    /**
     * 字段名称
     * @return value
     */
    String value() default "";

    /**
     * 数据类型
     * @return dataType
     */
    DbType dataType() default DbType.DbVarchar;

    /**
     * 字段说明
     * @return desc
     */
    String desc() default SymbolConstant.EMPTY;

    /**
     * 默认值，在创建表或者插入新记录时会附带自定义的默认值
     * <br/> (若不想逐个设定，可由{@link DbTable#enabledDefaultValue()}开启给定的默认值)
     * <p>
     *     若默认值是int、long、double、decimal, float 等之类的数字类型，则自定义即可
     *     若默认值是 boolean类型，则直接以字符串的true、false即可 (不区分大小写)
     * </p>
     * <p>
     *     注意：若给定的默认值是abc这样的字符串，而java属性或sql字段类型为int类型之类的，则会无法解析的，并抛出异常
     * </p>
     * @return defaultValue
     */
    String defaultValue() default SymbolConstant.EMPTY;

    /**
     * 是否为空
     * @return isNull
     */
    boolean isNull() default true;

    /**
     * 查询时若当前sql字段为字符类型，是否null转为空字符
     * <p>
     * 若使用了条件构造器的Select方法，则isNullToEmpty不会生效
     * </p>
     * @return isNullToEmpty
     */
    boolean isNullToEmpty() default false;

    /**
     * 查询时，指定查询sql字段的包装
     * 例：concat('user-', tp.name) columnName
     * <p>
     * 若使用了条件构造器的Select方法，则wrapperColumn不会生效
     * </p>
     * @return wrapperColumn
     */
    String wrapperColumn() default SymbolConstant.EMPTY;


}
