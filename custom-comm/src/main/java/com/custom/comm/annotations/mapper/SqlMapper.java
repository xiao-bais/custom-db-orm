package com.custom.comm.annotations.mapper;

import java.lang.annotation.*;

/**
 * 作用与继承BasicDao相同，两者选其一
 * @author  Xiao-Bai
 * @since 2021/12/1 23:23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SqlMapper {

    /**
     * 作用与DbDataSource中的属性order一致，以此来确定数据源，主数据源默认为1
     */
    int order() default 1;


}
