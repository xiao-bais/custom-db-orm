package com.custom.wrapper;

import com.custom.enums.DbSymbol;
import com.custom.enums.SqlLike;
import com.custom.enums.SqlOrderBy;
import com.custom.sqlparser.TableSqlBuilder;

import java.io.Serializable;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 17:17
 * @Desc：lambda表达式的条件构造对象
 **/
public class LambdaConditionEntity<T> extends ConditionAdapterHandler<T, SFunction<T, ?>, LambdaConditionEntity<T>>
        implements Wrapper<SFunction<T, ?>, LambdaConditionEntity<T>> {


    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column) {
        appendCondition(dbSymbol, condition, parseColumn(column), null, null, null);
        return childrenClass;
    }


    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String columnSql) {
        appendCondition(dbSymbol, condition, columnSql, null, null, columnSql);
        return childrenClass;
    }


    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, Object val) {
        appendCondition(dbSymbol, condition, parseColumn(column), val, null, null);
        return childrenClass;
    }


    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        appendCondition(dbSymbol, condition, parseColumn(column), val1, val2, null);
        return childrenClass;
    }


    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, String express) {
        appendCondition(dbSymbol, condition, parseColumn(column), null, null, express);
        return childrenClass;
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> select(SFunction<T, ?>... columns) {
        setSelectColumns(parseColumn(columns));
        return childrenClass;
    }

    @Override
    public LambdaConditionEntity<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.EQUALS, condition, column, val);
    }

    @Override
    public LambdaConditionEntity<T> ge(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.GREATER_THAN_EQUALS, condition, column, val);
    }

    @Override
    public LambdaConditionEntity<T> le(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LESS_THAN_EQUALS, condition, column, val);
    }

    @Override
    public LambdaConditionEntity<T> lt(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LESS_THAN, condition, column, val);
    }

    @Override
    public LambdaConditionEntity<T> gt(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.GREATER_THAN, condition, column, val);
    }

    @Override
    public LambdaConditionEntity<T> in(boolean condition, SFunction<T, ?> column, Collection<?> val) {
        return adapter(DbSymbol.IN, condition, column, val);
    }

    @Override
    public LambdaConditionEntity<T> notIn(boolean condition, SFunction<T, ?> column, Collection<?> val) {
        return adapter(DbSymbol.NOT_IN, condition, column, val);
    }

    @Override
    public LambdaConditionEntity<T> exists(boolean condition, String existsSql) {
        return adapter(DbSymbol.EXISTS, condition, existsSql);
    }

    @Override
    public LambdaConditionEntity<T> notExists(boolean condition, String notExistsSql) {
        return adapter(DbSymbol.EXISTS, condition, notExistsSql);
    }

    @Override
    public LambdaConditionEntity<T> like(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.LIKE, val));
    }

    @Override
    public LambdaConditionEntity<T> notLike(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.NOT_LIKE, condition, column, sqlConcat(SqlLike.LIKE, val));
    }

    @Override
    public LambdaConditionEntity<T> likeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.LEFT, val));
    }

    @Override
    public LambdaConditionEntity<T> likeRight(boolean condition, SFunction<T, ?> column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.RIGHT, val));
    }

    @Override
    public LambdaConditionEntity<T> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        return adapter(DbSymbol.BETWEEN, condition, column, val1, val2);
    }

    @Override
    public LambdaConditionEntity<T> notBetween(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        return adapter(DbSymbol.NOT_BETWEEN, condition, column, val1, val2);
    }

    @Override
    public LambdaConditionEntity<T> isNull(boolean condition, SFunction<T, ?> column) {
        adapter(DbSymbol.IS_NULL, condition, column);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> isNotNull(boolean condition, SFunction<T, ?> column) {
        return adapter(DbSymbol.IS_NOT_NULL, condition, column);
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> orderByAsc(boolean condition, SFunction<T, ?>... columns) {
        for (String column : parseColumn(columns)) {
            String orderByField = orderByField(column, SqlOrderBy.ASC);
            adapter(DbSymbol.ORDER_BY, condition, orderByField);
        }
        return childrenClass;
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> orderByDesc(boolean condition, SFunction<T, ?>... columns) {
        for (String column : parseColumn(columns)) {
            String orderByField = orderByField(column, SqlOrderBy.DESC);
            adapter(DbSymbol.ORDER_BY, condition, orderByField);
        }
        return childrenClass;
    }


    /**
     * 函数式接口序列化解析对象
     */
    private final ColumnParseHandler<T> columnParseHandler;

    public LambdaConditionEntity(Class<T> entityClass) {
        setCls(entityClass);
        TableSqlBuilder<T> tableSqlBuilder = getTableParserModelCache(entityClass);
        setTableSqlBuilder(tableSqlBuilder);
        columnParseHandler = new ColumnParseHandler<>(entityClass, tableSqlBuilder.getFields());
    }


    /**
     * 解析函数后，得到java属性字段对应的表字段名称
     */
    private String parseColumn(SFunction<T, ?> func) {
        return columnParseHandler.getColumn(func);
    }

    /**
     * 解析函数后，得到java属性字段对应的表字段名称
     */
    @SafeVarargs
    private final String[] parseColumn(SFunction<T, ?>... func) {
        return columnParseHandler.getColumn(func);
    }


    @Override
    public T getEntity() {
        return null;
    }
}
