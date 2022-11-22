package com.custom.action.condition;

import com.custom.comm.enums.SqlAggregate;
import com.custom.comm.enums.SqlOrderBy;
import com.custom.comm.utils.lambda.SFunction;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/19 17:27
 * @Desc：排序函数式接口方法
 **/
public class OrderByFunc<T> extends AbstractSqlFunc<T, OrderByFunc<T>> {

    @Override
    public OrderByFunc<T> sum(SFunction<T, ?> column) {
        String originColumn = columnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.SUM, null), SqlAggregate.SUM, originColumn, orderBy);
    }

    @Override
    public OrderByFunc<T> sum(boolean isNullToZero, SFunction<T, ?> column) {
        String originColumn = columnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.SUM, isNullToZero, null), SqlAggregate.SUM, originColumn, orderBy);
    }

    @Override
    public OrderByFunc<T> avg(SFunction<T, ?> column) {
        String originColumn = columnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.AVG, null), SqlAggregate.AVG, originColumn, orderBy);
    }

    @Override
    public OrderByFunc<T> avg(boolean isNullToZero, SFunction<T, ?> column) {
        String originColumn = columnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.AVG, isNullToZero, null), SqlAggregate.AVG, originColumn, orderBy);
    }

    @Override
    public OrderByFunc<T> count(SFunction<T, ?> column, boolean distinct) {
        String originColumn = columnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.COUNT, distinct), SqlAggregate.COUNT, originColumn, orderBy);
    }

    public OrderByFunc<T> countOne() {
        return doFunc("%s(1) %s", SqlAggregate.COUNT, orderBy);
    }

    public OrderByFunc<T> countAll() {
        return doFunc("%s(*) %s", SqlAggregate.COUNT, orderBy);
    }

    @Override
    public OrderByFunc<T> ifNull(SFunction<T, ?> column, Object elseVal) {
        String originColumn = columnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.IFNULL, null), SqlAggregate.IFNULL, originColumn, elseVal, orderBy);
    }

    @Override
    public OrderByFunc<T> max(SFunction<T, ?> column) {
        String originColumn = columnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.MAX, null), SqlAggregate.MAX, originColumn, orderBy);
    }

    @Override
    public OrderByFunc<T> max(boolean isNullToZero, SFunction<T, ?> column) {
        String originColumn = columnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.MAX, isNullToZero, null), SqlAggregate.MAX, originColumn, orderBy);
    }

    @Override
    public OrderByFunc<T> min(SFunction<T, ?> column) {
        String originColumn = columnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.MIN, null), SqlAggregate.MIN, originColumn, orderBy);
    }

    @Override
    public OrderByFunc<T> min(boolean isNullToZero, SFunction<T, ?> column) {
        String originColumn = columnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.MIN, isNullToZero, null), SqlAggregate.MIN, originColumn, orderBy);
    }

    private final String orderBy;

    public OrderByFunc(Class<T> entityClass, SqlOrderBy orderBy) {
        this.orderBy = orderBy.getName();
        super.initNeed(entityClass);
    }

    public String getOrderBy() {
        return orderBy;
    }
}
