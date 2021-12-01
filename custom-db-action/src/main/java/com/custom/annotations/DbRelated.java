package com.custom.annotations;

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
    String joinStyle() default "left join";

    /**
     * 注入的字段
     * @return
     */
    String field();


}
