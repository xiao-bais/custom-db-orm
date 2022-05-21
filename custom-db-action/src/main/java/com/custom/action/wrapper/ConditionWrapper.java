package com.custom.action.wrapper;

import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.comm.CustomUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

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
    private Class<T> entityClass;

    /**
     * 当前条件构造是否只进行单表查询
     */
    private Boolean primaryTable = false;

    /**
     * 最终的sql条件语句
     */
    private final StringBuilder finalConditional = new StringBuilder();

    /**
     * 上一次的拼接条件
     */
    private String lastCondition = SymbolConstant.EMPTY;

    /**
     * sql中的所有参数值
     */
    private final List<Object> paramValues = new ArrayList<>();

    /**
     * 排序
     */
    private final StringJoiner orderBy = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
    /**
     * 分组
     */
    private final StringJoiner groupBy = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
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
    private Boolean hasPageParams = false;

    /**
     * 函数式接口序列化解析对象
     */
    private ColumnParseHandler<T> columnParseHandler;

    protected TableSqlBuilder<T> getTableSqlBuilder() {
        return tableSqlBuilder;
    }

    protected void setTableSqlBuilder(TableSqlBuilder<T> tableSqlBuilder) {
        this.tableSqlBuilder = tableSqlBuilder;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    protected void setEntityClass(Class<T> entityClass) {
        if(Objects.isNull(entityClass)) {
            ExThrowsUtil.toNull("映射实体Class对象缺失");
        }
        this.entityClass = entityClass;
    }

    public List<Object> getParamValues() {
        return paramValues;
    }

    public String getFinalConditional() {
        return finalConditional.toString();
    }

    protected StringBuilder getFinalCondition() {
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

    public Integer getPageSize() {
        return pageSize;
    }

    protected TableSqlBuilder<T> getTableParserModelCache(Class<T> key) {
        return TableInfoCache.getTableModel(key);
    }

    /**
     * 参数注入后的sql条件（只针对where后面的查询条件，不带有group by, order by, having, limit 等特殊聚合条件）
     * <p>
     * 原sql(a.name = ?, params = 20)
     * </p>
     * 返回sql(a.name = 20)
     */
    public String injectParamsConditional() {
        return CustomUtil.handleExecuteSql(this.finalConditional.toString(), this.paramValues.toArray());
    }

    public boolean hasPageParams() {
        return hasPageParams;
    }

    protected void wrapperInitialize(Class<T> entityClass) {
        this.entityClass = entityClass;
        TableSqlBuilder<T> tableSqlBuilder = getTableParserModelCache(entityClass);
        setTableSqlBuilder(tableSqlBuilder);
    }

    protected void setPageParams(Integer pageIndex, Integer pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.hasPageParams = true;
    }

    /**
     * 解析函数后，得到java属性字段对应的表字段名称
     */
    @SafeVarargs
    protected final String[] parseColumn(SFunction<T, ?>... func) {
        return getColumnParseHandler().getColumn(func);
    }

    /**
     * 解析函数后，得到java属性字段对应的表字段名称
     */
    protected String parseColumn(SFunction<T, ?> func) {
        return getColumnParseHandler().getColumn(func);
    }

    public abstract T getEntity();

    public Boolean getPrimaryTable() {
        return primaryTable;
    }

    protected void setPrimaryTable() {
        this.primaryTable = true;
    }

    protected ColumnParseHandler<T> getColumnParseHandler() {
        if (Objects.isNull(columnParseHandler)) {
            this.columnParseHandler = new ColumnParseHandler<>(entityClass);
        }
        return columnParseHandler;
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
}
