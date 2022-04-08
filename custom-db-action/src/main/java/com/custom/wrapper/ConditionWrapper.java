package com.custom.wrapper;

import com.custom.comm.CustomUtil;
import com.custom.dbconfig.SymbolConst;
import com.custom.sqlparser.TableInfoCache;
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
    private final StringBuilder finalConditional = new StringBuilder();

    /**
     * 上一次的拼接条件
     */
    private String lastCondition = SymbolConst.EMPTY;

    /**
     * sql中的所有参数值
     */
    private final List<Object> paramValues = new ArrayList<>();

    /**
     * 排序
     */
    private final StringJoiner orderBy = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
    /**
     * 分组
     */
    private final StringJoiner groupBy = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
    /**
     * 筛选
     */
    private final StringBuilder having = new StringBuilder();
    private final List<Object> havingParams = new ArrayList<>();

    /**
     * 分页
     */
    private Integer pageIndex;
    private Integer pageSize;

    protected TableSqlBuilder<T> getTableSqlBuilder() {
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

    protected String getLastCondition() {
        return lastCondition;
    }

    protected void setLastCondition(String lastCondition) {
        this.lastCondition = lastCondition;
    }

    public String[] getSelectColumns() {
        return selectColumns;
    }

    protected void setSelectColumns(String[] selectColumns) {
        this.selectColumns = selectColumns;
    }

    public StringJoiner getOrderBy() {
        return orderBy;
    }

    public StringJoiner getGroupBy() {
        return groupBy;
    }

    public StringBuilder getHaving() {
        return having;
    }

    public List<Object> getHavingParams() {
        return havingParams;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    protected void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    protected void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    protected TableSqlBuilder<T> getTableParserModelCache(Class<T> key) {
        return TableInfoCache.getTableModel(key);
    }

    /**
     * 参数注入后的sql条件
     * 原sql(a.name = ?, params = 20)
     * 返回sql(a.name = 20)
     */
    public String getInjectorParamsFinalConditional() {
        return CustomUtil.handleExecuteSql(this.finalConditional.toString(), this.paramValues.toArray());
    }

    public abstract T getEntity();

}
