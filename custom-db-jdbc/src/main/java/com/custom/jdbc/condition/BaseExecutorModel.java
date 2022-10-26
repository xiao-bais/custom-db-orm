package com.custom.jdbc.condition;

import java.util.Objects;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 21:50
 * @Desc
 */
public class BaseExecutorModel {

    /**
     * 待执行的sql
     */
    private final String prepareSql;

    /**
     * 是否支持sql打印
     */
    private final boolean sqlPrintSupport;
    /**
     * sql参数
     */
    private final Object[] sqlParams;

    public BaseExecutorModel(String prepareSql, boolean sqlPrintSupport, Object[] sqlParams) {
        this.prepareSql = prepareSql;
        this.sqlPrintSupport = sqlPrintSupport;
        this.sqlParams = sqlParams;
    }

    public String getPrepareSql() {
        return prepareSql;
    }

    public boolean isSqlPrintSupport() {
        return sqlPrintSupport;
    }

    public Object[] getSqlParams() {
        if (Objects.isNull(this.sqlParams)) {
            return new Object[]{};
        }
        return sqlParams;
    }

}