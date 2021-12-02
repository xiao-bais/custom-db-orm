package com.custom.sqlparser;

import com.custom.annotations.*;
import com.custom.comm.CustomUtil;
import com.custom.dbconfig.SymbolConst;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/2 16:34
 * @Desc：对于@DbRelated注解的解析
 **/
public class DbRelationParserModel<T> extends AbstractTableModel<T> {

    private Class<T> cls;

    private String joinTable;

    private String joinAlias;

    private String condition;

    private String joinStyle;

    private String fieldName;

    private String column;


    public DbRelationParserModel(Class<T> cls, Field field, String table, String alias) {
        this.cls = cls;
        this.setTable(table);
        this.setAlias(alias);
        DbRelated annotation = field.getAnnotation(DbRelated.class);
        this.joinTable = annotation.joinTable();
        this.column = annotation.field();
        this.fieldName = field.getName();
        this.joinAlias = annotation.joinAlias();
        this.joinStyle = annotation.joinStyle();
        this.condition = annotation.condition();
    }

    /**
     * 关联的sql分为两部分
     * 一是 @DbJoinTables注解，二是@DbRelated注解
     * 默认按顺序载入，优先加载DbJoinTables注解(顺带优先@DbMap的查询字段)
     */
    @Override
    public String buildTableSql() {

        StringBuilder selectSql = new StringBuilder();
        // @Related注解的所有字段解析对象
        List<DbRelationParserModel<T>> relatedParserModels = new ArrayList<>();
        // @DbKey注解的解析对象
        DbKeyParserModel<T> dbKeyParserModel = null;
        // @DbField注解的所有字段解析对象
        List<DbFieldParserModel<T>> fieldParserModels = new ArrayList<>();
        // @DbJoinTables注解的所有值
        List<String> joinTableParserModels = new ArrayList<>();
        // @DbMap注解的解析
        Map<String, String> joinTableParserModelMap = new HashMap<>();

        DbJoinTables joinTables = this.cls.getAnnotation(DbJoinTables.class);
        if (joinTables != null) {
            Arrays.stream(joinTables.value()).map(DbJoinTable::value).forEach(joinTableParserModels::add);
        }

        Field[] fields = CustomUtil.getFields(this.cls);
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbRelated.class)) {
                DbRelationParserModel<T> relatedParserModel = new DbRelationParserModel<>(this.cls, field, this.getTable(), this.getAlias());
                relatedParserModels.add(relatedParserModel);
            } else if (field.isAnnotationPresent(DbKey.class)) {
                dbKeyParserModel = new DbKeyParserModel<>(field, this.getTable(), this.getAlias());
            } else if (field.isAnnotationPresent(DbField.class)) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field);
                fieldParserModels.add(fieldParserModel);
            } else if (field.isAnnotationPresent(DbMap.class)) {
                DbMap annotation = field.getAnnotation(DbMap.class);
                joinTableParserModelMap.put(CustomUtil.getJoinFieldStr(annotation.value().trim()), field.getName());
            }
        }

        StringJoiner baseFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);

        // 第一步 拼接主键
        if (dbKeyParserModel != null) {
            baseFieldSql.add(dbKeyParserModel.getSelectFieldSql());
        }

        // 第二步 拼接此表的其他字段
        if (!fieldParserModels.isEmpty()) {
            fieldParserModels.stream().map(DbFieldParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        // 第三步 拼接以joinTables的方式关联的查询字段
        if (!joinTableParserModelMap.isEmpty()) {
            joinTableParserModelMap.forEach((k, v) -> baseFieldSql.add(String.format("%s `%s`", k, v)));
        }

        // 第三步 拼接以related方式关联的查询字段
        if (!relatedParserModels.isEmpty()) {
            relatedParserModels.stream().map(DbRelationParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        // 第四步 拼接主表
        selectSql.append(String.format("select %s\n from `%s` %s \n", baseFieldSql.toString(), this.getTable(), this.getAlias()));

        // 第五步 拼接以joinTables方式的关联条件
        if (!joinTableParserModels.isEmpty()) {
            joinTableParserModels.stream().map(model -> String.format("\n %s", model)).forEach(selectSql::append);
        }

        // 第六步 拼接以related方式的关联条件
        if (!relatedParserModels.isEmpty()) {
            selectSql.append(getRelatedTableSql(relatedParserModels));
        }
        return selectSql.toString();
    }


    /**
     * 拼接related的表关联
     */
    private String getRelatedTableSql(List<DbRelationParserModel<T>> relatedParserModels) {
        StringBuilder joinTableSql = new StringBuilder();
        List<String> conditions = new ArrayList<>();
        for (DbRelationParserModel<T> model : relatedParserModels) {
            String condition = String.format("%s@%s@%s", model.joinTable, model.joinAlias, model.condition);
            if (!conditions.contains(condition)) {
                joinTableSql.append("\n").append(String.format("%s `%s` %s on %s", model.getJoinStyle(), model.getJoinTable(), model.getJoinAlias(), model.getCondition()));
                conditions.add(condition);
            }
        }
        return joinTableSql.toString();
    }

    @Override
    public Object getValue(T x, String fieldName) {
        return null;
    }

    @Override
    public String getFieldSql() {
        return String.format("%s.`%s`", this.joinAlias, this.column);
    }

    @Override
    public String getSelectFieldSql() {
        return String.format("%s.`%s` `%s`", this.joinAlias, this.column, this.fieldName);
    }


    public String getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(String joinTable) {
        this.joinTable = joinTable;
    }

    public String getJoinAlias() {
        return joinAlias;
    }

    public void setJoinAlias(String joinAlias) {
        this.joinAlias = joinAlias;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getJoinStyle() {
        return joinStyle;
    }

    public void setJoinStyle(String joinStyle) {
        this.joinStyle = joinStyle;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
