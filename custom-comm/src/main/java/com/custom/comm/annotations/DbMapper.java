package com.custom.comm.annotations;

import com.custom.comm.SymbolConstant;

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
public @interface DbMapper {

    /**
     * 关联表映射的字段 例如：example：‘left join employee emp on emp.id = a.emp_id’
     * value=emp.name
     */
    String value();

    /**
     * 查询时若当前字段为字符类型，是否null转为空字符
     * <p>
     * 若使用了条件构造器的Select方法，则isNullToEmpty不会生效
     * </p>
     * @return isNullToEmpty
     */
    boolean isNullToEmpty() default false;

    /**
     * 查询时，指定查询字段的包装
     * 例：concat('user-', tp.name) columnName
     * <p>
     * 若使用了条件构造器的Select方法，则wrapperColumn不会生效
     * </p>
     * @return wrapperColumn
     */
    String wrapperColumn() default SymbolConstant.EMPTY;
}
