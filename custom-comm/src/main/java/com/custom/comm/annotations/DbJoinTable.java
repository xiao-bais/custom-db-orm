package com.custom.comm.annotations;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/19
 * @Description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbJoinTable {

    /**
     * 关联条件
     * example：‘left join employee emp on emp.id = a.emp_id’
     */
    String value();
}
