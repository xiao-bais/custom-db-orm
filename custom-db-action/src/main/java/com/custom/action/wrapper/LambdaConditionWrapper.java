package com.custom.action.wrapper;

import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.DbSymbol;
import com.custom.comm.enums.SqlLike;
import com.custom.comm.enums.SqlOrderBy;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 17:17
 * @Desc：lambda表达式的条件构造对象
 **/
public class LambdaConditionWrapper<T> extends ConditionAdapter<T, LambdaConditionWrapper<T>>
        implements Wrapper<SFunction<T, ?>, LambdaConditionWrapper<T>> {


    @Override
    protected LambdaConditionWrapper<T> getInstance() {
        return new LambdaConditionWrapper<>(getEntityClass());
    }

    @Override
    public LambdaConditionWrapper<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.EQUALS, condition, column, val);
    }

    @Override
    public LambdaConditionWrapper<T> ge(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.GREATER_THAN_EQUALS, condition, column, val);
    }

    @Override
    public LambdaConditionWrapper<T> le(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LESS_THAN_EQUALS, condition, column, val);
    }

    @Override
    public LambdaConditionWrapper<T> lt(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LESS_THAN, condition, column, val);
    }

    @Override
    public LambdaConditionWrapper<T> gt(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.GREATER_THAN, condition, column, val);
    }

    @Override
    public LambdaConditionWrapper<T> in(boolean condition, SFunction<T, ?> column, Collection<?> val) {
        return adapter(DbSymbol.IN, condition, column, val);
    }

    @Override
    public LambdaConditionWrapper<T> inSql(boolean condition, String inSql, Object... params) {
        return null;
    }

    @Override
    public LambdaConditionWrapper<T> notIn(boolean condition, SFunction<T, ?> column, Collection<?> val) {
        return adapter(DbSymbol.NOT_IN, condition, column, val);
    }

    @Override
    public LambdaConditionWrapper<T> exists(boolean condition, String existsSql) {
        return adapter(DbSymbol.EXISTS, condition, existsSql);
    }

    @Override
    public LambdaConditionWrapper<T> notExists(boolean condition, String notExistsSql) {
        return adapter(DbSymbol.EXISTS, condition, notExistsSql);
    }

    @Override
    public LambdaConditionWrapper<T> like(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.LIKE, val));
    }

    @Override
    public LambdaConditionWrapper<T> notLike(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.NOT_LIKE, condition, column, sqlConcat(SqlLike.LIKE, val));
    }

    @Override
    public LambdaConditionWrapper<T> likeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.LEFT, val));
    }

    @Override
    public LambdaConditionWrapper<T> likeRight(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.RIGHT, val));
    }

    @Override
    public LambdaConditionWrapper<T> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        return adapter(DbSymbol.BETWEEN, condition, column, val1, val2);
    }

    @Override
    public LambdaConditionWrapper<T> notBetween(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        return adapter(DbSymbol.NOT_BETWEEN, condition, column, val1, val2);
    }

    @Override
    public LambdaConditionWrapper<T> isNull(boolean condition, SFunction<T, ?> column) {
        adapter(DbSymbol.IS_NULL, condition, column);
        return childrenClass;
    }

    @Override
    public LambdaConditionWrapper<T> isNotNull(boolean condition, SFunction<T, ?> column) {
        return adapter(DbSymbol.IS_NOT_NULL, condition, column);
    }

    public LambdaConditionWrapper(Class<T> entityClass) {
        wrapperInitialize(entityClass);
    }


    @Override
    public T getEntity() {
        return null;
    }
}
