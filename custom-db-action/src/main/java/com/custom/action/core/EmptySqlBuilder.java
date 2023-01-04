package com.custom.action.core;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.jdbc.executor.JdbcExecutorFactory;

import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2022/10/1 22:53
 * 该类暂时只做一些判断时使用，不做任何sql构建或增删改查操作
 */
public class EmptySqlBuilder<T> extends AbstractSqlBuilder<T> {


    public EmptySqlBuilder(Class<T> entityClass, JdbcExecutorFactory executorFactory) {
        TableParseModel<T> tableModel = TableInfoCache.getTableModel(entityClass);
        this.injectTableInfo(tableModel, executorFactory);
    }

    @Override
    public String createTargetSql() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String createTargetSql(boolean primaryTable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String createTargetSql(Object obj, List<Object> sqlParams) {
        return null;
    }
}
