package com.custom.action.wrapper;

import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.DbSymbol;
import com.custom.comm.enums.SqlLike;
import com.custom.comm.enums.SqlOrderBy;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 17:17
 * @Desc：lambda表达式的条件构造对象
 **/
public class LambdaConditionEntity<T> extends ConditionAssembly<T, SFunction<T, ?>, LambdaConditionEntity<T>>
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

    @Override
    protected LambdaConditionEntity<T> getInstance() {
        return new LambdaConditionEntity<>(getEntityClass());
    }

    @Override
    public LambdaConditionEntity<T> or(boolean condition, LambdaConditionEntity<T> wrapper) {
        return spliceCondition(condition, false, wrapper);
    }

    @Override
    public LambdaConditionEntity<T> or(boolean condition, Consumer<LambdaConditionEntity<T>> consumer) {
        return mergeConsmerCondition(condition, false, consumer);
    }

    @Override
    public LambdaConditionEntity<T> or(boolean condition) {
        appendState = condition;
        if(condition) {
            appendSybmol = SymbolConstant.OR;
        }
        return childrenClass;
    }

    @Override
    public LambdaConditionEntity<T> and(boolean condition, LambdaConditionEntity<T> wrapper) {
        return spliceCondition(condition, true, wrapper);
    }

    @Override
    public LambdaConditionEntity<T> and(boolean condition, Consumer<LambdaConditionEntity<T>> consumer) {
        return mergeConsmerCondition(condition, true, consumer);
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> select(SFunction<T, ?>... columns) {
        setSelectColumns(parseColumn(columns));
        return childrenClass;
    }

    @Override
    public LambdaConditionEntity<T> select(Consumer<SelectFunc<T>> consumer) {
        return doSelectSqlFunc(consumer);
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> groupBy(SFunction<T, ?>... columns) {
        Arrays.stream(parseColumn(columns)).forEach(x -> adapter(DbSymbol.GROUP_BY, true, x));
        return childrenClass;
    }

    @Override
    public LambdaConditionEntity<T> orderByAsc(boolean condition, Consumer<OrderByFunc<T>> consumer) {
        return doOrderBySqlFunc(SymbolConstant.ASC, consumer);
    }

    @Override
    public LambdaConditionEntity<T> orderByDesc(boolean condition, Consumer<OrderByFunc<T>> consumer) {
        return doOrderBySqlFunc(SymbolConstant.DESC, consumer);
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
        Arrays.stream(parseColumn(columns)).map(column -> orderByField(column, SqlOrderBy.ASC)).forEach(orderByField -> adapter(DbSymbol.ORDER_BY, condition, orderByField));
        return childrenClass;
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> orderByDesc(boolean condition, SFunction<T, ?>... columns) {
        Arrays.stream(parseColumn(columns)).map(column -> orderByField(column, SqlOrderBy.DESC)).forEach(orderByField -> adapter(DbSymbol.ORDER_BY, condition, orderByField));
        return childrenClass;
    }


    /**
     * 函数式接口序列化解析对象
     */
    private final ColumnParseHandler<T> columnParseHandler;

    public LambdaConditionEntity(Class<T> entityClass) {
        setEntityClass(entityClass);
        TableSqlBuilder<T> tableSqlBuilder = getTableParserModelCache(entityClass);
        setTableSqlBuilder(tableSqlBuilder);
        columnParseHandler = new ColumnParseHandler<>(entityClass);
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

    /**
     * sql查询函数执行方法
     */
    private LambdaConditionEntity<T> doSelectSqlFunc(Consumer<SelectFunc<T>> consumer) {
        SelectFunc<T> sqlFunc = new SelectFunc<>(getEntityClass());
        consumer.accept(sqlFunc);
        mergeSelect(sqlFunc.getColumns().split(SymbolConstant.SEPARATOR_COMMA_2));
        return childrenClass;
    }

    /**
     * sql排序函数执行方法
     */
    private LambdaConditionEntity<T> doOrderBySqlFunc(String orderByStyle, Consumer<OrderByFunc<T>> consumer) {
        OrderByFunc<T> sqlFunc = new OrderByFunc<>(getEntityClass(), orderByStyle);
        consumer.accept(sqlFunc);
        return adapter(DbSymbol.ORDER_BY, true, sqlFunc.getColumns());
    }


    @Override
    public T getEntity() {
        return null;
    }
}
