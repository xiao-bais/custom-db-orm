package com.custom.action.wrapper;

import com.custom.comm.enums.SqlAggregate;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/19 17:27
 * @Desc：排序函数式接口方法
 **/
public class OrderByFunc<T> extends SqlFunc<T, OrderByFunc<T>>{

    @Override
    public OrderByFunc<T> sum(SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.SUM, null), SqlAggregate.SUM, column, orderByStyle);
    }

    @Override
    public OrderByFunc<T> avg(SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.AVG, null), SqlAggregate.AVG, column, orderByStyle);
    }

    @Override
    public OrderByFunc<T> count(SFunction<T, ?> func, boolean distinct) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.COUNT, distinct), SqlAggregate.COUNT, column, orderByStyle);
    }

    @Override
    public OrderByFunc<T> ifNull(SFunction<T, ?> func, Object elseVal) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.IFNULL, null), SqlAggregate.IFNULL, column, elseVal, orderByStyle);
    }

    @Override
    public OrderByFunc<T> max(SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.MAX, null), SqlAggregate.MAX, column, orderByStyle);
    }

    @Override
    public OrderByFunc<T> min(SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.MIN, null), SqlAggregate.MIN, column, orderByStyle);
    }

    private final String orderByStyle;

    public OrderByFunc(Class<T> entityClass, String orderByStyle) {
        this.orderByStyle = orderByStyle;
        super.init(entityClass);
    }
}
