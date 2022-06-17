package com.custom.jdbc.param;

import java.util.Objects;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 21:50
 * @Desc
 */
public class BaseSqlParamInfo {

    /**
     * 待执行的sql
     */
    private String prepareSql;

    /**
     * 是否支持sql打印
     */
    private boolean sqlPrintSupport;
    /**
     * sql参数
     */
    private Object[] sqlParams;

    public BaseSqlParamInfo(String prepareSql, boolean sqlPrintSupport, Object[] sqlParams) {
        this.prepareSql = prepareSql;
        this.sqlPrintSupport = sqlPrintSupport;
        this.sqlParams = sqlParams;
    }

    public String getPrepareSql() {
        return prepareSql;
    }

    public void setPrepareSql(String prepareSql) {
        this.prepareSql = prepareSql;
    }

    public boolean isSqlPrintSupport() {
        return sqlPrintSupport;
    }

    public void setSqlPrintSupport(boolean sqlPrintSupport) {
        this.sqlPrintSupport = sqlPrintSupport;
    }

    public Object[] getSqlParams() {
        if (Objects.isNull(this.sqlParams)) {
            return new Object[0];
        }
        return sqlParams;
    }

    public void setSqlParams(Object[] sqlParams) {
        this.sqlParams = sqlParams;
    }
}
