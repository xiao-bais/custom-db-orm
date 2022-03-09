package com.custom.wrapper;

import com.custom.dbconfig.SymbolConst;
import com.custom.sqlparser.TableParserModelCache;
import com.custom.sqlparser.TableSqlBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2022/3/5 23:07
 * @desc:查询条件储存
 */
public abstract class ConditionWrapper<T> implements Serializable {


    /**
     * 查询的列名
     * 若是查询单表（查询的实体中(包括父类)没有@DbRelated,@DbJoinTables之类的关联注解）则column为表字段，例如 name, age
     * 若是查询关联表字段，则需附带关联表别名，例如：tp.name, tp.age
     */
    private Field[] selects;

    private String[] selectColumns;

    /**
     * 实体解析模板
     */
    private TableSqlBuilder<T> tableSqlBuilder;

    /**
     * 实体Class对象
     */
    private Class<T> cls;


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


    private final StringJoiner orderBy = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);


    public TableSqlBuilder<T> getTableSqlBuilder() {
        return tableSqlBuilder;
    }

    protected void setTableSqlBuilder(TableSqlBuilder<T> tableSqlBuilder) {
        this.tableSqlBuilder = tableSqlBuilder;
    }

    public Class<T> getCls() {
        return cls;
    }

    protected void setCls(Class<T> cls) {
        this.cls = cls;
    }

    public List<Object> getParamValues() {
        return paramValues;
    }

    public String getFinalConditional() {
        return finalConditional.toString();
    }

    public StringBuilder getFinalCondition() {
        return finalConditional;
    }

    public String getLastCondition() {
        return lastCondition;
    }

    protected void setLastCondition(String lastCondition) {
        this.lastCondition = lastCondition;
    }

    public Field[] getSelects() {
        return selects;
    }

    public void setSelects(Field[] selects) {
        this.selects = selects;
    }

    public String[] getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(String[] selectColumns) {
        this.selectColumns = selectColumns;
    }

    public StringJoiner getOrderBy() {
        return orderBy;
    }

    protected void setFinalConditional(StringBuilder finalConditional) {
        this.finalConditional = finalConditional;
    }

    protected TableSqlBuilder<T> getTableParserModelCache(Class<T> key) {
        return TableParserModelCache.getTableModel(key);
    }


    public abstract T getEntity();
}
