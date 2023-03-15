package com.custom.action.core;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

import java.util.List;

/**
 * 该类暂时只做一些判断时使用，不做任何sql构建或增删改查操作
 * @author   Xiao-Bai
 * @since  2022/10/1 22:53
 */
public class EmptySqlBuilder<T> extends AbstractSqlBuilder<T> {


    public EmptySqlBuilder(Class<T> entityClass, JdbcSqlSessionFactory sqlSessionFactory) {
        TableParseModel<T> tableModel = TableInfoCache.getTableModel(entityClass);
        this.injectTableInfo(tableModel, sqlSessionFactory);
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
