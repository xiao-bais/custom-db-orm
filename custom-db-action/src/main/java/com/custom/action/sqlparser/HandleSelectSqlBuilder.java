package com.custom.action.sqlparser;

import com.custom.action.annotations.DbJoinTables;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.util.DbUtil;
import com.custom.comm.GlobalDataHandler;
import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.SymbolConst;
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
    private final List<DbRelationParserModel<T>> relatedParserModels;
    private final List<DbJoinTableParserModel<T>> joinDbMappers;
    private final List<String> joinTableParserModels;

    public HandleSelectSqlBuilder(List<DbRelationParserModel<T>> relatedParserModels,
                                  List<DbJoinTableParserModel<T>> joinDbMappers,
                                  List<String> joinTableParserModels) {
        this.selectSql = new StringBuilder();
        this.relatedParserModels = relatedParserModels;
        this.joinDbMappers = joinDbMappers;
        this.joinTableParserModels = joinTableParserModels;
    }



    /**
     * 获取查询sql（代码自行判定是否需要拼接表连接的sql）
     */
    @Override
    public String buildSql() {
        try {
            if (JudgeUtilsAx.isNotEmpty(selectSql)) {
                return selectSql.toString();
            }
            if (DbUtil.isDbRelationTag(getEntityClass()) || getEntityClass().isAnnotationPresent(DbJoinTables.class)) {
                getSelectRelationSql();
            } else {
                getSelectBaseTableSql();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return SymbolConst.EMPTY;
        }
        return selectSql.toString();
    }

    /**
     * 获取查询sql（主动指定是否需要拼接表连接的sql）
     */
    protected String getSelectSql(boolean isRelated) {
        try {
            if (isRelated && (DbUtil.isDbRelationTag(getEntityClass()) || getEntityClass().isAnnotationPresent(DbJoinTables.class))) {
                getSelectRelationSql();
            } else {
                getSelectBaseTableSql();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return SymbolConst.EMPTY;
        }
        return selectSql.toString();
    }

    /**
     * 生成表查询sql语句
     */
    private void getSelectBaseTableSql() {
        StringJoiner baseFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);

        // 第一步 拼接主键
        if (getKeyParserModel() != null) {
            baseFieldSql.add(getKeyParserModel().getSelectFieldSql());
        }

        // 第二步 拼接此表的其他字段
        if (!getFieldParserModels().isEmpty()) {
            getFieldParserModels().stream().map(DbFieldParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        // 第三步 拼接主表
        selectSql.append(String.format("select %s\n from %s %s", baseFieldSql, getTable(), getAlias()));
    }

    /**
     * 关联的sql分为两部分
     * 一是 @DbJoinTables注解，二是@DbRelated注解
     * 默认按注解放置顺序载入，优先加载DbJoinTables注解(顺带优先@DbMap的查询字段)
     */
    private void getSelectRelationSql() {

        StringJoiner baseFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);

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
        selectSql.append(String.format("select %s\n from %s %s", baseFieldSql, getTable(), getAlias()));

        // 第五步 拼接以joinTables方式的关联条件
        if (!joinTableParserModels.isEmpty()) {
            joinTableParserModels.stream().map(model -> String.format("\n %s", model)).forEach(selectSql::append);
        }

        // 第六步 拼接以related方式的关联条件
        if (!relatedParserModels.isEmpty()) {
            selectSql.append(getRelatedTableSql(relatedParserModels));
        }
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
        StringJoiner columnStr = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        for (String x : columns) {
            String column = GlobalDataHandler.hasSqlKeyword(x) ? String.format("`%s`", x) : x;
            if(Objects.nonNull(column) && !column.contains(SymbolConst.POINT)) {
                column = getAlias() + SymbolConst.POINT + column;
            }
            String field = getColumnMapper().get(column);
            columnStr.add(field == null ? column : String.format("%s %s", column, field));
        }
        String selectSql = buildSql();
        selectSql = String.format("select %s\n %s", columnStr, selectSql.substring(selectSql.indexOf("from")));
        return selectSql;
    }

}
