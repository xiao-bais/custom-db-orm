package com.custom.wrapper;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbSymbol;
import com.custom.enums.SqlLike;
import com.custom.enums.SqlOrderBy;
import com.custom.exceptions.CustomCheckException;
import com.custom.sqlparser.TableSqlBuilder;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/13 9:23
 * @Desc：条件构造器抽象接口，
 * T：实体类型
 * R：字段类型（字段类型为String是为字符串、lambda时为SFunction函数接口）
 * Children：为继承该抽象类的子类类型
 **/
public abstract class AbstractWrapper<T, R, Children, OrderBy, Select> extends ConditionStorage<T, OrderBy, Select> {

    /**
     * 适用（orderBy, is null, is not null,）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, R column);
    /**
     * 适用（like，exists, not exists）
     */
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, String sqlCondition);
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
    public abstract Children select(R... columns);
    public abstract Children enabledRelatedCondition(Boolean enabledRelatedCondition);

    /**
    * 适配各种sql条件的拼接
    */
    public void appendCondition(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2, String express) {

        if(!condition) {
            return;
        }
        if(CustomUtil.isBlank(column)) {
            throw new CustomCheckException("column cannot be empty");
        }
        if(!column.contains(SymbolConst.POINT)) {
            column = String.format("%s.%s", getTableSqlBuilder().getAlias(), column);
        }

        String and = SymbolConst.AND;
        switch (dbSymbol) {
            case EQUALS:
            case NOT_EQUALS:
            case LESS_THAN:
            case GREATER_THAN:
            case LESS_THAN_EQUALS:
            case GREATER_THAN_EQUALS:
                setLastCondition(String.format(" %s %s %s ?", and, column, dbSymbol.getSymbol()));
                getParamValues().add(val1);
                break;
            case LIKE:
            case NOT_LIKE:
                setLastCondition(String.format(" %s %s %s ?", and, column, dbSymbol.getSymbol()));
                getParamValues().add(express);
                break;
            case IN:
            case NOT_IN:
                StringJoiner symbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
                if(CustomUtil.isBasicType(val1)) {
                    getParamValues().add(val1);

                }else if(val1.getClass().isArray()) {
                    int len = Array.getLength(val1);
                    for (int i = 0; i < len; i++) {
                        symbol.add(SymbolConst.QUEST);
                        getParamValues().add(Array.get(val1, i));
                    }

                }else if(val1 instanceof Collection) {
                    Collection<?> objects = (Collection<?>) val1;
                    getParamValues().addAll(objects);
                    objects.forEach(x -> symbol.add(SymbolConst.QUEST));
                }
                setLastCondition(String.format(" %s %s %s (%s)", and, column, dbSymbol.getSymbol(), symbol));
                break;
            case EXISTS:
            case NOT_EXISTS:
                setLastCondition(String.format(" %s %s (%s)", and, dbSymbol.getSymbol(), express));
                break;
            case BETWEEN:
            case NOT_BETWEEN:
                if(!CustomUtil.isBasicType(val1.getClass()) || !CustomUtil.isBasicType(val2.getClass())) {
                    throw new IllegalArgumentException("val1 or val2 can only be basic types");
                }
                if(JudgeUtilsAx.isEmpty(val1) || JudgeUtilsAx.isEmpty(val2)) {
                    throw new NullPointerException("At least one null value exists between val1 and val2");
                }
                setLastCondition(String.format(" %s %s %s", and, column, dbSymbol.getSymbol()));
                getParamValues().add(val1);
                getParamValues().add(val2);
                break;
            case IS_NULL:
            case IS_NOT_NULL:
                setLastCondition(String.format(" %s %s %s", and, column, dbSymbol.getSymbol()));
                break;
            case ORDER_BY:
                getOrderBy().add(column);
                break;
        }
        getFinalCondition().append(getLastCondition());
    }

    /**
     * 条件暂存(where后面的条件)
     */
    protected void storeCondition(Condition condition) {
        if(condition.cond) {
            commonlyCondition.add(condition);
        }
    }



    /**
     * 拼接下一段大条件
     */
    public void append(boolean isAppend, DbSymbol dbSymbol, String condition) {
        if(!isAppend) return;
        getFinalCondition().append(String.format(" %s (%s)", dbSymbol.getSymbol(), CustomUtil.trimSqlCondition(condition)));
    }

    /**
     * sql模糊查询条件拼接
     */
    public String sqlConcat(SqlLike sqlLike, Object val) {
        String sql = SymbolConst.EMPTY;
        switch (sqlLike) {
            case LEFT:
                sql = SymbolConst.PERCENT + val;
                break;
            case RIGHT:
                sql = val + SymbolConst.PERCENT;
                break;
            case LIKE:
                sql = SymbolConst.PERCENT + val + SymbolConst.PERCENT;
                break;
        }
        return sql;
    }

    /**
    * 排序字段整合
    */
    public String orderByField(String column, SqlOrderBy orderBy) {
        return String.format("%s %s", column, (orderBy == SqlOrderBy.ASC ? SqlOrderBy.ASC.getName() : SqlOrderBy.DESC.getName()));
    }

    private final List<Condition> commonlyCondition = new ArrayList<>();

    public List<Condition> getCommonlyCondition() {
        return commonlyCondition;
    }


    /**
     * 条件对象
     */
    public static class Condition {

        final boolean cond;
        /**
         * 条件字段属性
         */
        final Field field;
        /**
         * 条件
         */
        final DbSymbol dbSymbol;

        /**
         * 参数值1
         */
        Object val1;

        /**
         * 参数值2
         */
        Object val2;

        /**
         * 表达式
         */
        String express;

        Condition(boolean cond, Field field, DbSymbol dbSymbol, Object val1, Object val2) {
            this.cond = cond;
            this.field = field;
            this.dbSymbol = dbSymbol;
            this.val1 = val1;
            this.val2 = val2;
        }

        Condition(boolean cond, Field field, String express, DbSymbol dbSymbol) {
            this.cond = cond;
            this.field = field;
            this.dbSymbol = dbSymbol;
            this.express = express;
        }

        Condition(boolean cond, Field field, DbSymbol dbSymbol) {
            this.cond = cond;
            this.field = field;
            this.dbSymbol = dbSymbol;
        }

        public Field getField() {
            return field;
        }

        public DbSymbol getDbSymbol() {
            return dbSymbol;
        }

        public Object getVal1() {
            return val1;
        }

        public Object getVal2() {
            return val2;
        }

        public String getExpress() {
            return express;
        }
    }
}
