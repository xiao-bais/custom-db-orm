package com.custom.action.wrapper;

import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.DbSymbol;
import com.custom.comm.enums.SqlOrderBy;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2022/5/19 16:10
 * @Desc：中间层条件适配器
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
    protected Children adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, String express) {
        appendCondition(dbSymbol, condition, parseColumn(column), null, null, express);
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
            appendSybmol = SymbolConstant.OR;
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
        setSelectColumns(parseColumn(columns));
        return childrenClass;
    }

    @Override
    public Children select(Consumer<SelectFunc<T>> consumer) {
        return doSelectSqlFunc(consumer);
    }

    @SafeVarargs
    @Override
    public final Children groupBy(SFunction<T, ?>... columns) {
        Arrays.stream(parseColumn(columns)).forEach(x -> adapter(DbSymbol.GROUP_BY, true, x));
        return childrenClass;
    }

    @Override
    public Children orderByAsc(boolean condition, Consumer<OrderByFunc<T>> consumer) {
        return doOrderBySqlFunc(SymbolConstant.ASC, consumer);
    }

    @Override
    public Children orderByDesc(boolean condition, Consumer<OrderByFunc<T>> consumer) {
        return doOrderBySqlFunc(SymbolConstant.DESC, consumer);
    }

    @SafeVarargs
    @Override
    public final Children orderByAsc(boolean condition, SFunction<T, ?>... columns) {
        Arrays.stream(parseColumn(columns)).map(column -> orderByField(column, SqlOrderBy.ASC)).forEach(orderByField -> adapter(DbSymbol.ORDER_BY, condition, orderByField));
        return childrenClass;
    }

    @SafeVarargs
    @Override
    public final Children orderByDesc(boolean condition, SFunction<T, ?>... columns) {
        Arrays.stream(parseColumn(columns)).map(column -> orderByField(column, SqlOrderBy.DESC)).forEach(orderByField -> adapter(DbSymbol.ORDER_BY, condition, orderByField));
        return childrenClass;
    }

    /**
     * sql排序函数执行方法
     */
    private Children doOrderBySqlFunc(String orderByStyle, Consumer<OrderByFunc<T>> consumer) {
        OrderByFunc<T> sqlFunc = new OrderByFunc<>(getEntityClass(), orderByStyle);
        consumer.accept(sqlFunc);
        return adapter(DbSymbol.ORDER_BY, true, sqlFunc.getColumns());
    }

    @Override
    public T getEntity() {
        return null;
    }
}
