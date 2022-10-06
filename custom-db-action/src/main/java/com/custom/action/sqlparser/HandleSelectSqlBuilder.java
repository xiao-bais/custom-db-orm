package com.custom.action.sqlparser;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.action.util.DbUtil;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.StrUtils;
import com.custom.jdbc.GlobalDataHandler;
import com.custom.comm.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/1 17:22
 * @Desc：针对select查询做sql的构建
 **/
public class HandleSelectSqlBuilder<T> extends AbstractSqlBuilder<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StringBuilder selectSql;
    private final StringBuilder selectJoinSql;
    private final StringBuilder joinTableSql;
    private boolean primaryTable = false;
    private final List<DbRelationParserModel<T>> relatedParserModels;
    private final List<DbJoinTableParserModel<T>> joinDbMappers;
    private final List<String> joinTableParserModels;
    private final boolean existNeedInjectResult;

    public HandleSelectSqlBuilder(Class<T> entityClass) {
        TableParseModel<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        this.selectSql = new StringBuilder();
        this.selectJoinSql = new StringBuilder();
        this.joinTableSql = new StringBuilder();
        this.relatedParserModels = tableSqlBuilder.getRelatedParserModels();
        this.joinDbMappers = tableSqlBuilder.getJoinDbMappers();
        this.joinTableParserModels = tableSqlBuilder.getJoinTableParserModels();
        this.existNeedInjectResult = tableSqlBuilder.existNeedInjectResult();

        setEntityClass(entityClass);
        this.injectTableInfo(tableSqlBuilder);
    }


    /**
     * 获取查询sql（代码自行判定是否需要拼接表连接的sql）
     */
    @Override
    public String createTargetSql() {
        try {
            return this.buildSelect(!this.primaryTable).toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Constants.EMPTY;
        }
    }

    /**
     * 获取查询sql（主动指定是否需要拼接表连接的sql）
     */
    protected StringBuilder buildSelect(boolean isRelated) {
        try {
            if (StrUtils.isBlank(selectJoinSql)) {
                this.createSelectRelationSql();
            }
            if (StrUtils.isBlank(selectSql)) {
                this.createSelectBaseTableSql();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return isRelated ? this.selectJoinSql : this.selectSql;
    }

    /**
     * 生成表查询sql语句
     */
    private void createSelectBaseTableSql() {
        StringJoiner baseFieldSql = new StringJoiner(Constants.SEPARATOR_COMMA_2);

        DbKeyParserModel<T> keyParserModel = getKeyParserModel();
        // 第一步 拼接主键
        if (keyParserModel != null) {
            baseFieldSql.add(keyParserModel.getSelectFieldSql());
        }

        List<DbFieldParserModel<T>> fieldParserModels = getFieldParserModels();
        // 第二步 拼接此表的其他字段
        if (!fieldParserModels.isEmpty()) {
            fieldParserModels.stream().map(DbFieldParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        // 第三步 拼接主表
        selectSql.append(String.format(DbUtil.SELECT_TEMPLATE, baseFieldSql, getTable(), getAlias()));
    }

    /**
     * 关联的sql分为两部分
     * <br/>一是 @DbJoinTables注解，二是@DbRelated注解
     * <br/>默认按注解放置顺序载入，优先加载DbJoinTables注解(顺带优先@DbMap的查询字段)
     */
    private void createSelectRelationSql() {

        StringJoiner baseFieldSql = new StringJoiner(Constants.SEPARATOR_COMMA_2);

        // 第一步 拼接主键
        if (getKeyParserModel() != null) {
            baseFieldSql.add(getKeyParserModel().getSelectFieldSql());
        }

        // 第二步 拼接此表的其他字段
        if (!getFieldParserModels().isEmpty()) {
            getFieldParserModels().stream().map(DbFieldParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        // 第三步 拼接以joinTables的方式关联的查询字段
        if (!joinDbMappers.isEmpty()) {
            joinDbMappers.forEach(x -> baseFieldSql.add(x.getSelectFieldSql()));
        }

        // 第三步 拼接以related方式关联的查询字段
        if (!relatedParserModels.isEmpty()) {
            relatedParserModels.stream().map(DbRelationParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        // 第四步 拼接主表
        selectJoinSql.append(String.format(DbUtil.SELECT_TEMPLATE, baseFieldSql, getTable(), getAlias()));

        if (StrUtils.isBlank(joinTableSql)) {
            // 第五步 拼接以joinTables方式的关联条件
            if (!joinTableParserModels.isEmpty()) {
                joinTableParserModels.stream().map(model -> String.format("\n %s", model)).forEach(joinTableSql::append);
            }

            // 第六步 拼接以related方式的关联条件
            if (!relatedParserModels.isEmpty()) {
                joinTableSql.append(getRelatedTableSql(relatedParserModels));
            }
        }
        selectJoinSql.append(joinTableSql);
    }

    /**
     * 拼接related的表关联
     */
    private String getRelatedTableSql(List<DbRelationParserModel<T>> relatedParserModels) {
        StringBuilder joinTableSql = new StringBuilder();
        List<String> conditions = new ArrayList<>();
        for (DbRelationParserModel<T> model : relatedParserModels) {
            String condition = String.format("%s@%s@%s", model.getJoinTable(), model.getJoinAlias(), model.getCondition());
            if (!conditions.contains(condition)) {
                joinTableSql.append("\n").append(String.format("%s %s %s on %s", model.getJoinStyle().getStyle(), model.getJoinTable(), model.getJoinAlias(), model.getCondition()));
                conditions.add(condition);
            }
        }
        return joinTableSql.toString();
    }

    /**
     * 自定义查询表列名
     */
    public String selectColumns(String[] columns) {
        StringJoiner columnStr = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        for (String x : columns) {
            String column = GlobalDataHandler.hasSqlKeyword(x) ? String.format("`%s`", x) : x;
            if (Objects.nonNull(column) && !column.contains(Constants.POINT)) {
                column = getAlias() + Constants.POINT + column;
            }
            String field = getColumnMapper().get(column);
            columnStr.add(Objects.isNull(field) ? column : DbUtil.sqlSelectWrapper(column, field));
        }
        return String.format(DbUtil.SELECT_TEMPLATE, columnStr, getTable(), getAlias()) + joinTableSql;
    }

    public boolean isExistNeedInjectResult() {
        return existNeedInjectResult;
    }


    /**
     * 整合条件，获取最终可执行的sql
     */
    protected String selectExecuteSqlBuilder(ConditionWrapper<T> wrapper) throws Exception {
        this.primaryTable = wrapper.getPrimaryTable();
        StringBuilder selectSql = new StringBuilder();
        if (wrapper.getSelectColumns() != null) {
            selectSql.append(this.selectColumns(wrapper.getSelectColumns()));
        } else {
            selectSql.append(this.createTargetSql());
        }

        FullSqlConditionExecutor finalCondition = this.addLogicCondition(wrapper.getFinalConditional());
        selectSql.append(finalCondition.execute());

        // 添加自定义拼接的sql条件
        if (JudgeUtil.isNotEmpty(wrapper.getCustomizeSql())) {
            selectSql.append(wrapper.getCustomizeSql());
        }
        // group by
        if (JudgeUtil.isNotEmpty(wrapper.getGroupBy())) {
            selectSql.append(Constants.GROUP_BY).append(wrapper.getGroupBy());
        }
        // having
        if (JudgeUtil.isNotEmpty(wrapper.getHaving())) {
            selectSql.append(Constants.HAVING).append(wrapper.getHaving());
        }
        // order by
        if (CustomUtil.isNotBlank(wrapper.getOrderBy().toString())) {
            selectSql.append(Constants.ORDER_BY).append(wrapper.getOrderBy());
        }
        // order by params
        if (!wrapper.getHavingParams().isEmpty()) {
            wrapper.getParamValues().addAll(wrapper.getHavingParams());
        }
        return selectSql.toString();
    }
}
