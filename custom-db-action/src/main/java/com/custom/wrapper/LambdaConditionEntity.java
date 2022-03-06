package com.custom.wrapper;

import com.custom.enums.DbSymbol;
import com.custom.enums.ExecuteMethod;
import com.custom.enums.SqlLike;
import com.custom.enums.SqlOrderBy;
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
public class LambdaConditionEntity<T> extends AbstractWrapper<T, SFunction<T, ?>, LambdaConditionEntity<T>, Map<SFunction<T, ?>, SqlOrderBy>, Field>
        implements Wrapper<SFunction<T, ?>, LambdaConditionEntity<T>> {


    /**
     * 适用（orderBy, is null, is not null,）
     */
    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column) {
        storeCondition(new Condition(condition, fieldToColumn(column), dbSymbol));
        return this;
    }

    /**
     * 适用（like，exists, not exists）
     */
    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String sqlCondition) {
        storeCondition(new Condition(condition, null, sqlCondition, dbSymbol));
        return this;
    }

    /**
     * 适用（eq, ge, gt, le, lt, in, not in）
     */
    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, Object val) {
        storeCondition(new Condition(condition, fieldToColumn(column), dbSymbol, val, null));
        return this;
    }

    /**
     * 适用（between，not between）
     */
    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        storeCondition(new Condition(condition, fieldToColumn(column), dbSymbol, val1, val2));
        return this;
    }

    /**
     * 适用（like, not like）
     */
    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, String express) {
        storeCondition(new Condition(condition, fieldToColumn(column), express, dbSymbol));
        return this;
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> select(SFunction<T, ?>... columns) {
        setSelectColumns(fieldToColumn(columns));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> enabledRelatedCondition(Boolean enabledRelatedCondition) {
        setEnabledRelatedCondition(enabledRelatedCondition);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        adapter(DbSymbol.EQUALS, condition, column, val);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> ge(boolean condition, SFunction<T, ?> column, Object val) {
        adapter(DbSymbol.GREATER_THAN_EQUALS, condition, column, val);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> le(boolean condition, SFunction<T, ?> column, Object val) {
        adapter(DbSymbol.LESS_THAN_EQUALS, condition, column, val);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> lt(boolean condition, SFunction<T, ?> column, Object val) {
        adapter(DbSymbol.LESS_THAN, condition, column, val);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> gt(boolean condition, SFunction<T, ?> column, Object val) {
        adapter(DbSymbol.GREATER_THAN, condition, column, val);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> in(boolean condition, SFunction<T, ?> column, Collection<? extends Serializable> val) {
        adapter(DbSymbol.IN, condition, column, val);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> notIn(boolean condition, SFunction<T, ?> column, Collection<? extends Serializable> val) {
        adapter(DbSymbol.NOT_IN, condition, column, val);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> exists(boolean condition, String existsSql) {
        adapter(DbSymbol.EXISTS, condition, existsSql);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> notExists(boolean condition, String notExistsSql) {
        adapter(DbSymbol.EXISTS, condition, notExistsSql);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> like(boolean condition, SFunction<T, ?> column, Object val) {
        adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.LIKE, val));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> notLike(boolean condition, SFunction<T, ?> column, Object val) {
        adapter(DbSymbol.NOT_LIKE, condition, column, sqlConcat(SqlLike.LIKE, val));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> likeLeft(boolean condition, SFunction<T, ?> column, Object val) {
        adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.LEFT, val));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> likeRight(boolean condition, SFunction<T, ?> column, Object val) {
        adapter(DbSymbol.LIKE, condition, column, sqlConcat(SqlLike.RIGHT, val));
        return this;
    }

    @Override
    public LambdaConditionEntity<T> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        adapter(DbSymbol.BETWEEN, condition, column, val1, val2);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> notBetween(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        adapter(DbSymbol.NOT_BETWEEN, condition, column, val1, val2);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> isNull(boolean condition, SFunction<T, ?> column) {
        adapter(DbSymbol.IS_NULL, condition, column);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> isNotNull(boolean condition, SFunction<T, ?> column) {
        adapter(DbSymbol.IS_NOT_NULL, condition, column);
        return this;
    }

    @Override
    public LambdaConditionEntity<T> or(boolean condition, LambdaConditionEntity<T> conditionEntity) {
        if(conditionEntity != null) {
            if(conditionEntity.getOrderByColumns() != null) {
                if(getOrderByColumns() != null) {
                    getOrderByColumns().putAll(conditionEntity.getOrderByColumns());
                }else {
                    setOrderByColumns(conditionEntity.getOrderByColumns());
                }
            }
            if(!conditionEntity.getCommonlyCondition().isEmpty()) {
                getCommonlyCondition().addAll(conditionEntity.getCommonlyCondition());
            }
            if(conditionEntity.getSelectColumns() != null) {
                int thisLen = getSelectColumns().length;
                int addLen = conditionEntity.getSelectColumns().length;
                Field[] newFields  = new Field[thisLen + addLen];
                for (int i = 0; i < newFields.length; i++) {
                    if(i <= thisLen - 1) {
                        newFields[i] = getSelectColumns()[i];
                    }else {
                        newFields[i] = conditionEntity.getSelectColumns()[i];
                    }
                }
                setSelectColumns(newFields);
            }

        }
        return this;
    }

    @Override
    public LambdaConditionEntity<T> and(boolean condition, LambdaConditionEntity<T> conditionEntity) {
        return this;
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> orderByAsc(boolean condition, SFunction<T, ?>... columns) {
        if(getOrderByColumns() == null) {
            setOrderByColumns(new HashMap<>());
        }
        Arrays.stream(columns).forEach(column -> getOrderByColumns().put(column, SqlOrderBy.ASC));
        return this;
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> orderByDesc(boolean condition, SFunction<T, ?>... columns) {
        if(getOrderByColumns() == null) {
            setOrderByColumns(new HashMap<>());
        }
        Arrays.stream(columns).forEach(column -> getOrderByColumns().put(column, SqlOrderBy.DESC));
        return this;
    }


    /**
     * 函数式接口序列化解析对象
     */
    private final ColumnParseHandler<T> columnParseHandler;

    /**
     * 条件是否是添加（继续添加一个构造器（or / and））
     */
    private boolean andConditionFlag;

    private final List<LambdaConditionEntity<T>> lambdaConditionEntityList;

    public boolean isAndConditionFlag() {
        return andConditionFlag;
    }

    public void setAndConditionFlag(boolean andConditionFlag) {
        this.andConditionFlag = andConditionFlag;
    }

    public LambdaConditionEntity(Class<T> entityClass) {
        setCls(entityClass);
        setTableSqlBuilder(new TableSqlBuilder<>(entityClass, ExecuteMethod.NONE));
        columnParseHandler = new ColumnParseHandler<>(entityClass);
        lambdaConditionEntityList = new ArrayList<>();
    }


    private Field fieldToColumn(SFunction<T, ?> func) {
        Field[] fields = columnParseHandler.parseColumns(func);
        if(fields.length > 0) {
            return fields[0];
        }
        return null;
    }

    @SafeVarargs
    private final Field[] fieldToColumn(SFunction<T, ?>... func) {
        return columnParseHandler.parseColumns(func);
    }

}
