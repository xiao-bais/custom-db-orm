package com.custom.action.wrapper;

import com.custom.action.dbconfig.SymbolConst;
import com.custom.action.enums.DbSymbol;
import com.custom.action.enums.ExecuteMethod;
import com.custom.action.enums.SqlLike;
import com.custom.action.enums.SqlOrderBy;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.enums.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/16 14:11
 * @Desc：条件构造实例对象
 **/
public class ConditionEntity<T> extends ConditionAssembly<T, String, ConditionEntity<T>>
        implements Wrapper<String, ConditionEntity<T>> {


    @Override
    public ConditionEntity<T> eq(boolean condition, String column, Object val) {
        return adapter(DbSymbol.EQUALS, condition, column, val);
    }

    @Override
    public ConditionEntity<T> ge(boolean condition, String column, Object val) {
        return adapter(DbSymbol.GREATER_THAN_EQUALS, condition, column, val);
    }

    @Override
    public ConditionEntity<T> le(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LESS_THAN_EQUALS, condition, column, val);
    }

    @Override
    public ConditionEntity<T> lt(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LESS_THAN, condition, column, val);
    }

    @Override
    public ConditionEntity<T> gt(boolean condition, String column, Object val) {
        return adapter(DbSymbol.GREATER_THAN, condition, column, val);
    }

    @Override
    public ConditionEntity<T> in(boolean condition, String column, Collection<?> val) {
        return adapter(DbSymbol.IN, condition, column, val);
    }

    @Override
    public ConditionEntity<T> notIn(boolean condition, String column, Collection<?> val) {
        return adapter(DbSymbol.NOT_IN, condition, column, val);
    }

    @Override
    public ConditionEntity<T> exists(boolean condition, String existsSql) {
        return adapter(DbSymbol.EXISTS, condition, null,  existsSql);
    }

    @Override
    public ConditionEntity<T> notExists(boolean condition, String notExistsSql) {
        return adapter(DbSymbol.NOT_EXISTS, condition, null, null, notExistsSql);
    }

    @Override
    public ConditionEntity<T> like(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.LIKE, val));
    }

    @Override
    public ConditionEntity<T> notLike(boolean condition, String column, Object val) {
        return adapter(DbSymbol.NOT_LIKE, condition, column, sqlConcat(SqlLike.LIKE, val));
    }

    @Override
    public ConditionEntity<T> likeLeft(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.LEFT, val));
    }

    @Override
    public ConditionEntity<T> likeRight(boolean condition, String column, Object val) {
        return adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.RIGHT, val));
    }

    @Override
    public ConditionEntity<T> between(boolean condition, String column, Object val1, Object val2) {
        return adapter(DbSymbol.BETWEEN, condition, column, val1, val2);
    }

    @Override
    public ConditionEntity<T> notBetween(boolean condition, String column, Object val1, Object val2) {
        return adapter(DbSymbol.NOT_BETWEEN, condition, column, val1, val2);
    }

    @Override
    public ConditionEntity<T> isNull(boolean condition, String column) {
        return adapter(DbSymbol.IS_NULL, condition, column, null, null);
    }

    @Override
    public ConditionEntity<T> isNotNull(boolean condition, String column) {
        return adapter(DbSymbol.IS_NOT_NULL, condition, column, null, null);
    }

    @Override
    public ConditionEntity<T> orderByAsc(boolean condition, String... columns) {
        String orderBy = Arrays.stream(columns).map(column -> orderByField(column, SqlOrderBy.ASC)).collect(Collectors.joining("", SymbolConst.EMPTY, ""));
        return adapter(DbSymbol.ORDER_BY, condition, orderBy);
    }

    @Override
    public ConditionEntity<T> orderByDesc(boolean condition, String... columns) {
        String orderBy = Arrays.stream(columns).map(column -> orderByField(column, SqlOrderBy.DESC)).collect(Collectors.joining("", SymbolConst.EMPTY, ""));
        return adapter(DbSymbol.ORDER_BY, condition, orderBy);
    }


    public ConditionEntity(Class<T> entityClass) {
        setEntityClass(entityClass);
        setTableSqlBuilder(new TableSqlBuilder<>(entityClass, ExecuteMethod.NONE, false));
    }

    @Override
    protected ConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column) {
        appendCondition(dbSymbol, condition, column, null, null, null);
        return childrenClass;
    }

    @Override
    protected ConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val) {
        appendCondition(dbSymbol, condition, column, val, null, null);
        return childrenClass;
    }

    @Override
    protected ConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2) {
        appendCondition(dbSymbol, condition, column, val1, val2, null);
        return childrenClass;
    }

    @Override
    protected ConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, String express) {
        appendCondition(dbSymbol, condition, column, null, null, express);
        return childrenClass;
    }

    @Override
    protected ConditionEntity<T> getInstance() {
        return new ConditionEntity<>(getEntityClass());
    }

    @Override
    public ConditionEntity<T> or(boolean condition, ConditionEntity<T> wrapper) {
        return spliceCondition(condition, false, wrapper);
    }

    @Override
    public ConditionEntity<T> or(boolean condition, Consumer<ConditionEntity<T>> consumer) {
        return mergeConsmerCondition(condition, false, consumer);
    }

    @Override
    public ConditionEntity<T> or(boolean condition) {
        appendState = condition;
        if(condition) {
            appendSybmol = SymbolConst.OR;
        }
        return childrenClass;
    }

    @Override
    public ConditionEntity<T> and(boolean condition, ConditionEntity<T> wrapper) {
        return spliceCondition(condition, true, wrapper);
    }

    @Override
    public ConditionEntity<T> and(boolean condition, Consumer<ConditionEntity<T>> consumer) {
        return mergeConsmerCondition(condition, true, consumer);
    }

    /**
     * 若是查询单表（查询的实体中(包括父类)没有@DbRelated,@DbJoinTables之类的关联注解），则column为表字段，例如：name,age
     * 若是查询关联表字段，则需附带关联表别名，例如：tp.name,tp.age
     */
    @Override
    public ConditionEntity<T> select(String... columns) {
        setSelectColumns(columns);
        return childrenClass;
    }


    @Override
    public T getEntity() {
        return null;
    }


    @Override
    public ConditionEntity<T> select(Consumer<SelectFunc<T>> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConditionEntity<T> groupBy(String... columns) {
        Arrays.stream(columns).forEach(x -> adapter(DbSymbol.GROUP_BY, true, x));
        return childrenClass;
    }

    @Override
    public ConditionEntity<T> orderByAsc(boolean condition, Consumer<OrderByFunc<T>> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConditionEntity<T> orderByDesc(boolean condition, Consumer<OrderByFunc<T>> consumer) {
        throw new UnsupportedOperationException();
    }

}
