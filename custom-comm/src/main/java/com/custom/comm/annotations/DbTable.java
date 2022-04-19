package com.custom.comm.annotations;

import com.custom.comm.SymbolConst;

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
    String desc() default SymbolConst.EMPTY;


}
