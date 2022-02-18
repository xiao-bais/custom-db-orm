package com.custom.wrapper;

import com.custom.comm.CustomUtil;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbSymbol;
import com.custom.enums.ExecuteMethod;
import com.custom.enums.SqlLike;
import com.custom.sqlparser.DbFieldParserModel;
import com.custom.sqlparser.DbKeyParserModel;
import com.custom.sqlparser.TableSqlBuilder;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/16 14:11
 * @Desc：
 **/
public class ConditionEntity<T> extends AbstractWrapper<T, ConditionEntity<T>> implements Wrapper<String, ConditionEntity<T>> {


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
    public ConditionEntity<T> in(boolean condition, String column, Collection<? extends Serializable> val) {
        return adapter(DbSymbol.IN, condition, column, val);
    }

    @Override
    public ConditionEntity<T> notIn(boolean condition, String column, Collection<? extends Serializable> val) {
        return adapter(DbSymbol.NOT_IN, condition, column, val);
    }

    @Override
    public ConditionEntity<T> exists(boolean condition, String existsSql) {
        return adapter(DbSymbol.EXISTS, condition, null, null, existsSql);
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
    public ConditionEntity<T> or(boolean condition, ConditionEntity<T> conditionEntity) {
        super.setLastCondition(SymbolConst.EMPTY);
        super.append(condition, DbSymbol.OR, conditionEntity.getFinalConditional());
        super.getParamValues().addAll(conditionEntity.getParamValues());
        return this;
    }

    @Override
    public ConditionEntity<T> and(boolean condition, ConditionEntity<T> conditionEntity) {
        super.setLastCondition(SymbolConst.EMPTY);
        super.append(condition, DbSymbol.AND, conditionEntity.getFinalConditional());
        super.getParamValues().addAll(conditionEntity.getParamValues());
        return this;
    }



    public ConditionEntity(Class<T> entityClass) {
        setCls(entityClass);
        setTableSqlBuilder(new TableSqlBuilder<>(entityClass, ExecuteMethod.NONE));
    }

    @Override
    public ConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column) {
        appendCondition(dbSymbol, condition, column, null, null, null);
        return this;
    }

    @Override
    public ConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val) {
        appendCondition(dbSymbol, condition, column, val, null, null);
        return this;
    }

    @Override
    public ConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2) {
        appendCondition(dbSymbol, condition, column, val1, val2, null);
        return this;
    }

    @Override
    public ConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, String express) {
        appendCondition(dbSymbol, condition, column, null, null, express);
        return this;
    }

//    @Override
//    public ConditionEntity<T> select(String... columns) {
//        StringJoiner columnStr = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
//        Stream.of(columns).forEach(x ->{
//            DbKeyParserModel<T> keyParserModel = tableSqlBuilder.getKeyParserModel();
//            List<DbFieldParserModel<T>> fieldParserModels = tableSqlBuilder.getFieldParserModels();
//            if(keyParserModel != null && x.equals(keyParserModel.getDbKey())) {
//                columnStr.add(String.format("%s.%s %s", keyParserModel.getAlias(), keyParserModel.getDbKey(), keyParserModel.getKey()));
//            }else if(!fieldParserModels.isEmpty()) {
//                Optional<DbFieldParserModel<T>> firstDbFieldParserModel = fieldParserModels.stream().filter(field -> field.getColumn().equals(x)).findFirst();
//                if (firstDbFieldParserModel.isPresent()) {
//                    DbFieldParserModel<T> fieldParserModel = firstDbFieldParserModel.get();
//                    columnStr.add(String.format("%s.%s %s", fieldParserModel.getAlias(), fieldParserModel.getColumn(), fieldParserModel.getFieldName()));
//                }else {
//                    columnStr.add(String.format("%s.%s", tableSqlBuilder.getAlias(), x));
//                }
//            }else {
//                columnStr.add(String.format("%s.%s", tableSqlBuilder.getAlias(), x));
//            }
//        });
//        super.setSelectColumns(columnStr.toString());
//        return this;
//    }
}
