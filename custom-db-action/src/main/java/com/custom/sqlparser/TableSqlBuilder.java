package com.custom.sqlparser;

import com.custom.annotations.*;
import com.custom.comm.CustomUtil;
import com.custom.dbconfig.SymbolConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/2 14:10
 * @Desc：
 **/
public class TableSqlBuilder<T> {

    private static Logger logger = LoggerFactory.getLogger(TableSqlBuilder.class);

    private Class<T> cls;

    private T t;

    private String table;

    private Field[] fields;

    private DbKeyParserModel<T> keyParserModel = null;

    private List<DbFieldParserModel<T>> fieldParserModels = new ArrayList<>();

    private List<DbRelationParserModel<T>> relatedParserModels = new ArrayList<>();

    private Map<String, String> joinTableParserModelMap = new HashMap<>();

    private List<String> joinTableParserModels = new ArrayList<>();

    private StringBuilder selectSql = new StringBuilder();

    private String alias;

    private String className;


    /**
    * 获取查询sql
    */
    public String getSelectSql() {
        try {
            if (CustomUtil.isDbRelationTag(this.cls) || this.cls.isAnnotationPresent(DbJoinTables.class)) {
                getSelectRelationSql();
            }else {
                getSelectBaseTableSql();
            }
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return SymbolConst.EMPTY;
        }
        return selectSql.toString();
    }


    /**
    * 创建表
    */
    public String createTableSql() {
        StringBuilder createTableSql = new StringBuilder();
        StringJoiner fieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
        if(this.keyParserModel != null) {
            fieldSql.add(keyParserModel.buildTableSql() + "\n");
        }

        if(!this.fieldParserModels.isEmpty()) {
            fieldParserModels.stream().map(dbFieldParserModel -> dbFieldParserModel.buildTableSql() + "\n").forEach(fieldSql::add);
        }

        createTableSql.append(String.format("create table `%s` (\n%s)", this.table, fieldSql.toString()));
        return createTableSql.toString();
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
        selectSql.append(String.format("select %s\n from `%s` %s\n", baseFieldSql.toString(), this.table, this.alias));
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
        if (!joinTableParserModelMap.isEmpty()) {
            joinTableParserModelMap.forEach((k, v) -> baseFieldSql.add(String.format("%s `%s`", k, v)));
        }

        // 第三步 拼接以related方式关联的查询字段
        if (!relatedParserModels.isEmpty()) {
            relatedParserModels.stream().map(DbRelationParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        // 第四步 拼接主表
        selectSql.append(String.format("select %s\n from `%s` %s\n", baseFieldSql.toString(), this.table, this.alias));

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
                joinTableSql.append("\n").append(String.format("%s `%s` %s on %s", model.getJoinStyle(), model.getJoinTable(), model.getJoinAlias(), model.getCondition()));
                conditions.add(condition);
            }
        }
        return joinTableSql.toString();
    }


    public TableSqlBuilder(Class<T> cls, boolean isUpdate) {
        this.cls = cls;
        DbTable annotation = cls.getAnnotation(DbTable.class);
        this.alias = annotation.alias();
        this.table = annotation.table();
        this.className = cls.getSimpleName();
        this.fields = CustomUtil.getFields(this.cls);
        if (isUpdate) {
            buildUpdateModels();
        } else {
            buildSelectModels();
        }
    }

    public TableSqlBuilder(Class<T> cls) {
        this(cls, false);
    }

    public TableSqlBuilder(T t) {
        this.t = t;
    }

    /**
    * 构造查询模板
    */
    private void buildSelectModels() {
        DbJoinTables joinTables = this.cls.getAnnotation(DbJoinTables.class);
        if (joinTables != null) {
            Arrays.stream(joinTables.value()).map(DbJoinTable::value).forEach(joinTableParserModels::add);
        }
        Field[] fields = CustomUtil.getFields(this.cls);
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbRelated.class)) {
                DbRelationParserModel<T> relatedParserModel = new DbRelationParserModel<>(this.cls, field, this.table, this.alias);
                relatedParserModels.add(relatedParserModel);

            } else if (field.isAnnotationPresent(DbKey.class)) {
                keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias);

            } else if (field.isAnnotationPresent(DbField.class)) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias);
                fieldParserModels.add(fieldParserModel);

            } else if (field.isAnnotationPresent(DbMap.class)) {
                DbMap dbMap = field.getAnnotation(DbMap.class);
                joinTableParserModelMap.put(CustomUtil.getJoinFieldStr(dbMap.value().trim()), field.getName());
            }
        }
    }

    /**
    * 构造增删改模板
    */
    private void buildUpdateModels() {
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class)) {
                keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias);

            } else if (field.isAnnotationPresent(DbField.class)) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias);
                fieldParserModels.add(fieldParserModel);
            }
        }
    }


}
