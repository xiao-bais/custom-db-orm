package com.custom.sqlparser;

import com.custom.annotations.DbJoinTables;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.SymbolConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/1 17:22
 * @Desc：针对select查询做sql的构建
 **/
public class HandleSelectSqlBuilder<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private StringBuilder selectSql;
    private String table;
    private String alias;
    private Class<T> entityClass;
    private boolean underlineToCamel;
    private DbKeyParserModel<T> keyParserModel;
    private List<DbFieldParserModel<T>> fieldParserModels;
    private List<DbRelationParserModel<T>> relatedParserModels;
    private List<DbJoinTableParserModel<T>> joinDbMappers;
    private List<String> joinTableParserModels;
    private Map<String, String> fieldMapper;
    private Map<String, String> columnMapper;

    public HandleSelectSqlBuilder(Class<T> entityClass, String table, String alias, boolean underlineToCamel, DbKeyParserModel<T> keyParserModel
        , List<DbFieldParserModel<T>> fieldParserModels, List<DbRelationParserModel<T>> relatedParserModels, List<DbJoinTableParserModel<T>> joinDbMappers
    ) {

    }



    /**
     * 获取查询sql（代码自行判定是否需要拼接表连接的sql）
     */
    public String getSelectSql() {
        try {
            if (JudgeUtilsAx.isEmpty(selectSql)) {
                if (CustomUtil.isDbRelationTag(this.entityClass) || this.entityClass.isAnnotationPresent(DbJoinTables.class)) {
                    getSelectRelationSql();
                } else {
                    getSelectBaseTableSql();
                }
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
            if (isRelated && (CustomUtil.isDbRelationTag(this.entityClass) || this.entityClass.isAnnotationPresent(DbJoinTables.class))) {
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
        if (keyParserModel != null) {
            baseFieldSql.add(keyParserModel.getSelectFieldSql());
        }

        // 第二步 拼接此表的其他字段
        if (!fieldParserModels.isEmpty()) {
            fieldParserModels.stream().map(DbFieldParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        // 第三步 拼接主表
        selectSql.append(String.format("select %s\n from %s %s", baseFieldSql, this.table, this.alias));
    }

    /**
     * 关联的sql分为两部分
     * 一是 @DbJoinTables注解，二是@DbRelated注解
     * 默认按注解放置顺序载入，优先加载DbJoinTables注解(顺带优先@DbMap的查询字段)
     */
    private void getSelectRelationSql() {

        StringJoiner baseFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);

        // 第一步 拼接主键
        if (keyParserModel != null) {
            baseFieldSql.add(keyParserModel.getSelectFieldSql());
        }

        // 第二步 拼接此表的其他字段
        if (!fieldParserModels.isEmpty()) {
            fieldParserModels.stream().map(DbFieldParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
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
        selectSql.append(String.format("select %s\n from %s %s", baseFieldSql, this.table, this.alias));

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
            String field = columnMapper.get(x);
            columnStr.add(field == null ? x : String.format("%s %s", x, field));
        }
        String selectSql = getSelectSql();
        selectSql = String.format("select %s\n %s", columnStr, selectSql.substring(selectSql.indexOf("from")));
        return selectSql;
    }

}
