package com.custom.wrapper;

import com.custom.enums.DbSymbol;
import com.custom.enums.SqlLike;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/16 14:11
 * @Desc：
 **/
public class QueryWrapper<T> extends ConditionEntity<T> implements Wrapper<String, ConditionEntity<T>> {


    @Override
    public ConditionEntity<T> eq(boolean condition, String column, Object val) {
        return adapter(DbSymbol.EQUALS, condition, column, val);
    }

    @Override
    public ConditionEntity<T> ge(boolean condition, String column, Object val) {
        return adapter(DbSymbol.GREATER_THAN_EQUALS, condition, column, val);
    }

    @Override
    public ConditionEntity<T> le(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LESS_THAN_EQUALS, condition, column, val);
    }

    @Override
    public ConditionEntity<T> lt(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LESS_THAN, condition, column, val);
    }

    @Override
    public ConditionEntity<T> gt(boolean condition, String column, Object val) {
        return adapter(DbSymbol.GREATER_THAN, condition, column, val);
    }

    @Override
    public ConditionEntity<T> in(boolean condition, String column, Collection<? extends Serializable> val) {
        return adapter(DbSymbol.IN, condition, column, val);
    }

    @Override
    public ConditionEntity<T> notIn(boolean condition, String column, Collection<? extends Serializable> val) {
        return adapter(DbSymbol.NOT_IN, condition, column, val);
    }

    @Override
    public ConditionEntity<T> exists(boolean condition, String existsSql) {
        return adapter(DbSymbol.EXISTS, condition, null, null, existsSql);
    }

    @Override
    public ConditionEntity<T> notExists(boolean condition, String notExistsSql) {
        return adapter(DbSymbol.NOT_EXISTS, condition, null, null, notExistsSql);
    }

    @Override
    public ConditionEntity<T> like(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, val);
    }

    @Override
    public ConditionEntity<T> notLike(boolean condition, String column, Object val) {
        return adapter(DbSymbol.NOT_LIKE, condition, column, val);
    }

    @Override
    public ConditionEntity<T> likeLeft(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.LEFT, val));
    }

    @Override
    public ConditionEntity<T> likeRight(boolean condition, String column, Object val) {
        return null;
    }

    @Override
    public ConditionEntity<T> between(boolean condition, String column, Object val1, Object val2) {
        return null;
    }

    @Override
    public ConditionEntity<T> notBetween(boolean condition, String column, Object val1, Object val2) {
        return null;
    }

    @Override
    public ConditionEntity<T> isNull(boolean condition, String column) {
        return null;
    }

    @Override
    public ConditionEntity<T> isNotNull(boolean condition, String column) {
        return null;
    }

    @Override
    public ConditionEntity<T> or() {
        return null;
    }

    @Override
    public ConditionEntity<T> or(boolean condition, ConditionEntity<T> conditionEntity) {
        return null;
    }

    @Override
    public ConditionEntity<T> or(ConditionEntity<T> conditionEntity) {
        return null;
    }

    @Override
    public ConditionEntity<T> and() {
        return null;
    }

    @Override
    public ConditionEntity<T> and(boolean condition, ConditionEntity<T> conditionEntity) {
        return null;
    }

    @Override
    public ConditionEntity<T> and(ConditionEntity<T> conditionEntity) {
        return null;
    }


    public QueryWrapper(Class<T> entityClass) {
        super(entityClass);
    }
}
