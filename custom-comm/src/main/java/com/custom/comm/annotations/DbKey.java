package com.custom.comm.annotations;

import com.custom.comm.enums.DbType;
import com.custom.comm.enums.KeyStrategy;

import java.lang.annotation.*;

/**
 * 主键注解
 * @author Xiao-Bai
 * @since 2021/1/10 0010 16:25
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbKey {

    /**
     * 主键
     * @return
     */
    String value() default "";

    /**
     * 主键的增值策略
     * @return
     */
    KeyStrategy strategy() default KeyStrategy.AUTO;

    /**
     * 主键的数据类型
     * @return
     */
    DbType dbType() default DbType.DbInt;


    /**
     * 主键说明(primary key)
     * @return
     */
    String desc() default "主键";




}
