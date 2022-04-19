package com.custom.action.generator.table;

import com.custom.comm.enums.DbMediaType;
import com.custom.comm.enums.KeyStrategy;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/19 14:49
 * @Desc：字段结构解析模板
 **/
public class ColumnStructModel {

    /**
     * 字段名称
     */
    private String column;

    /**
     * 表名
     */
    private String table;

    /**
     * 属性名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private DbMediaType columnType;

    /**
     * java属性类型
     */
    private Class<?> fieldType;

    /**
     * 是否是主键字段
     */
    private Boolean primaryKey = false;
    private String keyExtra;

    /**
     * 主键策略
     */
    private KeyStrategy keyStrategy;

    /**
     * 字段说明
     */
    private String desc;


    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public DbMediaType getColumnType() {
        return columnType;
    }

    public void setColumnType(DbMediaType columnType) {
        this.columnType = columnType;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public KeyStrategy getKeyStrategy() {
        return keyStrategy;
    }

    public void setKeyStrategy(KeyStrategy keyStrategy) {
        this.keyStrategy = keyStrategy;
    }

    public String getKeyExtra() {
        return keyExtra;
    }

    public void setKeyExtra(String keyExtra) {
        this.keyExtra = keyExtra;
    }
}
