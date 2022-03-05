package com.custom.wrapper;

import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbSymbol;
import com.custom.enums.SqlOrderBy;
import com.custom.sqlparser.TableSqlBuilder;

import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2022/3/5 23:07
 * @desc:查询条件储存
 */
public class ConditionStorage<T, OrderBy, Select> {

    /**
     * 查询字段
     */
    private List<Select> queryColumns = new ArrayList<>();

    /**
     * 排序条件存储
     */
    private OrderBy orderByColumns;

    /**
     * 实体解析模板
     */
    private TableSqlBuilder<T> tableSqlBuilder;

    /**
     * 实体Class对象
     */
    private Class<T> cls;



    public List<Select> getQueryColumns() {
        return queryColumns;
    }

    public void setQueryColumns(List<Select> queryColumns) {
        this.queryColumns = queryColumns;
    }

    public OrderBy getOrderByColumns() {
        return orderByColumns;
    }

    public void setOrderByColumns(OrderBy orderByColumns) {
        this.orderByColumns = orderByColumns;
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

    /**
     * 排序字段
     * 在条件拼接完成后，进行排序，例如：age asc, score desc
     */
    private final StringJoiner orderBy = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);

    /**
     * lambda表达式的排序条件
     */
    private final Map<SFunction<T, ?>, SqlOrderBy> lambdaOrderBy = new HashMap<>();


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

    public void setLastCondition(String lastCondition) {
        this.lastCondition = lastCondition;
    }

    public String[] getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(String[] selectColumns) {
        this.selectColumns = selectColumns;
    }

    public Boolean getEnabledRelatedCondition() {
        return enabledRelatedCondition;
    }

    public void setEnabledRelatedCondition(Boolean enabledRelatedCondition) {
        this.enabledRelatedCondition = enabledRelatedCondition;
    }

    public StringJoiner getOrderBy() {
        return orderBy;
    }

    public void setFinalConditional(StringBuilder finalConditional) {
        this.finalConditional = finalConditional;
    }
}