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
    private DbMediaType dbType;
    private String columnType;

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

    /**
     * 生成的getter/setter方法名称
     */
    private String getterMethodName;
    private String setterMethodName;

    /**
     * 字段上的Db*注解配置描述
     */
    private String dbFieldAnnotation;
    
    /**
     * 输出字段信息
     */
    private String outputFieldInfo;


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

    public DbMediaType getDbType() {
        return dbType;
    }

    public void setDbType(DbMediaType dbType) {
        this.dbType = dbType;
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

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getGetterMethodName() {
        return getterMethodName;
    }

    public void setGetterMethodName(String getterMethodName) {
        this.getterMethodName = getterMethodName;
    }

    public String getSetterMethodName() {
        return setterMethodName;
    }

    public void setSetterMethodName(String setterMethodName) {
        this.setterMethodName = setterMethodName;
    }

    public String getDbFieldAnnotation() {
        return dbFieldAnnotation;
    }

    public void setDbFieldAnnotation(String dbFieldAnnotation) {
        this.dbFieldAnnotation = dbFieldAnnotation;
    }

    public String getOutputFieldInfo() {
        return outputFieldInfo;
    }

    public void setOutputFieldInfo(String outputFieldInfo) {
        this.outputFieldInfo = outputFieldInfo;
    }
}
