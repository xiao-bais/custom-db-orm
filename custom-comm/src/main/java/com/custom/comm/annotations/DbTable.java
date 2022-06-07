package com.custom.comm.annotations;

import com.custom.comm.SymbolConstant;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/6/28
 * @Description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbTable {

    /**
     * 表名称
     * <p>
     *     若当前类跟父类同时标注了DbTable注解，且对应的table值不一致时，则不会合并父类的属性字段
     * </p>
     * @return
     */
    String table();

    /**
     * 指定表的别名
     * @return
     */
    String alias() default "a";

    /**
     * 指定表的说明
     * @return
     */
    String desc() default SymbolConstant.EMPTY;

    /**
     * 当子类跟父类同时标注了@DbJoinTable(s)注解时，是否在查询时向上查找父类的@DbJoinTable(s)注解，且合并关联条件
     * @return
     */
    boolean mergeSuperDbJoinTables() default true;

}
