package com.custom.action.core;

import java.io.Serializable;

/**
 * 字段属性关联
 * @author  Xiao-Bai
 * @since  2022/8/25 12:45
 */
public class ColumnPropertyMap<T> implements Serializable {

    /**
     * java属性名
     */
    private String propertyName;
    /**
     * java属性类型
     */
    private Class<?> propertyType;
    /**
     * get方法名
     */
    private String getMethodName;

    /**
     * sql字段名
     */
    private String column;

    /**
     * 带别名前缀的sql字段名
     */
    private String aliasColumn;

    /**
     * 表名
     */
    private String tableName;

    private Class<T> targetClass;

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public void setGetMethodName(String getMethodName) {
        this.getMethodName = getMethodName;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public void setAliasColumn(String aliasColumn) {
        this.aliasColumn = aliasColumn;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public String getGetMethodName() {
        return getMethodName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumn() {
        return column;
    }

    public String getAliasColumn() {
        return aliasColumn;
    }

    public Class<T> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    public static ColumnPropertyMap<?> parse2Map(Class<?> targetClass, String implMethod) {
        TableParseModel<?> tableModel = TableInfoCache.getTableModel(targetClass);
        return tableModel.columnPropertyMaps()
                .stream().filter(op -> op.getGetMethodName().equals(implMethod))
                .findFirst().orElse(null);
    }

    public static String parse2Property(Class<?> targetClass, String implMethod) {
        ColumnPropertyMap<?> parse2Map = parse2Map(targetClass, implMethod);
        if (parse2Map != null) {
            return parse2Map.propertyName;
        }
        return null;
    }

    public static String parse2Column(Class<?> targetClass, String implMethod) {
        ColumnPropertyMap<?> parse2Map = parse2Map(targetClass, implMethod);
        if (parse2Map != null) {
            return parse2Map.column;
        }
        return "";
    }

    public static String parse2AliasColumn(Class<?> targetClass, String implMethod) {
        ColumnPropertyMap<?> parse2Map = parse2Map(targetClass, implMethod);
        if (parse2Map != null) {
            return parse2Map.aliasColumn;
        }
        return null;
    }

}
