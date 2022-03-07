package com.custom.wrapper;

import com.custom.enums.DbSymbol;
import com.custom.enums.ExecuteMethod;
import com.custom.enums.SqlLike;
import com.custom.enums.SqlOrderBy;
import com.custom.sqlparser.TableSqlBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 17:17
 * @Desc：lambda表达式的条件构造对象
 **/
public class LambdaConditionEntity<T> extends AbstractWrapper<T, SFunction<T, ?>, LambdaConditionEntity<T>, Map<SFunction<T, ?>, SqlOrderBy>>
        implements Wrapper<SFunction<T, ?>, LambdaConditionEntity<T>> {



    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column) {
        storeCondition(new Condition(condition, fieldToColumn(column), dbSymbol));
        return this;
    }


    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String sqlCondition) {
        storeCondition(new Condition(condition, null, sqlCondition, dbSymbol));
        return this;
    }


    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, Object val) {
        storeCondition(new Condition(condition, fieldToColumn(column), dbSymbol, val, null));
        return this;
    }


    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        storeCondition(new Condition(condition, fieldToColumn(column), dbSymbol, val1, val2));
        return this;
    }


    @Override
    protected LambdaConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, SFunction<T, ?> column, String express) {
        storeCondition(new Condition(condition, fieldToColumn(column), express, dbSymbol));
        return this;
    }

    @SafeVarargs
    @Override
    public final LambdaConditionEntity<T> select(SFunction<T, ?>... columns) {
        setSelects(fieldToColumn(columns));
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
        if(condition && conditionEntity != null) {
            handleNewSelectAndOrderBy(false, conditionEntity);
        }
        return this;
    }



    @Override
    public LambdaConditionEntity<T> and(boolean condition, LambdaConditionEntity<T> conditionEntity) {
        if(condition && conditionEntity != null) {
            handleNewSelectAndOrderBy(true, conditionEntity);
        }
        return this;
    }

    private void handleNewSelectAndOrderBy(boolean isAnd, LambdaConditionEntity<T> conditionEntity) {
        conditionEntity.setAndConditionFlag(isAnd);
        if(conditionEntity.getOrderByColumns() != null) {
            if(getOrderByColumns() != null) {
                getOrderByColumns().putAll(conditionEntity.getOrderByColumns());
            }else {
                setOrderByColumns(conditionEntity.getOrderByColumns());
            }
        }
        if(conditionEntity.getSelects() != null) {
            int thisLen = getSelects().length;
            int addLen = conditionEntity.getSelects().length;
            Field[] newFields  = new Field[thisLen + addLen];
            for (int i = 0; i < newFields.length; i++) {
                if(i <= thisLen - 1) {
                    newFields[i] = getSelects()[i];
                }else {
                    newFields[i] = conditionEntity.getSelects()[i];
                }
            }
            setSelects(newFields);
        }
        this.lambdaConditionEntityList.add(conditionEntity);
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
    private Boolean andConditionFlag;

    private final List<LambdaConditionEntity<T>> lambdaConditionEntityList;

    public Boolean getAndConditionFlag() {
        return andConditionFlag;
    }

    protected void setAndConditionFlag(Boolean andConditionFlag) {
        this.andConditionFlag = andConditionFlag;
    }

    public List<LambdaConditionEntity<T>> getLambdaConditionEntityList() {
        return lambdaConditionEntityList;
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
