package com.custom.wrapper;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbSymbol;
import com.custom.enums.SqlLike;
import com.custom.exceptions.CustomCheckException;
import com.custom.sqlparser.TableSqlBuilder;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/13 9:23
 * @Desc：条件构造器抽象接口
 **/
public abstract class AbstractWrapper<T, Children> {


    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, String column);
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, String column, Object val);
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2);
    protected abstract Children adapter(DbSymbol dbSymbol, boolean condition, String column, String express);
    public abstract Children select(String... columns);
    public abstract Children enabledRelatedCondition(Boolean enabledRelatedCondition);

    private TableSqlBuilder<T> tableSqlBuilder;

    private Class<T> cls;

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
            column = String.format("%s.%s", tableSqlBuilder.getAlias(), column);
        }

        String and = SymbolConst.AND;
        switch (dbSymbol) {
            case EQUALS:
            case NOT_EQUALS:
            case LESS_THAN:
            case GREATER_THAN:
            case LESS_THAN_EQUALS:
            case GREATER_THAN_EQUALS:
                lastCondition = String.format(" %s %s %s ?", and, column, dbSymbol.getSymbol());
                paramValues.add(val1);
                break;
            case LIKE:
            case NOT_LIKE:
                lastCondition = String.format(" %s %s %s ?", and, column, dbSymbol.getSymbol());
                paramValues.add(express);
                break;
            case IN:
            case NOT_IN:
                StringJoiner symbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
                if(CustomUtil.isBasicType(val1)) {
                    paramValues.add(val1);

                }else if(val1.getClass().isArray()) {
                    int len = Array.getLength(val1);
                    for (int i = 0; i < len; i++) {
                        symbol.add(SymbolConst.QUEST);
                        paramValues.add(Array.get(val1, i));
                    }

                }else if(val1 instanceof Collection) {
                    Collection<?> objects = (Collection<?>) val1;
                    paramValues.addAll(objects);
                    objects.forEach(x -> symbol.add(SymbolConst.QUEST));
                }
                lastCondition = String.format(" %s %s %s (%s)", and, column, dbSymbol.getSymbol(), symbol);
                break;
            case EXISTS:
            case NOT_EXISTS:
                lastCondition = String.format(" %s %s (%s)", and, dbSymbol.getSymbol(), express);
                break;
            case BETWEEN:
            case NOT_BETWEEN:
                if(!CustomUtil.isBasicType(val1.getClass()) || !CustomUtil.isBasicType(val2.getClass())) {
                    throw new IllegalArgumentException("val1 or val2 can only be basic types");
                }
                if(JudgeUtilsAx.isEmpty(val1) || JudgeUtilsAx.isEmpty(val2)) {
                    throw new NullPointerException("At least one null value exists between val1 and val2");
                }
                lastCondition = String.format(" %s %s %s", and, column, dbSymbol.getSymbol());
                paramValues.add(val1);
                paramValues.add(val2);
                break;
            case IS_NULL:
            case IS_NOT_NULL:
                lastCondition = String.format(" %s %s %s", and, column, dbSymbol.getSymbol());
                break;
            case ORDER_BY:
                orderBy.add(column);
                break;
        }
        finalConditional.append(lastCondition);
    }


    /**
     * 最终的sql条件语句
     */
    private StringBuilder finalConditional = new StringBuilder();

    /**
     * 上一次的拼接条件
     */
    private String lastCondition = SymbolConst.EMPTY;

    /**
     * sql中的所有参数值
     */
    private final List<Object> paramValues = new ArrayList<>();

    /**
    * 排序字段
     * 在条件拼接完成后，进行排序，例如：age asc, score desc
    */
    private final StringJoiner orderBy = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);

    /**
     * 查询的列名
     * 若是查询单表（查询的实体中(包括父类)没有@DbRelated,@DbJoinTables之类的关联注解）则column为表字段，例如 name, age
     * 若是查询关联表字段，则需附带关联表别名，例如：tp.name, tp.age
     */
    private String[] selectColumns;

    /**
     * 在条件构造中是否开启表连接（若不开启，则使用条件构造对象时，只会以单表的格式去执行查询）
     * 默认为true，以此承接@DbRelated、DbJoinTables(DbJoinTable)注解的使用
     * 当条件为true后，条件构造器上的column参数便可填入该实体里面所关联的其他表字段
     * 除主表外，关联表在使用条件构造对象时必须带上别名：例如：tp.name
    */
    private Boolean enabledRelatedCondition = true;


    public List<Object> getParamValues() {
        return paramValues;
    }

    public String getFinalConditional() {
        return finalConditional.toString();
    }

    public String getLastCondition() {
        return lastCondition;
    }

    public void setLastCondition(String lastCondition) {
        this.lastCondition = lastCondition;
    }


    public String[] getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(String[] selectColumns) {
        this.selectColumns = selectColumns;
    }

    public TableSqlBuilder<T> getTableSqlBuilder() {
        return tableSqlBuilder;
    }

    public void setTableSqlBuilder(TableSqlBuilder<T> tableSqlBuilder) {
        this.tableSqlBuilder = tableSqlBuilder;
    }

    public Class<T> getCls() {
        return cls;
    }

    public void setCls(Class<T> cls) {
        this.cls = cls;
    }

    public StringJoiner getOrderBy() {
        return orderBy;
    }

    public Boolean getEnabledRelatedCondition() {
        return enabledRelatedCondition;
    }

    public void setEnabledRelatedCondition(Boolean enabledRelatedCondition) {
        this.enabledRelatedCondition = enabledRelatedCondition;
    }

    /**
     * 拼接下一段大条件
     */
    public void append(boolean isAppend, DbSymbol dbSymbol, String condition) {
        if(!isAppend) return;
        this.finalConditional = new StringBuilder(this.finalConditional + String.format(" %s (%s)", dbSymbol.getSymbol(), CustomUtil.trimSqlCondition(condition)));
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
    public String orderByField(String column, boolean isAsc) {
        return String.format("%s %s", column, (isAsc ? SymbolConst.ASC :SymbolConst.DESC));
    }
}
