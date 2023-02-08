package com.custom.jdbc.condition;

import com.custom.comm.utils.Asserts;

import java.util.Objects;

/**
 * @author Xiao-Bai
 * @date 2022/6/17 21:50
 */
public class BaseExecutorBody {

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

    public BaseExecutorBody(String prepareSql, boolean sqlPrintSupport, Object[] sqlParams) {
        Asserts.notEmpty(prepareSql, "The Sql to be Not Empty");
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
