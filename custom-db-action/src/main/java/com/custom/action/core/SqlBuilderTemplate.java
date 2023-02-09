package com.custom.action.core;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.jdbc.executor.JdbcExecutorFactory;

/**
 * SQL构建模板
 * @author   Xiao-Bai
 * @since  2022/9/22 1:15
 */
public class SqlBuilderTemplate<T> {

    /**
     * 查询sql构造模板对象
     */
    private final AbstractSqlBuilder<T> selectSqlBuilder;
    /**
     * 插入sql构造模板对象
     */
    private final AbstractSqlBuilder<T> insertSqlBuilder;
    /**
     * 修改sql构造模板对象
     */
    private final AbstractSqlBuilder<T> updateSqlBuilder;
    /**
     * 删除sql构造模板对象
     */
    private final AbstractSqlBuilder<T> deleteSqlBuilder;

    private final AbstractSqlBuilder<T> emptySqlBuilder;

    public SqlBuilderTemplate(Class<T> entityClass, JdbcExecutorFactory executorFactory) {
        this.selectSqlBuilder = new HandleSelectSqlBuilder<>(entityClass, executorFactory);
        this.insertSqlBuilder = new HandleInsertSqlBuilder<>(entityClass, executorFactory);
        this.updateSqlBuilder = new HandleUpdateSqlBuilder<>(entityClass, executorFactory);
        this.deleteSqlBuilder = new HandleDeleteSqlBuilder<>(entityClass, executorFactory);
        this.emptySqlBuilder = new EmptySqlBuilder<>(entityClass, executorFactory);
    }

    public AbstractSqlBuilder<T> getSelectSqlBuilder() {
        return selectSqlBuilder;
    }

    public AbstractSqlBuilder<T> getInsertSqlBuilder() {
        return insertSqlBuilder;
    }

    public AbstractSqlBuilder<T> getUpdateSqlBuilder() {
        return updateSqlBuilder;
    }

    public AbstractSqlBuilder<T> getDeleteSqlBuilder() {
        return deleteSqlBuilder;
    }

    public AbstractSqlBuilder<T> getEmptySqlBuilder() {
        return emptySqlBuilder;
    }
}
