package com.custom.wrapper;

import com.custom.enums.SqlAggregate;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/19 17:27
 * @Desc：排序函数式接口方法
 **/
public class OrderByFunc<T> extends SqlFunc<T, OrderByFunc<T>>{

    @Override
    public OrderByFunc<T> sum(SFunction<T, ?> func) {
        String field = getColumnParseHandler().getField(func);
        return doFunc(getFormatRex(SqlAggregate.SUM, null), SqlAggregate.SUM, getFieldMapper().get(field), orderByStyle);
    }

    @Override
    public OrderByFunc<T> avg(SFunction<T, ?> func) {
        String field = getColumnParseHandler().getField(func);
        return doFunc(getFormatRex(SqlAggregate.AVG, null), SqlAggregate.AVG, getFieldMapper().get(field), orderByStyle);
    }

    @Override
    public OrderByFunc<T> count(SFunction<T, ?> func, boolean distinct) {
        String field = getColumnParseHandler().getField(func);
        return doFunc(getFormatRex(SqlAggregate.COUNT, distinct), SqlAggregate.COUNT, getFieldMapper().get(field), orderByStyle);
    }

    @Override
    public OrderByFunc<T> ifNull(SFunction<T, ?> func, Object elseVal) {
        String field = getColumnParseHandler().getField(func);
        return doFunc(getFormatRex(SqlAggregate.IF_NULL, null), SqlAggregate.IF_NULL, getFieldMapper().get(field), elseVal, orderByStyle);
    }

    @Override
    public OrderByFunc<T> max(SFunction<T, ?> func) {
        String field = getColumnParseHandler().getField(func);
        return doFunc(getFormatRex(SqlAggregate.MAX, null), SqlAggregate.MAX, getFieldMapper().get(field), orderByStyle);
    }

    @Override
    public OrderByFunc<T> min(SFunction<T, ?> func) {
        String field = getColumnParseHandler().getField(func);
        return doFunc(getFormatRex(SqlAggregate.MIN, null), SqlAggregate.MIN, getFieldMapper().get(field), orderByStyle);
    }

    private final String orderByStyle;

    public OrderByFunc(Class<T> entityClass, String orderByStyle) {
        this.orderByStyle = orderByStyle;
        super.init(entityClass);
    }
}
