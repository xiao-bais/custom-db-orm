package com.custom.sqlparser;

import com.custom.annotations.*;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.DbFieldsConst;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.ExecuteMethod;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/2 14:10
 * @Desc：构建实体表的基础模板，以及提供一系列的sql语句或字段
 **/
public class TableSqlBuilder<T> implements Cloneable{

    private static Logger logger = LoggerFactory.getLogger(TableSqlBuilder.class);

    private Class<T> cls;

    private T entity;

    private List<T> list;

    private String table;

    private String alias;

    private String desc;

    private Field[] fields;

    private boolean underlineToCamel;
    /**
     * @Desc：对于@DbRelated注解的解析
     */
    private DbKeyParserModel<T> keyParserModel = null;
    /**
     * @desc:对于@DbField注解的解析
     */
    private List<DbFieldParserModel<T>> fieldParserModels = new ArrayList<>();
    /**
     * @Desc：对于@DbRelated注解的解析
     */
    private List<DbRelationParserModel<T>> relatedParserModels = new ArrayList<>();
    /**
     * @Desc:对于@DbJoinTables注解的解析
     */
    private List<DbJoinTableParserModel<T>> joinDbMappers = new ArrayList<>();
    /**
     * @Desc:对于@DbJoinTables注解的解析
     */
    private List<String> joinTableParserModels = new ArrayList<>();
    /**
     * @Desc:查询的sql语句
     */
    private final StringBuilder selectSql = new StringBuilder();
    /**
     * @Desc:插入的sql语句
     */
    private final StringJoiner insertSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
    /**
     * @Desc:插入的`?`
     */
    private final StringJoiner insetSymbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
    /**
     * @Desc:对象的所有值
     */
    private final List<Object> objValues = new ArrayList<>();
    /**
     * @desc:修改的sql语句
     */
    private final StringBuilder updateSql = new StringBuilder();
    /**
     * @desc:对于java属性字段到表字段的映射关系
     */
    private final Map<String, String> fieldMapper = new HashMap<>();
    /**
     * @desc:对于表字段到java属性字段的映射关系
     */
    private final Map<String, String>  columnMapper= new HashMap<>();


