package com.custom.comm.annotations;

import com.custom.comm.utils.Constants;
import com.custom.comm.enums.DbJoinStyle;

import java.lang.annotation.*;

/**
 * 表关联注解，用于在属性上标明
 * @author  Xiao-Bai
 * @since 2021/1/17 0017 17:35
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbRelated {

    /**
     * 要关联的表
     * @return
     */
    String joinTable();

    /**
     * 关联表的别名
     * @return
     */
    String joinAlias();

    /**
     * 关联条件
     * @return
     */
    String condition();

    /**
     * 关联方式
     * @return
     */
    DbJoinStyle joinStyle() default DbJoinStyle.LEFT;

    /**
     * 注入的字段
     * @return
     */
    String field() default Constants.EMPTY;


}
