package com.custom.annotations.reader;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 16:52
 * @Desc：
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Update {

    /**
     * 读取文件的路径，用于在查询时获取路径中文件的内容
     */
    String value();
}
