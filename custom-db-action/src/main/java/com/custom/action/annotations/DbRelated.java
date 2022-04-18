package com.custom.action.annotations;

import com.custom.action.enums.DbJoinStyle;
import com.custom.comm.SymbolConst;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/17 0017 17:35
 * @Version 1.0
 * @Description DbRelation
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
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
    String field() default SymbolConst.EMPTY;


}
