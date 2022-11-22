package com.custom.joiner.core.condition;

import com.custom.comm.utils.lambda.SFunction;

import java.util.Collection;

/**
 * @author Xiao-Bai
 * @date 2022/8/31 23:49
 * @desc
 */
public class LambdaJoinConditionWrapper<T> implements JoinConditionWrapper<LambdaJoinConditionWrapper<T>> {
    @Override
    public <Param> LambdaJoinConditionWrapper<T> eq(boolean condition, SFunction<Param, ?> column, Object val) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> ge(boolean condition, SFunction<Param, ?> column, Object val) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> le(boolean condition, SFunction<Param, ?> column, Object val) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> lt(boolean condition, SFunction<Param, ?> column, Object val) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> gt(boolean condition, SFunction<Param, ?> column, Object val) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> in(boolean condition, SFunction<Param, ?> column, Collection<?> val) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> inSql(boolean condition, SFunction<Param, ?> column, String inSql, Object... params) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> notIn(boolean condition, SFunction<Param, ?> column, Collection<?> val) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> notInSql(boolean condition, SFunction<Param, ?> column, String inSql, Object... params) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> exists(boolean condition, String existsSql) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> notExists(boolean condition, String notExistsSql) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> like(boolean condition, SFunction<Param, ?> column, Object val) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> notLike(boolean condition, SFunction<Param, ?> column, Object val) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> likeLeft(boolean condition, SFunction<Param, ?> column, Object val) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> likeRight(boolean condition, SFunction<Param, ?> column, Object val) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> between(boolean condition, SFunction<Param, ?> column, Object val1, Object val2) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> notBetween(boolean condition, SFunction<Param, ?> column, Object val1, Object val2) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> isNull(boolean condition, SFunction<Param, ?> column) {
        return null;
    }

    @Override
    public <Param> LambdaJoinConditionWrapper<T> isNotNull(boolean condition, SFunction<Param, ?> column) {
        return null;
    }
}
