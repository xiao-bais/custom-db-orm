package com.custom.comm.annotations;

import java.lang.annotation.*;

/**
 * 表关联映射字段注解
 * @author  Xiao-Bai
 * @since 2021/10/20
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbJoinField {

    /**
     * 关联表映射的字段 例如：example：‘left join employee emp on emp.id = a.emp_id’
     * <br/>那么value = emp.name
     */
    String value();

}
