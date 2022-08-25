package com.custom.action.sqlparser;

import com.custom.action.condition.SFunction;

import java.io.Serializable;

/**
 * @author Xiao-Bai
 * @date 2022/8/25 12:45
 * @desc
 */
public class ColumnFunctionMap<T> implements Serializable {

    /**
     * Function函数
     */
    private SFunction<T, ?> columnFunc;

    /**
     * java属性名
     */
    private String propertyName;

    /**
     * sql字段名
     */
    private String column;

    public ColumnFunctionMap(SFunction<T, ?> columnFunc, String propertyName, String column) {
        this.columnFunc = columnFunc;
        this.propertyName = propertyName;
        this.column = column;
    }

    public ColumnFunctionMap() {
    }

    public SFunction<T, ?> getColumnFunc() {
        return columnFunc;
    }

    public void setColumnFunc(SFunction<T, ?> columnFunc) {
        this.columnFunc = columnFunc;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
