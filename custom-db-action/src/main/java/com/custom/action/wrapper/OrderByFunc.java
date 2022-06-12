package com.custom.action.wrapper;

import com.custom.comm.enums.DbSymbol;
import com.custom.comm.enums.SqlAggregate;
import com.custom.comm.enums.SqlOrderBy;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/19 17:27
 * @Desc：排序函数式接口方法
 **/
public class OrderByFunc<T> extends SqlFunc<T, OrderByFunc<T>>{

    @Override
    public OrderByFunc<T> sum(SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.SUM, null), SqlAggregate.SUM, column, orderBy);
    }

    @Override
    public OrderByFunc<T> sum(boolean isNullToZero, SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.SUM, isNullToZero, null), SqlAggregate.SUM, column, orderBy);
    }

    @Override
    public OrderByFunc<T> avg(SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.AVG, null), SqlAggregate.AVG, column, orderBy);
    }

    @Override
    public OrderByFunc<T> avg(boolean isNullToZero, SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.AVG, isNullToZero, null), SqlAggregate.AVG, column, orderBy);
    }

    @Override
    public OrderByFunc<T> count(SFunction<T, ?> func, boolean distinct) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.COUNT, distinct), SqlAggregate.COUNT, column, orderBy);
    }

    @Override
    public OrderByFunc<T> ifNull(SFunction<T, ?> func, Object elseVal) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.IFNULL, null), SqlAggregate.IFNULL, column, elseVal, orderBy);
    }

    @Override
    public OrderByFunc<T> max(SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.MAX, null), SqlAggregate.MAX, column, orderBy);
    }

    @Override
    public OrderByFunc<T> max(boolean isNullToZero, SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.MAX, isNullToZero, null), SqlAggregate.MAX, column, orderBy);
    }

    @Override
    public OrderByFunc<T> min(SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.MIN, null), SqlAggregate.MIN, column, orderBy);
    }

    @Override
    public OrderByFunc<T> min(boolean isNullToZero, SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.MIN, isNullToZero, null), SqlAggregate.MIN, column, orderBy);
    }

    private final String orderBy;

    public OrderByFunc(Class<T> entityClass, SqlOrderBy orderBy) {
        this.orderBy = orderBy.getName();
        super.init(entityClass);
    }

    public String getOrderBy() {
        return orderBy;
    }
}
