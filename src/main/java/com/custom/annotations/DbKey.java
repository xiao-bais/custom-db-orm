package com.custom.annotations;

import com.custom.enums.DbMediaType;
import com.custom.enums.KeyStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/10 0010 16:25
 * @Version 1.0
 * @Description DbKey
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbKey {

    /**
     * 主键
     * @return
     */
    String value() default "";

    /**
     * 主键的增值类型
     * @return
     */
    KeyStrategy strategy() default KeyStrategy.AUTO;

    /**
     * 主键的数据类型
     * @return
     */
    DbMediaType dbType() default DbMediaType.DbInt;


    /**
     * 主键说明
     * @return
     */
    String desc() default "";




}
