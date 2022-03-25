package com.custom.sqlparser;

import com.custom.fill.TableFillObject;

import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/24 11:14
 * @Desc：实体解析模板缓存
 **/
@SuppressWarnings("unchecked")
public class TableInfoCache {

    /**
     * 实体解析模板缓存
     * key-实体全路径名称
     * value-实体解析模板（TableSqlBuilder）
     */
    private final static Map<String, Object> tableModel = new CustomLocalCache();
    private static Boolean underlineToCamel = false;

    public static <T> TableSqlBuilder<T> getTableModel(Class<T> cls) {
        TableSqlBuilder<T> tableSqlBuilder = (TableSqlBuilder<T>) tableModel.get(cls.getName());
        if(tableSqlBuilder == null) {
            tableSqlBuilder = new TableSqlBuilder<>(cls, underlineToCamel);
            tableModel.put(cls.getName(), tableSqlBuilder);
        }
        return tableSqlBuilder;
    }
    public static void setUnderlineToCamel(boolean underlineToCamel) {
        TableInfoCache.underlineToCamel = underlineToCamel;
    }


    /**
     * 获取表字段到java字段的映射
     */
    public static <T> Map<String, String> getColumnMap(Class<T> cls) {
        return getTableModel(cls).getColumnMapper();
    }

    /**
     * 获取java字段到表字段的映射
     */
    public static <T> Map<String, String> getFieldMap(Class<T> cls) {
        return getTableModel(cls).getFieldMapper();
    }


    /**
     * 表的逻辑删除字段缓存
     * key-实体全路径名称
     * value-true or false
     */
    private final static Map<String, Object> tableLogic = new CustomLocalCache();

    public static void setTableLogic(String key, Object val) {
        tableLogic.put(key, val);
    }

    public static Boolean isExistsLogic(String table) {
        return (Boolean) tableLogic.get(table);
    }


    /**
     * 字段自动填充缓存
     * key - java实体对象Class
     * value - 填充策略对象
     */
    private final static Map<String, Object> TableFill = new CustomLocalCache();

    public static void setTableFill(String key, Object val) {
        TableFill.put(key, val);
    }

    public static TableFillObject getTableFill(String key) {
        return  (TableFillObject) TableFill.get(key);
    }

    public static Boolean isExistsTableFill() {
        return TableFill.isEmpty();
    }

}