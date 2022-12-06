package com.custom.action.condition;

import com.custom.action.condition.support.TableSupport;
import com.custom.action.core.TableSimpleSupport;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.util.DbUtil;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.lambda.SFunction;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Xiao-Bai
 * @date 2022/3/5 23:07
 * @desc:查询条件储存
 */
@SuppressWarnings("unchecked")
public abstract class ConditionWrapper<T> implements Serializable {


    /**
     * 查询的列名
     * 若是查询单表（查询的实体中(包括父类)没有@DbRelated,@DbJoinTables之类的关联注解）则column为表字段，例如 name, age
     * 若是查询关联表字段，则需附带关联表别名，例如：tp.name, tp.age
     */
    private String[] selectColumns;

    /**
     * 表数据支持
     */
    private TableSupport tableSupport;

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
    private StringBuilder finalConditional;

    /**
     * 上一次的拼接条件
     */
    private String lastCondition;

    /**
     * sql中的所有参数值
     */
    private List<Object> paramValues;

    /**
     * 排序
     */
    private StringJoiner orderBy;
    /**
     * 分组
     */
    private StringJoiner groupBy;
    /**
     * 筛选
     */
    private StringBuilder having;
    private List<Object> havingParams;

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

    /**
     * 自定义sql条件，只会在条件构造器的条件拼接完成之后才会拼接该条件
     */
    private StringBuilder customizeSql;


