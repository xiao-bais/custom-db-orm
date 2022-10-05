package com.custom.action.condition;

import com.custom.action.util.DbUtil;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.Constants;
import com.custom.comm.enums.DbSymbol;
import com.custom.comm.enums.SqlLike;
import com.custom.comm.enums.SqlOrderBy;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.jdbc.GlobalDataHandler;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
        implements ConditionSplicer<Children>, QueryFunction<Children, T, R> {


    /**
     * 适用（orderBy, is null, is not null,）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, R column);
    /**
     * 适用（exists, not exists）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, String sqlColumn);
    /**
     * 适用（eq, ge, gt, le, lt, in, not in）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, R column, Object val);
    /**
     * 适用（between，not between, like, not like）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, R column, Object val1, Object val2);

    /**
     * 适用（exists, not exists）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, R column, String expression);

    /**
     * 子类的实例化
     */
    protected abstract Children getInstance();

    /**
     * 拼接自定义的sql条件(该条件只支持拼接在where之后, group by之前)
     */
    public Children addCutsomizeSql(String customizeSql, Object... params) {
        if (JudgeUtil.isEmpty(customizeSql)) {
            return childrenClass;
        }
        this.addCustomizeSql(customizeSql);
        this.addParams(Arrays.stream(params).collect(Collectors.toList()));
        return childrenClass;
    }

    public Children addCutsomizeSql(boolean condition, String customizeSql, Object... params) {
        if (condition) {
            return addCutsomizeSql(customizeSql, params);
        }
        return childrenClass;
    }

    /**
    * 适配各种sql条件的拼接
    */
    protected void appendCondition(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2, String expression) {

        if(!condition || !appendState) {
            return;
        }
        column = this.checkedColumn(dbSymbol, column);
        // sql最终条件组装
        this.handleFinalCondition(dbSymbol, column, val1, val2, expression);

        if(CustomUtil.isNotBlank(getLastCondition())) {
            addCondition(getLastCondition());
            setLastCondition(Constants.EMPTY);
        }
        if(appendSybmol.equals(Constants.OR)) {
            appendSybmol = Constants.AND;
        }
    }

    private String checkedColumn(DbSymbol dbSymbol, String column) {
        if(CustomUtil.isBlank(column) && !ALLOW_NOT_ALIAS.contains(dbSymbol)) {
            ExThrowsUtil.toCustom("column cannot be empty");
        }
        if (GlobalDataHandler.hasSqlKeyword(column)) {
            column = GlobalDataHandler.wrapperSqlKeyword(column);
        }
        if(!column.contains(Constants.POINT)) {
            column = DbUtil.fullSqlColumn(getTableSqlBuilder().getAlias(), column);
        }
        return column;
    }

    /**
     * sql最终条件组装
     */
    private void handleFinalCondition(DbSymbol dbSymbol, String column, Object val1, Object val2, String expression) {
        switch (dbSymbol) {
            case EQUALS:
            case NOT_EQUALS:
            case LESS_THAN:
            case GREATER_THAN:
            case LESS_THAN_EQUALS:
            case GREATER_THAN_EQUALS:
                setLastCondition(DbUtil.applyCondition(appendSybmol, column, dbSymbol.getSymbol()));
                CustomUtil.addParams(getParamValues(), val1);
                break;

            case LIKE:
            case NOT_LIKE:
                setLastCondition(DbUtil.applyCondition(appendSybmol,
                        column, dbSymbol.getSymbol(), DbUtil.sqlConcat((SqlLike) val2)));
                addParams(val1);
                break;

            case IN:
            case NOT_IN:
                ConditionOnInsqlAssembly(dbSymbol, column, val1);
                break;

            case EXISTS:
            case NOT_EXISTS:
                setLastCondition(DbUtil.applyExistsCondition(appendSybmol, dbSymbol.getSymbol(), expression));
                break;

            case BETWEEN:
            case NOT_BETWEEN:
                ConditionOnSqlBetweenAssembly(dbSymbol, column, val1, val2);
                break;

            case IS_NULL:
            case IS_NOT_NULL:
                setLastCondition(DbUtil.applyIsNullCondition(appendSybmol, column, dbSymbol.getSymbol()));
                break;

            case ORDER_BY:
            case ORDER_BY_ASC:
            case ORDER_BY_DESC:
                getOrderBy().add(column);
                break;

            case GROUP_BY:
                getGroupBy().add(column);
                break;

            case HAVING:
                getHaving().append(column);
                getHavingParams().addAll((List<Object>) val1);
                break;
        }
    }

    /**
     * between not between 的条件组装
     */
    private void ConditionOnSqlBetweenAssembly(DbSymbol dbSymbol, String column, Object val1, Object val2) {
        if(!CustomUtil.isBasicType(val1) || !CustomUtil.isBasicType(val2)) {
            throw new IllegalArgumentException("val1 or val2 can only be basic types");
        }
        if(JudgeUtil.isEmpty(val1) || JudgeUtil.isEmpty(val2)) {
            throw new NullPointerException("At least one null value exists between val1 and val2");
        }
        setLastCondition(String.format(" %s %s %s", appendSybmol, column, dbSymbol.getSymbol()));
        addParams(val1, val2);
    }

    /**
     * in 、not in的条件组装
     */
    private void ConditionOnInsqlAssembly(DbSymbol dbSymbol, String column, Object val1) {
        StringJoiner symbol = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        if (CustomUtil.isBasicType(val1)) {
            addParams(val1);

        } else if (val1.getClass().isArray()) {
            int len = Array.getLength(val1);
            for (int i = 0; i < len; i++) {
                symbol.add(Constants.QUEST);
                addParams(Array.get(val1, i));
            }

        } else if (val1 instanceof Collection) {
            Collection<?> objects = (Collection<?>) val1;
            addParams(objects);
            objects.forEach(x -> symbol.add(Constants.QUEST));
        }
        setLastCondition(DbUtil.applyInCondition(appendSybmol, column, dbSymbol.getSymbol(), symbol.toString()));
    }

    /**
     * 拼接下一段大条件
     */
    protected void append(DbSymbol prefix, String condition) {
        addCondition(String.format(" %s (%s)", prefix.getSymbol(), DbUtil.trimSqlCondition(condition)));
    }

    /**
     * 拼接insql条件
     */
    protected void appendInSql(String column, DbSymbol dbSymbol, String condition, Object... params) {
        column = this.checkedColumn(dbSymbol, column);
        addCondition(DbUtil.applyInCondition(appendSybmol, column, dbSymbol.getSymbol(), condition));
        if (params.length > 0) {
            addParams(params);
        }
    }

    /**
     * sql模糊查询条件拼接
     */
    protected String sqlConcat(SqlLike sqlLike, Object val) {
        String sql = Constants.EMPTY;
        switch (sqlLike) {
            case LEFT:
                sql = Constants.PERCENT + val;
                break;
            case RIGHT:
                sql = val + Constants.PERCENT;
                break;
            case LIKE:
                sql = Constants.PERCENT + val + Constants.PERCENT;
                break;
        }
        return sql;
    }

    /**
    * 排序字段整合
    */
    protected String orderByField(String column, SqlOrderBy orderBy) {
        return DbUtil.sqlSelectWrapper(column,
                orderBy == SqlOrderBy.ASC ?
                        SqlOrderBy.ASC.getName() : SqlOrderBy.DESC.getName());
    }



    /**
     * 拼接大条件
     */
    protected Children spliceCondition(boolean condition, boolean spliceType, ConditionWrapper<T> wrapper) {
        if(condition && Objects.nonNull(wrapper)) {
            this.mergeConditionWrapper(spliceType, wrapper);
        }
        appendState = true;
        return childrenClass;
    }

    /**
     * sql查询函数执行方法
     */
    protected Children doSelectSqlFunc(Consumer<SelectFunc<T>> consumer) {
        SelectFunc<T> sqlFunc = new SelectFunc<>(getEntityClass());
        consumer.accept(sqlFunc);
        this.mergeSelect(sqlFunc.getColumns().split(String.valueOf(Constants.CENTER_LINE)));
        return childrenClass;
    }


    /**
     * 合并新的条件构造器
     */
    protected void mergeConditionWrapper(boolean spliceType, ConditionWrapper<T> conditionEntity) {

        // 1. 合并查询列-select
        if (Objects.nonNull(conditionEntity.getSelectColumns())) {
            mergeSelect(conditionEntity.getSelectColumns());
        }
        // 2. 合并添加条件-condition
        if (JudgeUtil.isNotEmpty(conditionEntity.getFinalConditional())) {
            mergeCondition(spliceType, conditionEntity);
        }

        // 3. 合并分组-group by
        if (JudgeUtil.isNotEmpty(conditionEntity.getGroupBy())) {
            mergeGroupBy(conditionEntity);
        }

        // 4. 合并-having
        if (JudgeUtil.isNotEmpty(conditionEntity.getHaving())) {
            mergeHaving(conditionEntity);
        }

        // 5. 合并排序字段-orderBy
        if (JudgeUtil.isNotEmpty(conditionEntity.getOrderBy())) {
            mergeOrderBy(conditionEntity);
        }

    }

    /**
     * 合并条件
     * 合并前：name = 'aaa'
     * 合并后：name = 'aaa' and (age > 22)
     */
    private void mergeCondition(boolean spliceType, ConditionWrapper<T> conditionEntity) {
        append(spliceType ? DbSymbol.AND : DbSymbol.OR, conditionEntity.getFinalConditional());
        addParams(conditionEntity.getParamValues());
    }

    private void mergeOrderBy(ConditionWrapper<T> conditionEntity) {
        getOrderBy().merge(conditionEntity.getOrderBy());
    }

    private void mergeGroupBy(ConditionWrapper<T> conditionEntity) {
        getGroupBy().merge(conditionEntity.getOrderBy());
    }

    private void mergeHaving(ConditionWrapper<T> conditionEntity) {
        if (JudgeUtil.isEmpty(getHaving()) && JudgeUtil.isNotEmpty(conditionEntity.getHaving())) {
            getHaving().append(conditionEntity.getHaving());
        } else if (JudgeUtil.isNotEmpty(getHaving()) && JudgeUtil.isNotEmpty(conditionEntity.getHaving())) {
            getHaving().append(String.format(" and %s ", conditionEntity.getHaving()));
        }
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
            ExThrowsUtil.toCustom("Missing paging parameter：pageIndex：%s, pageSize：%s", pageIndex, pageSize);
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
    protected static String appendSybmol = Constants.AND;
    // 允许不包含别名的sql条件
    private final static List<DbSymbol> ALLOW_NOT_ALIAS = Arrays.asList(DbSymbol.EXISTS, DbSymbol.NOT_EXISTS);
    /**
     * 拼接and or 方法时，对于后面sql条件的拼接做处理
     * 默认为true即and
     */
    protected static boolean appendState = true;



}
