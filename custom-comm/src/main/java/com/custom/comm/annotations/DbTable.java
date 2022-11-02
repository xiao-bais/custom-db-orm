package com.custom.comm.annotations;

import com.custom.comm.utils.Constants;

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
     * 子类是否允许合并父类的关联条件以及映射的关联字段同时标注
     *  {@link DbJoinTables}, {@link DbJoinTable}, {@link DbRelated}
     *  <br/> true => 关联顺序按照父类最先关联，一层一层往子类关联
     *  <br/> false => 不会合并父类的关联条件，并且将{@link DbRelated#field()}, {@link DbMapper} 标注的字段设置为普通字段
     *  <br/> 普通字段等同于 {@link DbField#exist()} = false 或者 {@link DbNotField}
     * @return mergeSuperJoin
     */
    boolean mergeSuperJoin() default true;


}
