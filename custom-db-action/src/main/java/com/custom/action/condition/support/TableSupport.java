package com.custom.action.condition.support;

import java.util.Map;

/**
 * @author Xiao-Bai
 * @date 2022/11/1 13:07
 * @desc 用于部分需要表数据支持的对象
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
}
