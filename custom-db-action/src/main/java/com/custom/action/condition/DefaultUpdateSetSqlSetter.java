package com.custom.action.condition;

/**
 * 默认的sql设置器
 * @author  Xiao-Bai
 * @since  2022/6/27 0027 11:59
 */
public class DefaultUpdateSetSqlSetter<T> extends AbstractUpdateSetSqlSetter<T, DefaultUpdateSetSqlSetter<T>>
        implements UpdateSqlSet<String, DefaultUpdateSetSqlSetter<T>> {


    @Override
    protected DefaultUpdateSetSqlSetter<T> getInstance() {
        return new DefaultUpdateSetSqlSetter<>(thisEntityClass());
    }

    public DefaultUpdateSetSqlSetter(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public DefaultUpdateSetSqlSetter<T> set(boolean condition, String column, Object val) {
        return super.addSetSql(condition, column, val);
    }

    @Override
    public DefaultUpdateSetSqlSetter<T> setSql(boolean condition, String setSql, Object... params) {
        return super.addSetSqlString(condition, setSql, params);
    }
}
