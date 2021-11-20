package com.custom.annotations.reader;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 16:51
 * @Desc：
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Query {

    /**
    * 示例：1：sql/test.sql, 2: select * from tableName
    */
    String value();
    
    /**
     * true：获取的value值用于读取内容路径中指定的文件内容（test.sql）。以此获取sql
     * false：获取的value值直接用于执行的sql
    */
    boolean isPath() default true;

    /**
     * true：参数以放置顺序来匹配sql中'?'的位置
     * false: 以参数的名称来替换sql中对应的参数名称（例如：以 name 将 #{name} 替换成 '?'）
    */
    boolean isOrder() default true;

}
