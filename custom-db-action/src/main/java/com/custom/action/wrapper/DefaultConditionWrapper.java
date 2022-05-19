package com.custom.action.wrapper;

import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.DbSymbol;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.enums.SqlLike;
import com.custom.comm.enums.SqlOrderBy;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/16 14:11
 * @Desc：条件构造实例对象
 **/
public class DefaultConditionWrapper<T> extends ConditionAssembly<T, String, DefaultConditionWrapper<T>>
        implements Wrapper<String, DefaultConditionWrapper<T>> {


    @Override
    public DefaultConditionWrapper<T> eq(boolean condition, String column, Object val) {
        return adapter(DbSymbol.EQUALS, condition, column, val);
    }

    @Override
    public DefaultConditionWrapper<T> ge(boolean condition, String column, Object val) {
        return adapter(DbSymbol.GREATER_THAN_EQUALS, condition, column, val);
    }

    @Override
    public DefaultConditionWrapper<T> le(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LESS_THAN_EQUALS, condition, column, val);
    }

    @Override
    public DefaultConditionWrapper<T> lt(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LESS_THAN, condition, column, val);
    }

    @Override
    public DefaultConditionWrapper<T> gt(boolean condition, String column, Object val) {
        return adapter(DbSymbol.GREATER_THAN, condition, column, val);
    }

    @Override
    public DefaultConditionWrapper<T> in(boolean condition, String column, Collection<?> val) {
        return adapter(DbSymbol.IN, condition, column, val);
    }

    @Override
    public DefaultConditionWrapper<T> inSql(boolean condition, String inSql, Object... params) {
        return adapter(DbSymbol.IN, condition, inSql, params);
    }

    @Override
    public DefaultConditionWrapper<T> notIn(boolean condition, String column, Collection<?> val) {
        return adapter(DbSymbol.NOT_IN, condition, column, val);
    }

    @Override
    public DefaultConditionWrapper<T> exists(boolean condition, String existsSql) {
        return adapter(DbSymbol.EXISTS, condition, null,  existsSql);
    }

    @Override
    public DefaultConditionWrapper<T> notExists(boolean condition, String notExistsSql) {
        return adapter(DbSymbol.NOT_EXISTS, condition, null, null, notExistsSql);
    }

    @Override
    public DefaultConditionWrapper<T> like(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.LIKE, val));
    }

    @Override
    public DefaultConditionWrapper<T> notLike(boolean condition, String column, Object val) {
        return adapter(DbSymbol.NOT_LIKE, condition, column, sqlConcat(SqlLike.LIKE, val));
    }

    @Override
    public DefaultConditionWrapper<T> likeLeft(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.LEFT, val));
    }

    @Override
    public DefaultConditionWrapper<T> likeRight(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.RIGHT, val));
    }

    @Override
    public DefaultConditionWrapper<T> between(boolean condition, String column, Object val1, Object val2) {
        return adapter(DbSymbol.BETWEEN, condition, column, val1, val2);
    }

    @Override
    public DefaultConditionWrapper<T> notBetween(boolean condition, String column, Object val1, Object val2) {
        return adapter(DbSymbol.NOT_BETWEEN, condition, column, val1, val2);
    }

    @Override
    public DefaultConditionWrapper<T> isNull(boolean condition, String column) {
        return adapter(DbSymbol.IS_NULL, condition, column, null, null);
    }

    @Override
    public DefaultConditionWrapper<T> isNotNull(boolean condition, String column) {
        return adapter(DbSymbol.IS_NOT_NULL, condition, column, null, null);
    }

    @Override
    public DefaultConditionWrapper<T> orderByAsc(boolean condition, String... columns) {
        String orderBy = Arrays.stream(columns).map(column -> orderByField(column, SqlOrderBy.ASC)).collect(Collectors.joining("", SymbolConstant.EMPTY, ""));
        return adapter(DbSymbol.ORDER_BY, condition, orderBy);
    }

    @Override
    public DefaultConditionWrapper<T> orderByDesc(boolean condition, String... columns) {
        String orderBy = Arrays.stream(columns).map(column -> orderByField(column, SqlOrderBy.DESC)).collect(Collectors.joining("", SymbolConstant.EMPTY, ""));
        return adapter(DbSymbol.ORDER_BY, condition, orderBy);
    }


    public DefaultConditionWrapper(Class<T> entityClass) {
        setEntityClass(entityClass);
        setTableSqlBuilder(new TableSqlBuilder<>(entityClass, ExecuteMethod.NONE, false));
    }

    @Override
    protected DefaultConditionWrapper<T> adapter(DbSymbol dbSymbol, boolean condition, String column) {
        appendCondition(dbSymbol, condition, column, null, null, null);
        return childrenClass;
    }

    @Override
    protected DefaultConditionWrapper<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val) {
        appendCondition(dbSymbol, condition, column, val, null, null);
        return childrenClass;
    }

    @Override
    protected DefaultConditionWrapper<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2) {
        appendCondition(dbSymbol, condition, column, val1, val2, null);
        return childrenClass;
    }

    @Override
    protected DefaultConditionWrapper<T> adapter(DbSymbol dbSymbol, boolean condition, String column, String express) {
        appendCondition(dbSymbol, condition, column, null, null, express);
        return childrenClass;
    }

    @Override
    protected DefaultConditionWrapper<T> getInstance() {
        return new DefaultConditionWrapper<>(getEntityClass());
    }

    @Override
    public DefaultConditionWrapper<T> or(boolean condition, DefaultConditionWrapper<T> wrapper) {
        return spliceCondition(condition, false, wrapper);
    }

    @Override
    public DefaultConditionWrapper<T> or(boolean condition, Consumer<DefaultConditionWrapper<T>> consumer) {
        return mergeConsmerCondition(condition, false, consumer);
    }

    @Override
    public DefaultConditionWrapper<T> or(boolean condition) {
        appendState = condition;
        if(condition) {
            appendSybmol = SymbolConstant.OR;
        }
        return childrenClass;
    }

    @Override
    public DefaultConditionWrapper<T> and(boolean condition, DefaultConditionWrapper<T> wrapper) {
        return spliceCondition(condition, true, wrapper);
    }

    @Override
    public DefaultConditionWrapper<T> and(boolean condition, Consumer<DefaultConditionWrapper<T>> consumer) {
        return mergeConsmerCondition(condition, true, consumer);
    }

    /**
     * 若是查询单表（查询的实体中(包括父类)没有@DbRelated,@DbJoinTables之类的关联注解），则column为表字段，例如：name,age
     * 若是查询关联表字段，则需附带关联表别名，例如：tp.name,tp.age
     */
    @Override
    public DefaultConditionWrapper<T> select(String... columns) {
        setSelectColumns(columns);
        return childrenClass;
    }


    @Override
    public T getEntity() {
        return null;
    }


    @Override
    public DefaultConditionWrapper<T> select(Consumer<SelectFunc<T>> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DefaultConditionWrapper<T> select(SelectFunc<T> selectFunc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DefaultConditionWrapper<T> groupBy(String... columns) {
        Arrays.stream(columns).forEach(x -> adapter(DbSymbol.GROUP_BY, true, x));
        return childrenClass;
    }

    @Override
    public DefaultConditionWrapper<T> orderByAsc(boolean condition, Consumer<OrderByFunc<T>> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DefaultConditionWrapper<T> orderByAsc(boolean condition, OrderByFunc<T> orderByFunc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DefaultConditionWrapper<T> orderByDesc(boolean condition, Consumer<OrderByFunc<T>> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DefaultConditionWrapper<T> orderByDesc(boolean condition, OrderByFunc<T> orderByFunc) {
        throw new UnsupportedOperationException();
    }

}