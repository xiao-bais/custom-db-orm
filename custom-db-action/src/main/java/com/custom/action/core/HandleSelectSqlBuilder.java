package com.custom.action.core;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.action.interfaces.SqlBuilder;
import com.custom.action.util.DbUtil;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.StrUtils;
import com.custom.jdbc.configuration.GlobalDataHandler;
import com.custom.comm.utils.Constants;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 提供一系列查询记录的sql构建
 * @author   Xiao-Bai
 * @since  2022/4/1 17:22
 **/
public class HandleSelectSqlBuilder<T> extends AbstractSqlBuilder<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SqlBuilder selectSqlBuilder = null;
    private SqlBuilder selectJoinSqlBuilder = null;
    private final StringBuilder joinTableSql;
    private final List<DbRelationParserModel<T>> relatedParserModels;
    private final List<DbJoinTableParserModel<T>> joinDbMappers;
    private final List<String> joinTableParserModels;

    public HandleSelectSqlBuilder(Class<T> entityClass, JdbcSqlSessionFactory sqlSessionFactory) {
        TableParseModel<T> tableSqlBuilder = TableInfoCache.getTableModel(entityClass);
        this.joinTableSql = new StringBuilder();
        this.relatedParserModels = tableSqlBuilder.getRelatedParserModels();
        this.joinDbMappers = tableSqlBuilder.getJoinDbMappers();
        this.joinTableParserModels = tableSqlBuilder.getJoinTableParserModels();
        this.injectTableInfo(tableSqlBuilder, sqlSessionFactory);
    }


    /**
     * 获取查询sql（代码自行判定是否需要拼接表连接的sql）
     */
    @Override
    public String createTargetSql(Object obj, List<Object> sqlParams) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String createTargetSql() {
        return createTargetSql(false);
    }

    @Override
    public String createTargetSql(boolean primaryTable) {
        try {
            return this.build(!primaryTable)
                    .create(SqlExecTemplate.SELECT_LIST);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return Constants.EMPTY;
        }
    }

    /**
     * 获取查询sql（主动指定是否需要拼接表连接的sql）
     * @param isRelated 是否需要关联的sql
     */
    protected SqlBuilder build(boolean isRelated) {

        // 关联表的查询SQL
        if (JudgeUtil.isEmpty(selectJoinSqlBuilder)) {
            this.selectJoinSqlBuilder = this.createSelectRelationSql();
        }

        // 不关联的查询SQL
        if (JudgeUtil.isEmpty(selectSqlBuilder)) {
            this.selectSqlBuilder = this.createSelectBaseTableSql();
        }
        return isRelated ? this.selectJoinSqlBuilder : this.selectSqlBuilder;
    }

    /**
     * 生成表查询sql语句
     */
    private SqlBuilder createSelectBaseTableSql() {
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
        return template -> SqlExecTemplate.format(template, baseFieldSql, getTable(), getAlias());
    }


    /**
     * 关联的sql分为两部分
     * <br/>一是 @DbJoinTables注解，二是@DbRelated注解
     * <br/>默认按注解放置顺序载入，优先加载DbJoinTables注解(顺带优先@DbMap的查询字段)
     */
    private SqlBuilder createSelectRelationSql() {

        StringJoiner baseFieldSql = new StringJoiner(Constants.SEPARATOR_COMMA_2);

        // 第一步 拼接主键
        if (getKeyParserModel() != null) {
            baseFieldSql.add(getKeyParserModel().getSelectFieldSql());
        }

        // 第二步 拼接此表的其他字段
        if (!getFieldParserModels().isEmpty()) {
            getFieldParserModels().stream().map(DbFieldParserModel::getSelectFieldSql)
                    .forEach(baseFieldSql::add);
        }

        // 第三步 拼接以joinTables的方式关联的查询字段
        if (!joinDbMappers.isEmpty()) {
            joinDbMappers.forEach(x -> baseFieldSql.add(x.getSelectFieldSql()));
        }

        // 第三步 拼接以related方式关联的查询字段
        if (!relatedParserModels.isEmpty()) {
            relatedParserModels.stream().map(DbRelationParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        this.createJoinTableSql();

        return template -> SqlExecTemplate.format(template, baseFieldSql, getTable(), getAlias()) + joinTableSql;
    }



    /**
     * 创建关联表SQl(joinTable方式)
     * @see com.custom.comm.annotations.DbJoinTable
     * @see com.custom.comm.annotations.DbJoinTables
     *
     */
    private void createJoinTableSql() {
        if (StrUtils.isNotBlank(joinTableSql)) {
            return;
        }
        // 第四步 拼接以joinTables方式的关联条件
        if (!joinTableParserModels.isEmpty()) {
            joinTableParserModels.stream().map(model -> "\n " + model).forEach(joinTableSql::append);
        }

        // 第五步 拼接以related方式的关联条件
        if (!relatedParserModels.isEmpty()) {
            joinTableSql.append(getRelatedTableSql(relatedParserModels));
        }
    }



    /**
     * 拼接related的表关联
     */
    private String getRelatedTableSql(List<DbRelationParserModel<T>> relatedParserModels) {
        List<String> conditions = new ArrayList<>();
        List<SqlBuilder> joinSqlBuildList = new ArrayList<>();

        for (DbRelationParserModel<T> model : relatedParserModels) {

            // (关联表+关联表别名+关联条件) 作为唯一标识
            String condition = String.format("%s@%s@%s",
                    model.getJoinTable(), model.getJoinAlias(), model.getCondition());

            if (!conditions.contains(condition)) {
                joinSqlBuildList.add((template) -> SqlExecTemplate.format(template,
                        model.getJoinStyle().getStyle(), model.getJoinTable(), model.getJoinAlias(), model.getCondition())
                );
                conditions.add(condition);
            }
        }
        return joinSqlBuildList.stream().map(op -> op.create(SqlExecTemplate.JOIN_TABLE))
                .collect(Collectors.joining());
    }

    /**
     * 自定义查询表列名
     */
    public SqlBuilder selectColumns(List<String> columns, Boolean primaryTable) {
        StringJoiner columnStr = new StringJoiner(Constants.SEPARATOR_COMMA_2);
        for (String x : columns) {
            String column = GlobalDataHandler.hasSqlKeyword(x) ? String.format("`%s`", x) : x;
            if (Objects.nonNull(column) && !column.contains(Constants.POINT)
                && !column.contains(Constants.BRACKETS_LEFT) && !column.contains(Constants.BRACKETS_RIGHT)) {
                column = getAlias() + Constants.POINT + column;
            }
            String field = getColumnMapper().get(column);
            columnStr.add(Objects.isNull(field) ? column : DbUtil.sqlSelectWrapper(column, field));
        }

        String suffix;
        if (primaryTable) {
            suffix = Constants.EMPTY;
        }else {
            this.createJoinTableSql();
            suffix = String.valueOf(this.joinTableSql);
        }

        return template -> SqlExecTemplate.format(template, columnStr, getTable(), getAlias()) + suffix;
    }

    /**
     * 整合条件，获取最终可执行的sql
     */
    public String executeSqlBuilder(ConditionWrapper<T> wrapper) throws Exception {

        StringBuilder selectSql = new StringBuilder();
        Boolean primaryTable = wrapper.getPrimaryTable();

        if (wrapper.getSelectColumns() != null) {
            SqlBuilder sqlBuilder = this.selectColumns(wrapper.getSelectColumns(), primaryTable);
            selectSql.append(sqlBuilder.create(SqlExecTemplate.SELECT_LIST));
        } else {
            selectSql.append(this.createTargetSql(primaryTable));
        }

        FullSqlConditionExecutor finalCondition = this.addLogicCondition(wrapper.getFinalConditional());
        selectSql.append(finalCondition.execute());

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


    /**
     * 创建查询数量的SQL
     * @param wrapper 条件构造器
     */
    public String createSelectCountSql(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = this.executeSqlBuilder(wrapper);
       return SqlExecTemplate.format(SqlExecTemplate.SELECT_COUNT, selectSql);
    }




}
