package com.custom.fill;

import com.custom.enums.FillStrategy;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/21 14:47
 * @Desc：自动填充对象
 **/
public class TableFillObject {

    /**
     * 填充字段
     */
    private String fieldName;

    /**
     * 实体Class
     */
    private Class<?> entityClass = null;

    /**
     * 填充的字段值
     */
    private Object fieldVal;

    /**
     * 填充策略
     */
    private FillStrategy strategy;


    public TableFillObject(String fieldName, Class<?> entityClass, Object fieldVal, FillStrategy strategy) {
        this.fieldName = fieldName;
        this.entityClass = entityClass;
        this.fieldVal = fieldVal;
        this.strategy = strategy;
    }

    public TableFillObject(String fieldName,  String fieldVal, FillStrategy strategy) {
        this(fieldName, null, fieldVal, strategy);
    }

    public TableFillObject() {};


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Object getFieldVal() {
        return fieldVal;
    }

    public void setFieldVal(Object fieldVal) {
        this.fieldVal = fieldVal;
    }

    public FillStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(FillStrategy strategy) {
        this.strategy = strategy;
    }
}
