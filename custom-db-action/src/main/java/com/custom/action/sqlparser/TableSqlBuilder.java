package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.dbaction.SqlExecuteAction;
import com.custom.action.util.DbUtil;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.SymbolConstant;
import com.custom.comm.annotations.*;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/2 14:10
 * @Desc：构建实体表的基础模板，以及提供一系列的sql语句或字段
 **/
public class TableSqlBuilder<T> implements Cloneable {

    private static final Logger logger = LoggerFactory.getLogger(TableSqlBuilder.class);

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
    private StringBuilder selectSql;
    /**
     * @Desc:插入的sql语句
     */
    private StringJoiner insertSql;
    /**
     * @Desc:插入的`?`
     */
    private StringJoiner insetSymbol;
    /**
     * @Desc:对象的所有值
     */
    private List<Object> objValues;
    /**
     * @desc:修改的sql语句
     */
    private StringBuilder updateSql;
    /**
     * @desc:对于java属性字段到表字段的映射关系
     */
    private final Map<String, String> fieldMapper = new HashMap<>();
    /**
     * @desc:对于表字段到java属性字段的映射关系
     */
    private final Map<String, String> columnMapper = new HashMap<>();

    /**
     * JDBC操作解析对象（增，删，改，查）四个对象
     * @see HandleInsertSqlBuilder
     * @see HandleDeleteSqlBuilder
     * @see HandleUpdateSqlBuilder
     * @see HandleSelectSqlBuilder
     */
    private AbstractSqlBuilder<T> sqlBuilder;

    /**
     * sql执行对象（jdbc）
     */
    private SqlExecuteAction sqlExecuteAction;

