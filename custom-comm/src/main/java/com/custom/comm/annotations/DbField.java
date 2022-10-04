package com.custom.comm.annotations;

import com.custom.comm.Constants;
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
    String desc() default Constants.EMPTY;


    /**
     * 是否为空，只在创建表的时候用到
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
    String wrapperColumn() default Constants.EMPTY;


    /**
     * 是否存在该表字段，作用与{@link DbIgnore}一致
     */
    boolean exist() default true;


}