    /**
     * 获取查询sql（代码自行判定是否需要拼接表连接的sql）
     */
    public String getSelectSql() {
        try {
            if(JudgeUtilsAx.isEmpty(selectSql)) {
                if (CustomUtil.isDbRelationTag(this.cls) || this.cls.isAnnotationPresent(DbJoinTables.class)) {
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
    public String getSelectSql(boolean isRelated) {
        try {
            if(isRelated) {
                if (CustomUtil.isDbRelationTag(this.cls) || this.cls.isAnnotationPresent(DbJoinTables.class)) {
                    getSelectRelationSql();
                }
            }else {
                getSelectBaseTableSql();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return SymbolConst.EMPTY;
        }
        return selectSql.toString();
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

    /**
     * 获取对象所有字段的值(多个对象)
     */
    public List<Object> getManyObjValues() {
        if (objValues.isEmpty()) {
            for (T t : list) {
                if (keyParserModel != null) {
                    if (t == null) throw new NullPointerException();
                    objValues.add(keyParserModel.getValue(t));
                }
                if (!fieldParserModels.isEmpty()) {
                    fieldParserModels.forEach(x -> objValues.add(x.getValue(t)));
                }
            }
        }
        return objValues;
    }

    /**
     * 获取对象所有字段的值(单个对象)
     */
    public List<Object> getOneObjValues() {
        if (objValues.isEmpty()) {
            if (keyParserModel != null) {
                objValues.add(keyParserModel.generateKey());
            }
            if (!fieldParserModels.isEmpty()) {
                fieldParserModels.forEach(x -> objValues.add(x.getValue(entity)));
            }
        }
        return objValues;
    }

    /**
     * 获取添加sql
     */
    public String getInsertSql() {
        try {
            if (keyParserModel != null) {
                insertSql.add(keyParserModel.getDbKey());
            }
            if (!fieldParserModels.isEmpty()) {
                fieldParserModels.forEach(x -> insertSql.add(x.getColumn()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return SymbolConst.EMPTY;
        }
        return String.format("insert into %s(%s) values %s ", this.table, insertSql.toString(), getInsertSymbol());
    }

    /**
     * 获取添加的？
     */
    private String getInsertSymbol() {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            StringJoiner brackets = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1, SymbolConst.BRACKETS_LEFT, SymbolConst.BRACKETS_RIGHT);
            if (keyParserModel != null) {
                brackets.add(SymbolConst.QUEST);
            }
            if (!fieldParserModels.isEmpty()) {
                fieldParserModels.forEach(x -> brackets.add(SymbolConst.QUEST));
            }
            insetSymbol.add(brackets.toString());
        }
        return insetSymbol.toString();
    }


    /**
     * 创建表结构
     */
    public String geCreateTableSql() {
        StringBuilder createTableSql = new StringBuilder();
        StringJoiner fieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
        if (this.keyParserModel != null) {
            fieldSql.add(keyParserModel.buildTableSql() + "\n");
        }

        if (!this.fieldParserModels.isEmpty()) {
            fieldParserModels.stream().map(dbFieldParserModel -> dbFieldParserModel.buildTableSql() + "\n").forEach(fieldSql::add);
        }

        createTableSql.append(String.format("create table `%s` (\n%s)", this.table, fieldSql));

        if(JudgeUtilsAx.isNotEmpty(this.desc)) {
            createTableSql.append(String.format(" COMMENT = '%s'", this.desc));
        }
        return createTableSql.toString();
    }

    /**
     * 删除表结构
     */
    public String getDropTableSql() {
        return String.format("DROP TABLE IF EXISTS `%s`", this.table);
    }

    /**
    * 表是否存在
    */
    public String getExitsTableSql(Class<?> cls) {
        DbTable annotation = cls.getAnnotation(DbTable.class);
        String table = annotation.table();
        return String.format("SELECT COUNT(1) COUNT FROM " +
                "`information_schema`.`TABLES` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s';", table, ExceptionConst.currMap.get(DbFieldsConst.DATA_BASE));
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
     * 构建修改的sql语句
     */
    public void buildUpdateSql(String[] updateDbFields, String logicDeleteQuerySql) {
        StringJoiner updateFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        if(updateDbFields.length > 0) {
            for (String field : updateDbFields) {
                Optional<DbFieldParserModel<T>> updateFieldOP = fieldParserModels.stream().filter(x -> x.getColumn().equals(field)).findFirst();
                updateFieldOP.ifPresent(op -> {
                    updateFieldSql.add(String.format("%s = ?", op.getFieldSql()));
                    objValues.add(op.getValue());
                });
            }
        }else {
            fieldParserModels.forEach(x -> {
                Object value = x.getValue();
                if (value != null) {
                    updateFieldSql.add(String.format("%s = ?", x.getFieldSql()));
                    objValues.add(value);
                }
            });
        }
        updateSql.append(SymbolConst.UPDATE).append(table).append(" ").append(alias)
                .append(SymbolConst.SET).append(updateFieldSql).append(SymbolConst.WHERE)
                .append(getLogicUpdateSql(keyParserModel.getFieldSql(), logicDeleteQuerySql));
        objValues.add(keyParserModel.getValue(entity));
    }

    /**
     * 获取修改的逻辑删除字段sql
     */
    public String getLogicUpdateSql(String key, String logicDeleteQuerySql) {
        return JudgeUtilsAx.isNotBlank(logicDeleteQuerySql) ? String.format("%s and %s = ?", logicDeleteQuerySql, key) : String.format("%s = ?", key);
    }

    /**
     * 构建修改的sql字段语句
     */
    public void buildUpdateField(String condition, List<Object> conditionVals) {
        StringJoiner updateFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        for (DbFieldParserModel<T> fieldParserModel : fieldParserModels) {
            Object value = fieldParserModel.getValue();
            if(value != null) {
                updateFieldSql.add(fieldParserModel.getFieldSql() + " = ?");
                objValues.add(value);
            }
        }
        updateSql.append(SymbolConst.UPDATE).append(table).append(" ").append(alias)
                .append(SymbolConst.SET).append(updateFieldSql).append(" ").append(condition);
        objValues.addAll(conditionVals);
    }


    /**
     * 构建字段映射
     */
    private void buildMapper() {
        if(keyParserModel != null) {
            columnMapper.put(keyParserModel.getFieldSql(), keyParserModel.getKey());
            fieldMapper.put(keyParserModel.getKey(), keyParserModel.getFieldSql());
        }
        if(!fieldParserModels.isEmpty()) {
            fieldParserModels.forEach(x -> {
                columnMapper.put(x.getFieldSql(), x.getFieldName());
                fieldMapper.put(x.getFieldName(), x.getFieldSql());
            });
        }
        if(!joinDbMappers.isEmpty()) {
            joinDbMappers.forEach(x -> {
                columnMapper.put(x.getJoinName(), x.getFieldName());
                fieldMapper.put(x.getFieldName(), x.getJoinName());
            });
        }
        if(!relatedParserModels.isEmpty()) {
            relatedParserModels.forEach(x -> {
                columnMapper.put(x.getFieldSql(), x.getFieldName());
                fieldMapper.put(x.getFieldName(), x.getFieldSql());
            });
        }
    }


    /**
     * 初始化
     */
    void initTableBuild(ExecuteMethod method) {
        switch (method) {
            case NONE:
                break;
            case SELECT:
                buildSelectModels();
                break;
            case UPDATE:
                buildUpdateModels(true);
            case INSERT:
                buildUpdateModels(false);
                break;
            case DELETE:
                buildDeleteModels();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + method);
        }
    }

    public TableSqlBuilder(){}
    /**
     * 默认构造方法为查询
     */
    public TableSqlBuilder(Class<T> cls, boolean underlineToCamel) {
        this(cls, ExecuteMethod.SELECT, underlineToCamel);
    }

    public TableSqlBuilder(Class<T> cls, ExecuteMethod method, boolean underlineToCamel) {
        this.cls = cls;
        DbTable annotation = cls.getAnnotation(DbTable.class);
        if(annotation == null) {
            throw new CustomCheckException(cls.getName() + "未标注@DbTable注解");
        }
        if(JudgeUtilsAx.isEmpty(annotation.table())) {
            throw new CustomCheckException(cls.getName() + "未指定@DbTable注解上实体映射的表名");
        }
        this.alias = annotation.alias();
        this.table = annotation.table();
        this.desc = annotation.desc();
        this.underlineToCamel = underlineToCamel;
        if(method != ExecuteMethod.NONE) {
            this.fields = CustomUtil.getFields(this.cls);
            initTableBuild(method);
        }
        buildMapper();
    }

    /**
     * 构造查询模板
     */
    private void buildSelectModels() {
        DbJoinTables joinTables = this.cls.getAnnotation(DbJoinTables.class);
        if (joinTables != null) {
            Arrays.stream(joinTables.value()).map(DbJoinTable::value).forEach(joinTableParserModels::add);
        }
        Field[] fields = this.fields == null ? CustomUtil.getFields(this.cls) : this.fields;
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class) && keyParserModel == null) {
                keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias, this.underlineToCamel);

            } else if (field.isAnnotationPresent(DbField.class)) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias, this.underlineToCamel);
                fieldParserModels.add(fieldParserModel);

            } else if (field.isAnnotationPresent(DbMapper.class)) {
                DbJoinTableParserModel<T> joinTableParserModel = new DbJoinTableParserModel<>(field, this.underlineToCamel);
                joinDbMappers.add(joinTableParserModel);
            }else if (field.isAnnotationPresent(DbRelated.class)) {
                DbRelationParserModel<T> relatedParserModel = new DbRelationParserModel<>(this.cls, field, this.table, this.alias, this.underlineToCamel);
                relatedParserModels.add(relatedParserModel);

            }
        }
    }

    /**
     * 构造增改模板
     */
    private void buildUpdateModels(boolean isBuildUpdateModels) {
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class) && keyParserModel == null) {
                keyParserModel = new DbKeyParserModel<>(entity, field, this.table, this.alias, this.underlineToCamel);

            } else if (field.isAnnotationPresent(DbField.class) && isBuildUpdateModels) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(entity, field, this.table, this.alias, this.underlineToCamel);
                fieldParserModels.add(fieldParserModel);

            }else if (field.isAnnotationPresent(DbField.class)) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias, this.underlineToCamel);
                fieldParserModels.add(fieldParserModel);
            }
        }
    }

    /**
     * 构造删除模板
     */
    private void buildDeleteModels() {
        Optional<Field> fieldOptional = Arrays.stream(fields).filter(x -> x.isAnnotationPresent(DbKey.class)).findFirst();
        fieldOptional.ifPresent(field -> keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias, this.underlineToCamel));
    }

    public String getTable() {
        return table;
    }

    public String getAlias() {
        return alias;
    }

    public DbKeyParserModel<T> getKeyParserModel() {
        return keyParserModel;
    }

    public List<DbFieldParserModel<T>> getFieldParserModels() {
        return fieldParserModels;
    }

    public List<DbRelationParserModel<T>> getRelatedParserModels() {
        return relatedParserModels;
    }

    public List<Object> getObjValues() {
        return objValues;
    }

    public StringBuilder getUpdateSql() {
        return updateSql;
    }

    public List<String> getJoinTableParserModels() {
        return joinTableParserModels;
    }

    public Class<T> getCls() {
        return cls;
    }

    public T getEntity() {
        return entity;
    }

    public List<T> getList() {
        return list;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setCls(Class<T> cls) {
        this.cls = cls;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public void setKeyParserModel(DbKeyParserModel<T> keyParserModel) {
        this.keyParserModel = keyParserModel;
    }

    public void setFieldParserModels(List<DbFieldParserModel<T>> fieldParserModels) {
        this.fieldParserModels = fieldParserModels;
    }

    public void setRelatedParserModels(List<DbRelationParserModel<T>> relatedParserModels) {
        this.relatedParserModels = relatedParserModels;
    }

    public void setJoinTableParserModels(List<String> joinTableParserModels) {
        this.joinTableParserModels = joinTableParserModels;
    }

    protected void setJoinDbMappers(List<DbJoinTableParserModel<T>> joinDbMappers) {
        this.joinDbMappers = joinDbMappers;
    }

    public List<DbJoinTableParserModel<T>> getJoinDbMappers() {
        return joinDbMappers;
    }

    public Map<String, String> getFieldMapper() {
        return fieldMapper;
    }

    public Map<String, String> getColumnMapper() {
        return columnMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TableSqlBuilder<T> clone() {
        TableSqlBuilder<T> builder = null;
        try {
            builder = (TableSqlBuilder<T>) super.clone();
            builder.setAlias(this.alias);
            builder.setTable(this.table);
            builder.setCls(this.cls);
            builder.setEntity(this.entity);
            builder.setKeyParserModel(this.keyParserModel);
            builder.setFieldParserModels(this.fieldParserModels);
            builder.setRelatedParserModels(this.relatedParserModels);
            builder.setJoinTableParserModels(this.joinTableParserModels);
            builder.setJoinDbMappers(this.joinDbMappers);
        } catch (CloneNotSupportedException e) {
            logger.error(e.toString(), e);
        }
        return builder;
    }



}
