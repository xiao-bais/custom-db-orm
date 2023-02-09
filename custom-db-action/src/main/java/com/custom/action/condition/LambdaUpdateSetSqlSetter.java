package com.custom.action.condition;

import com.custom.comm.utils.lambda.SFunction;

/**
 * lambda表达式的sql设置器
 * @author   Xiao-Bai
 * @since  2022/6/27 0027 11:59
 */
public class LambdaUpdateSetSqlSetter<T> extends AbstractUpdateSetSqlSetter<T, LambdaUpdateSetSqlSetter<T>>
        implements UpdateSqlSet<SFunction<T, ?>, LambdaUpdateSetSqlSetter<T>> {


    @Override
    protected LambdaUpdateSetSqlSetter<T> getInstance() {
        return new LambdaUpdateSetSqlSetter<>(thisEntityClass());
    }

    public LambdaUpdateSetSqlSetter(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public LambdaUpdateSetSqlSetter<T> set(boolean condition, SFunction<T, ?> column, Object val) {
        return super.addSetSql(condition, column, val);
    }

    @Override
    public LambdaUpdateSetSqlSetter<T> setSql(boolean condition, String setSql, Object... params) {
        return super.addSetSqlString(condition, setSql, params);
    }
}
