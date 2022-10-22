package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;

/**
 * @author Xiao-Bai
 * @date 2022/10/1 22:53
 * 该类暂时只做一些判断时使用，不做任何sql构建或增删改查操作
 */
public class EmptySqlBuilder<T> extends AbstractSqlBuilder<T> {


    public EmptySqlBuilder(Class<T> entityClass, int order) {
        TableParseModel<T> tableModel = TableInfoCache.getTableModel(entityClass);
        this.injectTableInfo(tableModel, order);
    }

    @Override
    public String createTargetSql() {
        throw new UnsupportedOperationException();
    }
}