    public TableSupport getTableSupport() {
        return tableSupport;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public ConditionWrapper<T> setEntityClass(Class<T> entityClass) {
        Asserts.notNull(entityClass, "映射实体Class对象缺失");
        this.entityClass = entityClass;
        return this;
    }

    public List<Object> getParamValues() {
        return paramValues;
    }

    protected void addParams(Object param) {
        if (param instanceof Collection) {
            this.paramValues.addAll((Collection<?>) param);
            return;
        }
        this.paramValues.add(param);
    }

    protected void addParams(Object... params) {
        paramValues.addAll(Arrays.asList(params));
    }

    protected void addParams(List<Object> params) {
        this.paramValues.addAll(params);
    }

    public String getFinalConditional() {
        if (this.customizeSql != null) {
            return finalConditional.toString() + this.customizeSql;
        }
        return finalConditional.toString();
    }

    protected StringBuilder getFinalCondition() {
        return finalConditional;
    }

    protected void addCondition(String condition) {
        this.finalConditional.append(condition);
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

    public String getCustomizeSql() {
        if (this.customizeSql == null) {
            return Constants.EMPTY;
        }
        return customizeSql.toString();
    }

    protected void addCustomizeSql(String customizeSql) {
        if (this.customizeSql == null) {
            this.customizeSql = new StringBuilder();
        }
        this.customizeSql.append(Constants.WHITESPACE).append(customizeSql);
    }


    /**
     * 参数注入后的sql条件
     * <p>
     * 原sql(a.name = ?, params = 20)
     * </p>
     * 返回sql(a.name = 20)
     */
    public String injectParamsConditional() {
        StringBuilder handleSqlBuilder = new StringBuilder(this.finalConditional);

        if (JudgeUtil.isNotEmpty(this.customizeSql)) {
            handleSqlBuilder.append(this.customizeSql);
        }
        if (JudgeUtil.isNotEmpty(this.groupBy)) {
            handleSqlBuilder.append(Constants.GROUP_BY).append(this.groupBy);
        }
        if (JudgeUtil.isNotEmpty(this.having)) {
            handleSqlBuilder.append(Constants.HAVING).append(this.having);
            this.paramValues.addAll(this.havingParams);
        }
        if (JudgeUtil.isNotEmpty(this.orderBy)) {
            handleSqlBuilder.append(Constants.ORDER_BY).append(this.orderBy);
        }
        return CustomUtil.handleExecuteSql(handleSqlBuilder.toString(), this.paramValues.toArray());
    }

    public boolean hasPageParams() {
        return hasPageParams;
    }

    protected void wrapperInitialize(Class<T> entityClass, TableSupport tableSupport) {
        this.entityClass = entityClass;
        if (tableSupport == null) {
            this.tableSupport = new TableSimpleSupport<>(this.entityClass);
        }else {
            this.tableSupport = tableSupport;
        }
        this.columnParseHandler = new DefaultColumnParseHandler<>(entityClass);
        this.dataStructureInit();
    }

    protected void wrapperInitialize(Class<T> entityClass) {
        wrapperInitialize(entityClass, null);
    }

    /**
     * 结构初始化
     */
    protected void dataStructureInit() {
        this.finalConditional = new StringBuilder();
        this.lastCondition = Constants.EMPTY;
        this.paramValues = new ArrayList<>();
        this.orderBy = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        this.groupBy = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        this.having = new StringBuilder();
        this.havingParams = new ArrayList<>();
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
        return columnParseHandler.parseToColumns(Arrays.asList(func)).toArray(new String[0]);
    }

    /**
     * 解析函数后，得到java属性字段对应的表字段名称
     */
    protected String parseColumn(SFunction<T, ?> func) {
        return columnParseHandler.parseToColumn(func);
    }

    public abstract T getThisEntity();

    public Boolean getPrimaryTable() {
        return primaryTable;
    }

    protected void setPrimaryTable() {
        this.primaryTable = true;
    }

    protected void setPrimaryTable(boolean primaryTable) {
        this.primaryTable = primaryTable;
    }

    protected ColumnParseHandler<T> getColumnParseHandler() {
        return columnParseHandler;
    }

    protected void setColumnParseHandler(ColumnParseHandler<T> columnParseHandler) {
        this.columnParseHandler = columnParseHandler;
    }

    public void setTableSupport(TableSupport tableSupport) {
        this.tableSupport = tableSupport;
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
     * 返回一个可执行的对象
     */
    public ThisQuery getThisQuery() {
        StringBuilder selectSql = new StringBuilder();
        String format = "SELECT %s FROM %s %s";
        StringJoiner selectColumnSql = new StringJoiner(", ");
        if (this.selectColumns != null) {
            for (String column : selectColumns) {
                String aColumn = column;
                if (Objects.nonNull(column) && !column.contains(Constants.POINT)
                        && !column.contains(Constants.BRACKETS_LEFT) && !column.contains(Constants.BRACKETS_RIGHT)) {
                    aColumn = tableSupport.alias() + Constants.POINT + column;
                }
                String field = tableSupport.columnMap().get(aColumn);
                selectColumnSql.add(field == null ? aColumn : String.format("%s %s", aColumn, field));
            }

        }else {
            tableSupport.columnMap().forEach((key, val) -> {
                selectColumnSql.add(String.format("%s %s", key, val));
            });
        }
        selectSql.append(String.format(format, selectColumnSql, tableSupport.table(), tableSupport.alias()));

        // 拼接条件
        if (JudgeUtil.isNotEmpty(finalConditional)) {
            String finalConditional = getFinalConditional();
            selectSql.append(String.format(" WHERE (%s) ", DbUtil.trimSqlCondition(finalConditional)));
        }

        List<Object> params = new ArrayList<>(paramValues);

        // 收集剩余参数以及sql
        collectParams(selectSql, params);

        // 分页
        if (hasPageParams()) {
            selectSql.append(String.format("%s \nLIMIT %s, %s", selectSql, (pageIndex - 1) * pageSize, pageSize));
        }

        ThisQuery thisQuery = new ThisQuery();
        thisQuery.setSelectSql(selectSql.toString());
        thisQuery.setParams(params);
        return thisQuery;
    }

    /**
     * 收集所有参数
     */
    private void collectParams(StringBuilder selectSql, List<Object> params) {
        // group by
        if (JudgeUtil.isNotEmpty(groupBy)) {
            selectSql.append(Constants.GROUP_BY).append(groupBy);
        }
        // having
        if (JudgeUtil.isNotEmpty(having)) {
            selectSql.append(Constants.HAVING).append(having);
        }
        // order by
        if (JudgeUtil.isNotEmpty(orderBy)) {
            selectSql.append(Constants.ORDER_BY).append(orderBy);
        }
        // having params
        if (!JudgeUtil.isNotEmpty(havingParams)) {
            params.addAll(havingParams);
        }
    }


}
