package com.custom.action.wrapper;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.DbSymbol;
import com.custom.comm.enums.SqlLike;
import com.custom.comm.enums.SqlOrderBy;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/13 9:23
 * @Desc：条件适配处理
 * T：实体类型
 * R：字段类型（字段类型为String是为字符串、lambda时为SFunction函数接口）
 * Children：为继承该抽象类的子类类型
 **/
@SuppressWarnings("all")
public abstract class ConditionAssembly<T, R, Children> extends ConditionWrapper<T>
        implements ConditionSplice<Children>, QueryFunction<Children, T, R> {


    /**
     * 适用（orderBy, is null, is not null,）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, R column);
    /**
     * 适用（like，exists, not exists）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, String sqlColumn);
    /**
     * 适用（eq, ge, gt, le, lt, in, not in）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, R column, Object val);
    /**
     * 适用（between，not between）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, R column, Object val1, Object val2);
    /**
     * 适用（like, not like）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, R column, String express);

    /**
     * 子类的实例化
     */
    protected abstract Children getInstance();

    /**
    * 适配各种sql条件的拼接
    */
    protected void appendCondition(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2, String express) {

        if(!condition || !appendState) {
            return;
        }
        if(CustomUtil.isBlank(column) && DbSymbol.EXISTS != dbSymbol && DbSymbol.NOT_EXISTS != dbSymbol) {
            ExThrowsUtil.toCustom("column cannot be empty");
        }
        if(JudgeUtilsAx.isNotEmpty(column) && !column.contains(SymbolConstant.POINT)) {
            column = String.format("%s.%s", getTableSqlBuilder().getAlias(), column);
        }

        switch (dbSymbol) {
            case EQUALS:
            case NOT_EQUALS:
            case LESS_THAN:
            case GREATER_THAN:
            case LESS_THAN_EQUALS:
            case GREATER_THAN_EQUALS:
                setLastCondition(String.format(" %s %s %s ?", appendSybmol, column, dbSymbol.getSymbol()));
                getParamValues().add(val1);
                break;
            case LIKE:
            case NOT_LIKE:
                setLastCondition(String.format(" %s %s %s ?", appendSybmol, column, dbSymbol.getSymbol()));
                getParamValues().add(express);
                break;
            case IN:
            case NOT_IN:
                ConditionOnInsqlAssembly(dbSymbol, column, val1);
                break;
            case EXISTS:
            case NOT_EXISTS:
                setLastCondition(String.format(" %s %s (%s)", appendSybmol, dbSymbol.getSymbol(), express));
                break;
            case BETWEEN:
            case NOT_BETWEEN:
                ConditionOnSqlBetweenAssembly(dbSymbol, column, val1, val2);
                break;
            case IS_NULL:
            case IS_NOT_NULL:
                setLastCondition(String.format(" %s %s %s", appendSybmol, column, dbSymbol.getSymbol()));
                break;
            case ORDER_BY:
                getOrderBy().add(column);
                break;
            case GROUP_BY:
                getGroupBy().add(column);
                break;
            case HAVING:
                getHaving().append(column);
                getHavingParams().addAll((List<Object>)val1);
                break;
        }
        if(CustomUtil.isNotBlank(getLastCondition())) {
            getFinalCondition().append(getLastCondition());
            setLastCondition(SymbolConstant.EMPTY);
        }
        if(appendSybmol.equals(SymbolConstant.OR)) {
            appendSybmol = SymbolConstant.AND;
        }
    }

    /**
     * between not between 的条件组装
     */
    private void ConditionOnSqlBetweenAssembly(DbSymbol dbSymbol, String column, Object val1, Object val2) {
        if(!CustomUtil.isBasicType(val1) || !CustomUtil.isBasicType(val2)) {
            throw new IllegalArgumentException("val1 or val2 can only be basic types");
        }
        if(JudgeUtilsAx.isEmpty(val1) || JudgeUtilsAx.isEmpty(val2)) {
            throw new NullPointerException("At least one null value exists between val1 and val2");
        }
        setLastCondition(String.format(" %s %s %s", appendSybmol, column, dbSymbol.getSymbol()));
        getParamValues().add(val1);
        getParamValues().add(val2);
    }

    /**
     * in 、not in的条件组装
     */
    private void ConditionOnInsqlAssembly(DbSymbol dbSymbol, String column, Object val1) {
        StringJoiner symbol = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
        if(CustomUtil.isBasicType(val1)) {
            getParamValues().add(val1);

        }else if(val1.getClass().isArray()) {
            int len = Array.getLength(val1);
            for (int i = 0; i < len; i++) {
                symbol.add(SymbolConstant.QUEST);
                getParamValues().add(Array.get(val1, i));
            }

        }else if(val1 instanceof Collection) {
            Collection<?> objects = (Collection<?>) val1;
            getParamValues().addAll(objects);
            objects.forEach(x -> symbol.add(SymbolConstant.QUEST));
        }
        setLastCondition(String.format(" %s %s %s (%s)", appendSybmol, column, dbSymbol.getSymbol(), symbol));
    }

    /**
     * 拼接下一段大条件
     */
    protected void append(DbSymbol prefix, String condition) {
        getFinalCondition().append(String.format(" %s (%s)", prefix.getSymbol(), CustomUtil.trimSqlCondition(condition)));
    }

    /**
     * sql模糊查询条件拼接
     */
    protected String sqlConcat(SqlLike sqlLike, Object val) {
        String sql = SymbolConstant.EMPTY;
        switch (sqlLike) {
            case LEFT:
                sql = SymbolConstant.PERCENT + val;
                break;
            case RIGHT:
                sql = val + SymbolConstant.PERCENT;
                break;
            case LIKE:
                sql = SymbolConstant.PERCENT + val + SymbolConstant.PERCENT;
                break;
        }
        return sql;
    }

    /**
    * 排序字段整合
    */
    protected String orderByField(String column, SqlOrderBy orderBy) {
        return String.format("%s %s", column, (orderBy == SqlOrderBy.ASC ? SqlOrderBy.ASC.getName() : SqlOrderBy.DESC.getName()));
    }



    /**
     * 拼接大条件
     */
    protected Children spliceCondition(boolean condition, boolean spliceType, ConditionWrapper<T> wrapper) {
        if(condition && Objects.nonNull(wrapper)) {
            handleNewCondition(spliceType, wrapper);
        }
        appendState = true;
        return childrenClass;
    }


    /**
     * 添加新的条件，并合并同类项
     */
    protected void handleNewCondition(boolean spliceType, ConditionWrapper<T> conditionEntity) {

        // 1. 合并查询列-select
        if (Objects.nonNull(conditionEntity.getSelectColumns())) {
            mergeSelect(conditionEntity.getSelectColumns());
        }
        // 2. 合并添加条件-condition
        if (JudgeUtilsAx.isNotEmpty(conditionEntity.getFinalConditional())) {
            mergeCondition(spliceType, conditionEntity);
        }

        // 3. 合并排序字段-orderBy
        if (JudgeUtilsAx.isNotEmpty(conditionEntity.getOrderBy())) {
            mergeOrderBy(conditionEntity);
        }
    }

    /**
     * 合并查询列(数组合并)
     */
    protected void mergeSelect(String[] selectColumns) {
        if(Objects.isNull(selectColumns)) {
            return;
        }
        if(Objects.isNull(getSelectColumns())) {
            setSelectColumns(selectColumns);
            return;
        }
        int thisLen = getSelectColumns().length;
        int addLen = selectColumns.length;
        String[] newSelectColumns = new String[thisLen + addLen];
        for (int i = 0; i < newSelectColumns.length; i++) {
            if(i <= thisLen - 1) {
                newSelectColumns[i] = getSelectColumns()[i];
            }else {
                newSelectColumns[i] = selectColumns[i - thisLen];
            }
        }
        setSelectColumns(newSelectColumns);
    }

    /**
     * 合并条件
     * 合并前：name = 'aaa'
     * 合并后：name = 'aaa' and (age > 22)
     */
    private void mergeCondition(boolean spliceType, ConditionWrapper<T> conditionEntity) {
        append(spliceType ? DbSymbol.AND : DbSymbol.OR, conditionEntity.getFinalConditional());
        getParamValues().addAll(conditionEntity.getParamValues());
    }

    private void mergeOrderBy(ConditionWrapper<T> conditionEntity) {
        getOrderBy().merge(conditionEntity.getOrderBy());
    }


    /**
     * 合并消费类型的条件
     */
    protected Children mergeConsmerCondition(boolean condition, boolean spliceType, Consumer<Children> consumer) {
        if (condition) {
            Children instance = getInstance();
            consumer.accept(instance);
            return spliceCondition(true, spliceType, (ConditionWrapper<T>) instance);
        }
        return childrenClass;
    }

    @Override
    public Children having(boolean condition, String havingSql, Object... params) {
        appendCondition(DbSymbol.HAVING, condition, havingSql, params, null, null);
        return childrenClass;
    }

    @Override
    public Children pageParams(boolean condition, Integer pageIndex, Integer pageSize) {
        if((Objects.isNull(pageIndex) || Objects.isNull(pageSize))) {
            ExThrowsUtil.toCustom("缺少分页参数：pageIndex：" + pageIndex + ", pageSize：" + pageSize);
        }
        setPageParams(pageIndex, pageSize);
        return childrenClass;
    }

    /**
     * 本次查询是否只查询主表
     */
    public Children onlyPrimary() {
        setPrimaryTable();
        return childrenClass;
    }




    protected final Children childrenClass = (Children) this;
    protected static String appendSybmol = SymbolConstant.AND;
    /**
     * 拼接and or 方法时，对于后面方法的调用
     */
    protected static boolean appendState = true;



}
