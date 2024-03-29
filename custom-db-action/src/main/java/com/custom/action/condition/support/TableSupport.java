package com.custom.action.condition.support;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 用于部分需要表数据支持的对象
 * @author   Xiao-Bai
 * @since  2022/11/1 13:07
 */
public interface TableSupport {

    /**
     * 表名
     */
    String table();

    /**
     * 别名
     */
    String alias();

    /**
     * java字段到sql字段的映射
     */
    Map<String, String> fieldMap();

    /**
     * sql字段到java字段的映射
     */
    Map<String, String> columnMap();

    /**
     * 所有字段
     */
    List<Field> fields();
}
