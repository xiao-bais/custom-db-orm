package com.custom.jdbc.executebody;

import com.custom.comm.utils.AssertUtil;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.jdbc.handler.ResultSetTypeMappedHandler;

import java.util.Objects;

/**
 * 基础执行体
 * @author  Xiao-Bai
 * @since  2022/6/17 21:50
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
        AssertUtil.notEmpty(prepareSql, "The Sql to be Not Empty");
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

    /**
     * 创建查询结果集映射处理对象
     */
    public <T> ResultSetTypeMappedHandler<T> createRsMappedHandler(Class<T> entityClass, DbGlobalConfig globalConfig) {
        return new ResultSetTypeMappedHandler<>(entityClass, globalConfig);
    }

}
