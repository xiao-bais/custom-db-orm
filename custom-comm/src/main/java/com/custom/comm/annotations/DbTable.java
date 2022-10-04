package com.custom.comm.annotations;

import com.custom.comm.Constants;

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
     * @return table
     */
    String table();


    /**
     * 指定表的别名
     * @return alias
     */
    String alias() default "a";


    /**
     * 指定表的说明
     * @return desc
     */
    String desc() default Constants.EMPTY;


    /**
     * 当子类跟父类同时标注了@DbJoinTable(s)注解时，是否在查询时向上查找父类的@DbJoinTable(s)注解，且合并关联条件
     * @return mergeSuper
     */
    boolean mergeSuper() default true;


    /**
     * 是否开启数据库前缀，在操作sql时，生成的sql表名会自动附带数据库前缀
     * <br/> 表 -> 开启前: student, 开启后: hos.student
     */
    boolean enabledDbPrefix() default false;


}
