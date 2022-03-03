package com.custom.wrapper;

import com.custom.enums.DbSymbol;
import com.custom.enums.ExecuteMethod;
import com.custom.sqlparser.TableParserModelCache;
import com.custom.sqlparser.TableSqlBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 17:17
 * @Desc：lambda表达式的条件构造对象
 **/
public class LambdaConditionEntity<T> extends AbstractWrapper<T, LambdaConditionEntity<T>> implements Wrapper<SFunction<T, ?>, LambdaConditionEntity<T>> {


    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column) {
        return null;
    }

    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val) {
        return null;
    }

    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2) {
        return null;
    }

    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, String express) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> select(String... columns) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> enabledRelatedCondition(Boolean enabledRelatedCondition) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> ge(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> le(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> lt(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> gt(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> in(boolean condition, SFunction<T, ?> column, Collection<? extends Serializable> val) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> notIn(boolean condition, SFunction<T, ?> column, Collection<? extends Serializable> val) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> exists(boolean condition, String existsSql) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> notExists(boolean condition, String notExistsSql) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> like(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> notLike(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> likeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> likeRight(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> notBetween(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> isNull(boolean condition, SFunction<T, ?> column) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> isNotNull(boolean condition, SFunction<T, ?> column) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> or(boolean condition, LambdaConditionEntity<T> conditionEntity) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> and(boolean condition, LambdaConditionEntity<T> conditionEntity) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> orderByAsc(boolean condition, SFunction<T, ?> column) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> orderByAsc(boolean condition, SFunction<T, ?>... columns) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> orderByDesc(boolean condition, SFunction<T, ?> column) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> orderByDesc(boolean condition, SFunction<T, ?>... columns) {
        return null;
    }

    @Override
    public LambdaConditionEntity<T> groupBy(boolean condition, SFunction<T, ?>... column) {
        return null;
    }


    private final ColumnParseHandler<T> columnParseHandler;


    public LambdaConditionEntity(Class<T> entityClass) {
        setCls(entityClass);
        setTableSqlBuilder(new TableSqlBuilder<>(entityClass, ExecuteMethod.NONE));
        columnParseHandler = new ColumnParseHandler<>(entityClass);
    }


    @SuppressWarnings("unchecked")
    private String fieldToColumn(SFunction<T, ?> func) {
        List<Field> fieldList = columnParseHandler.parseColumns(func);
        if(!fieldList.isEmpty()) {
            return fieldList.get(0).getName();
        }
        return null;
    }

    @SafeVarargs
    private final List<String> fieldToColumn(SFunction<T, ?>... func) {
        List<Field> fieldList = columnParseHandler.parseColumns(func);
        if(!fieldList.isEmpty()) {
            return fieldList.stream().map(Field::getName).collect(Collectors.toList());
        }
        return null;
    }



    class Condition {

    }

}
