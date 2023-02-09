package com.custom.action.condition;

import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.Constants;
import com.custom.comm.enums.DbSymbol;
import com.custom.comm.enums.SqlOrderBy;
import com.custom.comm.utils.lambda.SFunction;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 中间层条件适配器
 * @author  Xiao-Bai
 * @since  2022/5/19 16:10
 **/
@SuppressWarnings("unchecked")
public class ConditionAdapter<T, Children> extends ConditionAssembly<T, SFunction<T, ?>, Children> {


    @Override
    protected Children adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column) {
        appendCondition(dbSymbol, condition, parseColumn(column), null, null, null);
        return childrenClass;
    }


    @Override
    protected Children adapter(DbSymbol dbSymbol, boolean condition, String columnSql) {
        appendCondition(dbSymbol, condition, columnSql, null, null, columnSql);
        return childrenClass;
    }


    @Override
    protected Children adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, Object val) {
        appendCondition(dbSymbol, condition, parseColumn(column), val, null, null);
        return childrenClass;
    }


    @Override
    protected Children adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        appendCondition(dbSymbol, condition, parseColumn(column), val1, val2, null);
        return childrenClass;
    }


    @Override
    protected Children adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, String expression) {
        appendCondition(dbSymbol, condition, parseColumn(column), null, null, expression);
        return childrenClass;
    }

    @Override
    protected Children getInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Children or(boolean condition, Children wrapper) {
        return spliceCondition(condition, false, (ConditionWrapper<T>) wrapper);
    }

    @Override
    public Children or(boolean condition, Consumer<Children> consumer) {
        return mergeConsmerCondition(condition, false, consumer);
    }

    @Override
    public Children or(boolean condition) {
        appendState = condition;
        if(condition) {
            appendSybmol = Constants.OR;
        }
        return childrenClass;
    }

    @Override
    public Children and(boolean condition, Children wrapper) {
        return spliceCondition(condition, true, (ConditionWrapper<T>) wrapper);
    }

    @Override
    public Children and(boolean condition, Consumer<Children> consumer) {
        return mergeConsmerCondition(condition, true, consumer);
    }

    @SafeVarargs
    @Override
    public final Children select(SFunction<T, ?>... columns) {
        setSelectColumns(Arrays.stream(parseColumn(columns)).collect(Collectors.toList()));
        return childrenClass;
    }

    @Override
    public Children select(Consumer<SelectFunc<T>> consumer) {
        return doSelectSqlFunc(consumer);
    }

    @Override
    public Children select(SelectFunc<T> selectFunc) {
        addSelectColumns(selectFunc.getSqlFragments());
        return childrenClass;
    }

    @SafeVarargs
    @Override
    public final Children groupBy(SFunction<T, ?>... columns) {
        Arrays.stream(parseColumn(columns))
                .forEach(x -> adapter(DbSymbol.GROUP_BY, true, x));
        return childrenClass;
    }

    @Override
    public Children orderByAsc(boolean condition, Consumer<OrderByFunc<T>> consumer) {
        return doOrderBySqlFunc(SqlOrderBy.ASC, consumer);
    }

    @Override
    public Children orderByAsc(boolean condition, OrderByFunc<T> orderByFunc) {
        if (!orderByFunc.getOrderBy().equals(Constants.ASC)) {
            throw new CustomCheckException("order by type is mismatch, should be entered '%s', current entered '%s'",
                    Constants.ASC, Constants.DESC);
        }
        return adapter(DbSymbol.ORDER_BY_ASC, condition, orderByFunc.getColumns());
    }

    @Override
    public Children orderByDesc(boolean condition, Consumer<OrderByFunc<T>> consumer) {
        return doOrderBySqlFunc(SqlOrderBy.DESC, consumer);
    }

    @Override
    public Children orderByDesc(boolean condition, OrderByFunc<T> orderByFunc) {
        if (!orderByFunc.getOrderBy().equals(Constants.DESC)) {
            throw new CustomCheckException("order by type is mismatch, should be entered '%s', current entered '%s'",
                    Constants.DESC, Constants.ASC);
        }
        return adapter(DbSymbol.ORDER_BY_DESC, condition, orderByFunc.getColumns());
    }

    @SafeVarargs
    @Override
    public final Children orderByAsc(boolean condition, SFunction<T, ?>... columns) {
        Arrays.stream(parseColumn(columns))
                .map(column -> orderByField(column, SqlOrderBy.ASC))
                .forEach(orderByField -> adapter(DbSymbol.ORDER_BY, condition, orderByField));
        return childrenClass;
    }

    @SafeVarargs
    @Override
    public final Children orderByDesc(boolean condition, SFunction<T, ?>... columns) {
        Arrays.stream(parseColumn(columns))
                .map(column -> orderByField(column, SqlOrderBy.DESC))
                .forEach(orderByField -> adapter(DbSymbol.ORDER_BY, condition, orderByField));
        return childrenClass;
    }

    @Override
    public Children orderByAsc(boolean condition, SFunction<T, ?> column) {
        String field = orderByField(parseColumn(column), SqlOrderBy.ASC);
        return adapter(DbSymbol.ORDER_BY, condition, field);
    }

    @Override
    public Children orderByDesc(boolean condition, SFunction<T, ?> column) {
        String field = orderByField(parseColumn(column), SqlOrderBy.DESC);
        return adapter(DbSymbol.ORDER_BY, condition, field);
    }

    /**
     * sql排序函数执行方法
     */
    private Children doOrderBySqlFunc(SqlOrderBy orderBy, Consumer<OrderByFunc<T>> consumer) {
        OrderByFunc<T> sqlFunc = new OrderByFunc<>(getEntityClass(), orderBy);
        consumer.accept(sqlFunc);
        return adapter(DbSymbol.ORDER_BY, true, sqlFunc.getColumns());
    }

    @Override
    public T getThisEntity() {
        return null;
    }
}
