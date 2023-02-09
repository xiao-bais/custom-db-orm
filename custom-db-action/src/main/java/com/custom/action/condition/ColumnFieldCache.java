package com.custom.action.condition;

import java.io.Serializable;

/**
 * 属性、sql字段缓存对象
 * @author   Xiao-Bai
 * @since  2022/12/6 0006 13:59
 */
public class ColumnFieldCache implements Serializable {

    /**
     * java属性
     */
    private final String field;

    /**
     * field对应get方法名
     */
    private final String getter;

    /**
     * sql字段(加别名)
     */
    private final String column;

    public ColumnFieldCache(String field, String getter, String column) {
        this.field = field;
        this.getter = getter;
        this.column = column;
    }

    public String getField() {
        return field;
    }

    public String getGetter() {
        return getter;
    }

    public String getColumn() {
        return column;
    }
}
