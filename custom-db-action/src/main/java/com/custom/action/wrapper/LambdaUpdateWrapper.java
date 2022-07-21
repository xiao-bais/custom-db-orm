package com.custom.action.wrapper;

import com.custom.comm.enums.DbSymbol;
import com.custom.comm.enums.SqlLike;

import java.util.Collection;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 17:17
 * @Desc：lambda表达式的条件构造对象
 **/
public class LambdaUpdateWrapper<T> extends ConditionAdapter<T, LambdaUpdateWrapper<T>>
        implements Wrapper<SFunction<T, ?>, LambdaUpdateWrapper<T>>, UpdateSet<SFunction<T, ?>, LambdaUpdateWrapper<T>> {


    @Override
    protected LambdaUpdateWrapper<T> getInstance() {
        return new LambdaUpdateWrapper<>(getEntityClass());
    }

    @Override
    public LambdaUpdateWrapper<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.EQUALS, condition, column, val);
    }

    @Override
    public LambdaUpdateWrapper<T> ge(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.GREATER_THAN_EQUALS, condition, column, val);
    }

    @Override
    public LambdaUpdateWrapper<T> le(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LESS_THAN_EQUALS, condition, column, val);
    }

    @Override
    public LambdaUpdateWrapper<T> lt(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LESS_THAN, condition, column, val);
    }

    @Override
    public LambdaUpdateWrapper<T> gt(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.GREATER_THAN, condition, column, val);
    }

    @Override
    public LambdaUpdateWrapper<T> in(boolean condition, SFunction<T, ?> column, Collection<?> val) {
        return adapter(DbSymbol.IN, condition, column, val);
    }

    @Override
    public LambdaUpdateWrapper<T> inSql(boolean condition, SFunction<T, ?> column, String inSql, Object... params) {
        appendInSql(parseColumn(column), DbSymbol.IN, inSql, params);
        return childrenClass;
    }

    @Override
    public LambdaUpdateWrapper<T> notIn(boolean condition, SFunction<T, ?> column, Collection<?> val) {
        return adapter(DbSymbol.NOT_IN, condition, column, val);
    }

    @Override
    public LambdaUpdateWrapper<T> notInSql(boolean condition, SFunction<T, ?> column, String inSql, Object... params) {
        appendInSql(parseColumn(column), DbSymbol.NOT_IN, inSql, params);
        return childrenClass;
    }

    @Override
    public LambdaUpdateWrapper<T> exists(boolean condition, String existsSql) {
        return adapter(DbSymbol.EXISTS, condition, null, existsSql);
    }

    @Override
    public LambdaUpdateWrapper<T> notExists(boolean condition, String notExistsSql) {
        return adapter(DbSymbol.NOT_EXISTS, condition, null, notExistsSql);
    }

    @Override
    public LambdaUpdateWrapper<T> like(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, val, SqlLike.LIKE);
    }

    @Override
    public LambdaUpdateWrapper<T> notLike(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.NOT_LIKE, condition, column, val, SqlLike.LIKE);
    }

    @Override
    public LambdaUpdateWrapper<T> likeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, val, SqlLike.LEFT);
    }

    @Override
    public LambdaUpdateWrapper<T> likeRight(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, val, SqlLike.RIGHT);
    }

    @Override
    public LambdaUpdateWrapper<T> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        return adapter(DbSymbol.BETWEEN, condition, column, val1, val2);
    }

    @Override
    public LambdaUpdateWrapper<T> notBetween(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        return adapter(DbSymbol.NOT_BETWEEN, condition, column, val1, val2);
    }

    @Override
    public LambdaUpdateWrapper<T> isNull(boolean condition, SFunction<T, ?> column) {
        adapter(DbSymbol.IS_NULL, condition, column);
        return childrenClass;
    }

    @Override
    public LambdaUpdateWrapper<T> isNotNull(boolean condition, SFunction<T, ?> column) {
        return adapter(DbSymbol.IS_NOT_NULL, condition, column);
    }

    /**
     * 转成默认格式的条件构造器
     */
    public DefaultConditionWrapper<T> toDefault() {
        return new DefaultConditionWrapper<T>(this);
    }

    public LambdaUpdateWrapper(Class<T> entityClass) {
        wrapperInitialize(entityClass);
    }


    LambdaUpdateWrapper(ConditionWrapper<T> wrapper) {
        this.setEntityClass(wrapper.getEntityClass());
        this.setColumnParseHandler(wrapper.getColumnParseHandler());
        this.setLastCondition(wrapper.getLastCondition());
        this.setSelectColumns(wrapper.getSelectColumns());
        this.setPageParams(wrapper.getPageIndex(), wrapper.getPageSize());
        this.setTableSqlBuilder(wrapper.getTableSqlBuilder());
        this.setPrimaryTable(wrapper.getPrimaryTable());
    }


    @Override
    public T getThisEntity() {
        return this.get();
    }

    @Override
    public LambdaUpdateWrapper<T> set(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaUpdateWrapper<T> setSql(boolean condition, String setSql, Object... params) {
        return null;
    }
}
