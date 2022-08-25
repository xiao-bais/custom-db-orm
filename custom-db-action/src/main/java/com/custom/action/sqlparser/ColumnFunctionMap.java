package com.custom.action.sqlparser;

import com.custom.action.condition.SFunction;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author Xiao-Bai
 * @date 2022/8/25 12:45
 * @desc
 */
public class ColumnFunctionMap<T> implements Serializable {

    /**
     * Function函数
     */
    private SFunction<T, ?> lambdaFunction;
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
     * 实体对象
     */
    private Class<T> entityClass;


    public ColumnFunctionMap(SFunction<T, ?> lambdaFunction, String propertyName, Class<?> propertyType, String getMethodName, String column, Class<T> entityClass) {
        this.lambdaFunction = lambdaFunction;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.getMethodName = getMethodName;
        this.column = column;
        this.entityClass = entityClass;
    }

    public ColumnFunctionMap() {
    }

    public SFunction<T, ?> getLambdaFunction() {
        return lambdaFunction;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public String getColumn() {
        return column;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public String getGetMethodName() {
        return getMethodName;
    }

    public void setLambdaFunction(SFunction<T, ?> lambdaFunction) {
        this.lambdaFunction = lambdaFunction;
    }

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

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
}
