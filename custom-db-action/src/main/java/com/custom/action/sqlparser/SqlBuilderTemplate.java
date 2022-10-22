package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;

/**
 * @author Xiao-Bai
 * @date 2022/9/22 1:15
 * @desc
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

    public SqlBuilderTemplate(Class<T> entityClass, int order) {
        this.selectSqlBuilder = new HandleSelectSqlBuilder<>(entityClass, order);
        this.insertSqlBuilder = new HandleInsertSqlBuilder<>(entityClass, order);
        this.updateSqlBuilder = new HandleUpdateSqlBuilder<>(entityClass, order);
        this.deleteSqlBuilder = new HandleDeleteSqlBuilder<>(entityClass, order);
        this.emptySqlBuilder = new EmptySqlBuilder<>(entityClass, order);
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
