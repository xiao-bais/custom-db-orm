package com.custom.sqlparser;

import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/24 11:14
 * @Desc：实体解析模板缓存
 **/
@SuppressWarnings("unchecked")
public class TableParserModelCache {

    /**
     * 实体解析模板缓存
     */
    private final static Map<String, Object> tableModel = new CustomLocalCache();


    public static void setTableModel(String key, Object val) {
       tableModel.put(key, val);
    }

    public static <T> TableSqlBuilder<T> getTableModel(Class<T> cls) {
        TableSqlBuilder<T> tableSqlBuilder = (TableSqlBuilder<T>) tableModel.get(cls.getName());
        if(tableSqlBuilder == null) {
            tableSqlBuilder = new TableSqlBuilder<>(cls);
        }
        return tableSqlBuilder;
    }
}
