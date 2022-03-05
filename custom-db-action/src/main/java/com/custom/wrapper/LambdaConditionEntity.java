package com.custom.wrapper;

import com.custom.enums.DbSymbol;
import com.custom.enums.ExecuteMethod;
import com.custom.enums.SqlLike;
import com.custom.sqlparser.TableParserModelCache;
import com.custom.sqlparser.TableSqlBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 17:17
 * @Desc：lambda表达式的条件构造对象
 **/
public class LambdaConditionEntity<T> extends AbstractWrapper<T, LambdaConditionEntity<T>> implements Wrapper<SFunction<T, ?>, LambdaConditionEntity<T>> {


    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column) {
        appendCondition(dbSymbol, condition, column, null, null, null);
        return this;
    }

    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val) {
        appendCondition(dbSymbol, condition, column, val, null, null);
        return this;
    }

    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2) {
        appendCondition(dbSymbol, condition, column, val1, val2, null);
        return this;
    }

    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, String express) {
        appendCondition(dbSymbol, condition, column, null, null, express);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> select(String... columns) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> enabledRelatedCondition(Boolean enabledRelatedCondition) {
        setEnabledRelatedCondition(enabledRelatedCondition);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.EQUALS, val, null));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> ge(boolean condition, SFunction<T, ?> column, Object val) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.GREATER_THAN_EQUALS, val, null));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> le(boolean condition, SFunction<T, ?> column, Object val) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.LESS_THAN_EQUALS, val, null));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> lt(boolean condition, SFunction<T, ?> column, Object val) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.LESS_THAN, val, null));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> gt(boolean condition, SFunction<T, ?> column, Object val) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.GREATER_THAN, val, null));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> in(boolean condition, SFunction<T, ?> column, Collection<? extends Serializable> val) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.IN, val, null));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> notIn(boolean condition, SFunction<T, ?> column, Collection<? extends Serializable> val) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.NOT_IN, val, null));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> exists(boolean condition, String existsSql) {
        commonlyCondition.add(new Condition(condition, null, DbSymbol.EQUALS, existsSql));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> notExists(boolean condition, String notExistsSql) {
        commonlyCondition.add(new Condition(condition, null, DbSymbol.EQUALS, notExistsSql));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> like(boolean condition, SFunction<T, ?> column, Object val) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.LIKE, sqlConcat(SqlLike.LIKE, val)));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> notLike(boolean condition, SFunction<T, ?> column, Object val) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.NOT_LIKE, sqlConcat(SqlLike.LIKE, val)));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> likeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.LIKE, sqlConcat(SqlLike.LEFT, val)));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> likeRight(boolean condition, SFunction<T, ?> column, Object val) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.LIKE, sqlConcat(SqlLike.RIGHT, val)));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.BETWEEN, val1, val2));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> notBetween(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.NOT_BETWEEN, val1, val2));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> isNull(boolean condition, SFunction<T, ?> column) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.IS_NULL));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> isNotNull(boolean condition, SFunction<T, ?> column) {
        commonlyCondition.add(new Condition(condition, fieldToColumn(column), DbSymbol.IS_NOT_NULL));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> or(boolean condition, LambdaConditionEntity<T> conditionEntity) {
        return this;
    }

    @Override
    public LambdaConditionEntity<T> and(boolean condition, LambdaConditionEntity<T> conditionEntity) {
        return this;
    }

    @Override
    public LambdaConditionEntity<T> orderByAsc(boolean condition, SFunction<T, ?> column) {
        return this;
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> orderByAsc(boolean condition, SFunction<T, ?>... columns) {
        return this;
    }

    @Override
    public LambdaConditionEntity<T> orderByDesc(boolean condition, SFunction<T, ?> column) {
        return this;
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> orderByDesc(boolean condition, SFunction<T, ?>... columns) {
        return this;
    }


    /**
     * 函数式接口序列化解析对象
     */
    private final ColumnParseHandler<T> columnParseHandler;
    /**
     * 一般条件
     */
    private final List<Condition> commonlyCondition = new ArrayList<>();

    /**
     * 查询字段
     */
    private final List<Field> selectColumns = new ArrayList<>();

    /**
     * orderBy
     */
    private final List<Condition> orderBy = new ArrayList<>();


    public LambdaConditionEntity(Class<T> entityClass) {
        setCls(entityClass);
        setTableSqlBuilder(new TableSqlBuilder<>(entityClass, ExecuteMethod.NONE));
        columnParseHandler = new ColumnParseHandler<>(entityClass);
    }


    private Field fieldToColumn(SFunction<T, ?> func) {
        List<Field> fieldList = columnParseHandler.parseColumns(func);
        if(!fieldList.isEmpty()) {
            return fieldList.get(0);
        }
        return null;
    }

    @SafeVarargs
    private final List<Field> fieldToColumn(SFunction<T, ?>... func) {
        return columnParseHandler.parseColumns(func);
    }

}