    /**
     * 获取查询sql（代码自行判定是否需要拼接表连接的sql）
     */
    public String getSelectSql() {
        try {
            if (JudgeUtilsAx.isEmpty(selectSql)) {
                if (DbUtil.isDbRelationTag(this.cls) || this.cls.isAnnotationPresent(DbJoinTables.class)) {
                    getSelectRelationSql();
                } else {
                    getSelectBaseTableSql();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return SymbolConstant.EMPTY;
        }
        return selectSql.toString();
    }

    /**
     * 获取查询sql（主动指定是否需要拼接表连接的sql）
     */
    protected String getSelectSql(boolean isRelated) {
        try {
            if (isRelated) {
                if (DbUtil.isDbRelationTag(this.cls) || this.cls.isAnnotationPresent(DbJoinTables.class)) {
                    getSelectRelationSql();
                }
            } else {
                getSelectBaseTableSql();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return SymbolConstant.EMPTY;
        }
        return selectSql.toString();
    }


    /**
     * 创建表结构
     */
    protected String geCreateTableSql() {
        StringBuilder createTableSql = new StringBuilder();
        StringJoiner fieldSql = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_1);
        if (Objects.nonNull(keyParserModel)) {
            fieldSql.add(keyParserModel.buildTableSql() + "\n");
        }

        if (!this.fieldParserModels.isEmpty()) {
            fieldParserModels.stream().map(dbFieldParserModel -> dbFieldParserModel.buildTableSql() + "\n").forEach(fieldSql::add);
        }

        createTableSql.append(String.format("create table `%s` (\n%s)", this.table, fieldSql));

        if (JudgeUtilsAx.isNotEmpty(this.desc)) {
            createTableSql.append(String.format(" COMMENT = '%s'", this.desc));
        }
        return createTableSql.toString();
    }

    /**
     * 删除表结构
     */
    protected String getDropTableSql() {
        return String.format("DROP TABLE IF EXISTS `%s`", this.table);
    }

    /**
     * 表是否存在
     */
    protected String getExitsTableSql(Class<?> cls) {
        DbTable annotation = cls.getAnnotation(DbTable.class);
        String table = annotation.table();
        return String.format("SELECT COUNT(1) COUNT FROM " +
                "`information_schema`.`TABLES` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s';", table, DbConnection.currMap.get(SymbolConstant.DATA_BASE));
    }

    /**
     * 生成表查询sql语句
     */
    private void getSelectBaseTableSql() {
        StringJoiner baseFieldSql = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);

        // 第一步 拼接主键
        if (Objects.nonNull(keyParserModel)) {
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

        StringJoiner baseFieldSql = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);

        // 第一步 拼接主键
        if (Objects.nonNull(keyParserModel)) {
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
     * 构建字段映射
     */
    private void buildMapper() {
        if (Objects.nonNull(keyParserModel)) {
            columnMapper.put(keyParserModel.getFieldSql(), keyParserModel.getKey());
            fieldMapper.put(keyParserModel.getKey(), keyParserModel.getFieldSql());
        }
        if (!fieldParserModels.isEmpty()) {
            fieldParserModels.forEach(x -> {
                columnMapper.put(x.getFieldSql(), x.getFieldName());
                fieldMapper.put(x.getFieldName(), x.getFieldSql());
            });
        }
        if (!joinDbMappers.isEmpty()) {
            joinDbMappers.forEach(x -> {
                columnMapper.put(x.getJoinName(), x.getFieldName());
                fieldMapper.put(x.getFieldName(), x.getJoinName());
            });
        }
        if (!relatedParserModels.isEmpty()) {
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

    /**
     * 默认构造方法为查询
     */
    public TableSqlBuilder(Class<T> cls, boolean underlineToCamel) {
        this(cls, ExecuteMethod.SELECT, underlineToCamel);
    }

    public TableSqlBuilder(Class<T> cls, ExecuteMethod method, boolean underlineToCamel) {
        // 初始化本对象属性
        initLocalProperty(cls, underlineToCamel);

        if (method != ExecuteMethod.NONE) {
            this.fields = CustomUtil.getFields(this.cls);
            // 构建字段解析模板
            initTableBuild(method);
        }
        // 构建字段映射缓存
        buildMapper();

        // 初始化数据结构
        initDataStructure(this);
    }

    /**
     * 初始化本对象属性
     */
    private void initLocalProperty(Class<T> cls, boolean underlineToCamel) {
        this.cls = cls;
        DbTable annotation = cls.getAnnotation(DbTable.class);
        if (Objects.isNull(annotation)) {
            ExThrowsUtil.toCustom(cls.getName() + "未标注@DbTable注解");
        }
        if (JudgeUtilsAx.isEmpty(annotation.table())) {
            ExThrowsUtil.toCustom(cls.getName() + "未指定@DbTable注解上实体映射的表名");
        }
        this.alias = annotation.alias();
        this.table = annotation.table();
        this.desc = annotation.desc();
        this.underlineToCamel = underlineToCamel;
    }

    /**
     * 构造查询模板
     */
    private void buildSelectModels() {
        DbJoinTables joinTables = this.cls.getAnnotation(DbJoinTables.class);
        if (Objects.nonNull(joinTables)) {
            Arrays.stream(joinTables.value()).map(DbJoinTable::value).forEach(joinTableParserModels::add);
        }
        DbJoinTable joinTable = this.cls.getAnnotation(DbJoinTable.class);
        if(Objects.nonNull(joinTable)) {
            joinTableParserModels.add(joinTable.value());
        }

        Field[] fields = Objects.isNull(this.fields) ? CustomUtil.getFields(this.cls) : this.fields;
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class) && Objects.isNull(keyParserModel)) {
                keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias, this.underlineToCamel);

            } else if (field.isAnnotationPresent(DbField.class)) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias, this.underlineToCamel);
                fieldParserModels.add(fieldParserModel);

            } else if (field.isAnnotationPresent(DbMapper.class)) {
                DbJoinTableParserModel<T> joinTableParserModel = new DbJoinTableParserModel<>(field);
                joinDbMappers.add(joinTableParserModel);
            } else if (field.isAnnotationPresent(DbRelated.class)) {
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
            if (field.isAnnotationPresent(DbKey.class) && Objects.isNull(keyParserModel)) {
                keyParserModel = new DbKeyParserModel<>(entity, field, this.table, this.alias, this.underlineToCamel);

            } else if (field.isAnnotationPresent(DbField.class) && isBuildUpdateModels) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(entity, field, this.table, this.alias, this.underlineToCamel);
                fieldParserModels.add(fieldParserModel);

            } else if (field.isAnnotationPresent(DbField.class)) {
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

    /**
     * 初始化数据结构
     */
    private void initDataStructure(TableSqlBuilder<T> tableSqlBuilder) {
        tableSqlBuilder.selectSql = new StringBuilder();
        tableSqlBuilder.updateSql = new StringBuilder();
        tableSqlBuilder.insertSql = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
        tableSqlBuilder.insetSymbol = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_1);
        tableSqlBuilder.objValues = new ArrayList<>();
    }

    /**
     * 实例化sql构造模板
     */
    public void buildSqlConstructorModel(ExecuteMethod method) {
        switch (method) {
            case SELECT:
                sqlBuilder = new HandleSelectSqlBuilder<>(relatedParserModels, joinDbMappers, joinTableParserModels);
                break;
            case UPDATE:
                sqlBuilder = new HandleUpdateSqlBuilder<>();
                break;
            case INSERT:
                sqlBuilder = new HandleInsertSqlBuilder<>();
                break;
            case DELETE:
                sqlBuilder = new HandleDeleteSqlBuilder<>();
        }
        if (Objects.nonNull(sqlBuilder)) {
            // 注入sql注解解析对象
            sqlBuilder.setKeyParserModel(keyParserModel);
            sqlBuilder.setFieldParserModels(fieldParserModels);
            // 初始化
            initializeSqlBuilder(sqlBuilder);
            // 注入sql执行对象
            sqlBuilder.setSqlExecuteAction(sqlExecuteAction);
        }
    }

    /**
     * 注入逻辑删除字段值
     */
    public void setLogicFieldInfo(String logicColumn, Object logicDeleteValue, Object logicNotDeleteValue) {
        sqlBuilder.setLogicColumn(logicColumn);
        sqlBuilder.setLogicDeleteValue(logicDeleteValue);
        sqlBuilder.setLogicNotDeleteValue(logicNotDeleteValue);
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
        if (Objects.nonNull(keyParserModel)) {
            keyParserModel.setEntity(entity);
        }
        if (!fieldParserModels.isEmpty()) {
            fieldParserModels.forEach(x -> x.setEntity(entity));
        }
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

    public void setSqlExecuteAction(SqlExecuteAction sqlExecuteAction) {
        this.sqlExecuteAction = sqlExecuteAction;
    }

    public AbstractSqlBuilder<T> getSqlBuilder() {
        return sqlBuilder;
    }

    private void initializeSqlBuilder(AbstractSqlBuilder<T> sqlBuilder) {
        sqlBuilder.setTable(this.table);
        sqlBuilder.setAlias(this.alias);
        sqlBuilder.setEntityClass(this.cls);
        if(Objects.nonNull(this.entity)) {
            sqlBuilder.setEntity(this.entity);
        }
        if(Objects.nonNull(this.list)) {
            sqlBuilder.setEntityList(this.list);
        }
        sqlBuilder.setFieldMapper(this.fieldMapper);
        sqlBuilder.setColumnMapper(this.columnMapper);
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
            initDataStructure(builder);
        } catch (CloneNotSupportedException e) {
            logger.error(e.toString(), e);
        }
        return builder;
    }


}
