package com.custom.action.sqlparser;

import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.annotations.*;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbConnection;
import com.custom.configuration.DbDataSource;
import com.custom.jdbc.CustomConfigHelper;
import com.custom.jdbc.GlobalDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/2 14:10
 * @Desc：构建实体表的基础模板，以及提供一系列的sql语句或字段
 **/
public class TableParseModel<T> implements Cloneable {

    private static final Logger logger = LoggerFactory.getLogger(TableParseModel.class);

    /**
     * 实体表的class对象
     */
    private Class<T> entityClass;

    /**
     * 表名称
     */
    private String table;

    /**
     * 表别名
     */
    private String alias;

    /**
     * 表说明
     */
    private String desc;

    /**
     * 表与实体对应的字段
     */
    private Field[] fields;

    /**
     * 驼峰转下划线
     */
    private boolean underlineToCamel;

    /**
     * 当子类跟父类同时标注了@DbJoinTable(s)注解时，是否在查询时向上查找父类的@DbJoinTable(s)注解，且合并关联条件
     */
    private boolean findUpDbJoinTables;
    /**
     * 对于{@link DbKey}注解的解析
     */
    private DbKeyParserModel<T> keyParserModel = null;
    /**
     * 对于{@link DbField}注解的解析
     */
    private final List<DbFieldParserModel<T>> fieldParserModels = new ArrayList<>();
    /**
     * 对于{@link DbRelated}注解的解析
     */
    private final List<DbRelationParserModel<T>> relatedParserModels = new ArrayList<>();
    /**
     * 对于{@link DbJoinTables}注解的解析
     */
    private final List<DbJoinTableParserModel<T>> joinDbMappers = new ArrayList<>();
    /**
     * 对于{@link DbJoinTables}注解的解析
     */
    private final List<String> joinTableParserModels = new ArrayList<>();

    /**
     * 对于java属性字段到表字段的映射关系
     */
    private final Map<String, String> fieldMapper = new HashMap<>();
    /**
     * 对于表字段到java属性字段的映射关系
     */
    private final Map<String, String> columnMapper = new HashMap<>();

    /**
     * 一对一字段
     */
    private List<Field> oneToOneFieldList;

    /**
     * 一对多字段
     */
    private List<Field> oneToManyFieldList;

    /**
     * 全字段解析对象
     */
    private List<ColumnPropertyMap<T>> columnPropertyMaps;


    /**
     * 创建表结构
     */
    public String createTableSql() {
        StringBuilder createTableSql = new StringBuilder();
        StringJoiner fieldSql = new StringJoiner(Constants.SEPARATOR_COMMA_1);
        if (Objects.nonNull(keyParserModel)) {
            fieldSql.add(keyParserModel.createTableSql() + "\n");
        }

        if (!this.fieldParserModels.isEmpty()) {
            fieldParserModels.stream()
                    .filter(DbFieldParserModel::isExistsDbField)
                    .map(dbFieldParserModel -> dbFieldParserModel.createTableSql() + "\n").forEach(fieldSql::add);
        }

        createTableSql.append(String.format("create table `%s` (\n%s)", this.table, fieldSql));

        if (JudgeUtil.isNotEmpty(this.desc)) {
            createTableSql.append(String.format(" comment = '%s'", this.desc));
        }
        return createTableSql.toString();
    }

    /**
     * 删除表结构
     */
    protected String dropTableSql() {
        return String.format("DROP TABLE IF EXISTS `%s`", this.table);
    }

    /**
     * 表是否存在
     */
    public String exitsTableSql(Class<?> cls) {
        DbTable annotation = cls.getAnnotation(DbTable.class);
        String table = annotation.table();
        return String.format("SELECT COUNT(1) COUNT FROM " +
                "`information_schema`.`TABLES` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s';",
                table, DbConnection.currMap.get(Constants.DATA_BASE));
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
    TableParseModel(Class<T> cls, boolean underlineToCamel) {
        this(cls, ExecuteMethod.SELECT, underlineToCamel);
    }

    TableParseModel(Class<T> cls, ExecuteMethod method, boolean underlineToCamel) {
        // 初始化本对象属性
        initLocalProperty(cls, underlineToCamel);

        if (method != ExecuteMethod.NONE) {
            this.fields = this.findUpDbJoinTables ? CustomUtil.loadFields(this.entityClass) : this.entityClass.getDeclaredFields();

            // 构建字段解析模板
            this.initTableBuild(method);

            // 创建字段属性映射
            try {
                this.createColumnPropertyMaps();
            } catch (IntrospectionException e) {
                logger.error(e.toString(), e);
            }

        }
        // 构建字段映射缓存
        this.buildMapper();
    }

    /**
     * 初始化本对象属性
     */
    private void initLocalProperty(Class<T> cls, boolean underlineToCamel) {
        this.entityClass = cls;
        DbTable annotation = cls.getAnnotation(DbTable.class);
        if (Objects.isNull(annotation)) {
            ExThrowsUtil.toCustom(cls.getName() + " 未标注@DbTable注解");
        }
        if (JudgeUtil.isEmpty(annotation.table())) {
            ExThrowsUtil.toCustom(cls.getName() + " 未指定@DbTable注解上实体映射的表名");
        }
        this.alias = annotation.alias();
        if (annotation.enabledDbPrefix()) {
            CustomConfigHelper configHelper = (CustomConfigHelper) GlobalDataHandler.readGlobalObject(Constants.DATA_CONFIG);
            DbDataSource dbDataSource = configHelper.getDbDataSource();
            String dbPrefix = CustomUtil.getDataBase(dbDataSource.getUrl());
            this.table = dbPrefix + "." + annotation.table();
        } else {
            this.table = annotation.table();
        }
        this.desc = annotation.desc();
        this.findUpDbJoinTables = annotation.mergeSuper();
        this.underlineToCamel = underlineToCamel;
        this.oneToOneFieldList = new ArrayList<>();
        this.oneToManyFieldList = new ArrayList<>();
        this.columnPropertyMaps = new ArrayList<>();
    }

    /**
     * 构造查询模板
     */
    private void buildSelectModels() {
        // 解析@DbJoinTables注解
        this.mergeDbJoinTables();

        for (Field field : fields) {
            if (this.isNotNeedParseProperty(field)) {
                continue;
            }

            if (field.isAnnotationPresent(DbKey.class) && Objects.isNull(keyParserModel)) {
                keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias, this.underlineToCamel);

            } else if (field.isAnnotationPresent(DbField.class)) {
                DbField dbField = field.getAnnotation(DbField.class);
                if (!dbField.exist()) {
                    continue;
                }
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias,
                        this.underlineToCamel, true);
                fieldParserModels.add(fieldParserModel);

            } else if (field.isAnnotationPresent(DbMapper.class)) {
                DbJoinTableParserModel<T> joinTableParserModel = new DbJoinTableParserModel<>(this.entityClass, field);
                joinDbMappers.add(joinTableParserModel);

            } else if (field.isAnnotationPresent(DbRelated.class)) {
                DbRelationParserModel<T> relatedParserModel = new DbRelationParserModel<>(this.entityClass, field,
                        this.table, this.alias, this.underlineToCamel);
                relatedParserModels.add(relatedParserModel);
            } else {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias,
                        this.underlineToCamel, false);
                fieldParserModels.add(fieldParserModel);
            }

        }
    }

    /**
     * 若满足条件，则该字段无需解析
     */
    private boolean isNotNeedParseProperty(Field field) {
        if (field.isAnnotationPresent(DbIgnore.class)) {
            return true;
        }
        // 基础字段或关联字段的java属性类型必须是允许的基本类型
        Class<?> fieldType = field.getType();
        if (!CustomUtil.isBasicClass(fieldType)) {
            this.handleMoreResultField(field, fieldType);
            return true;
        }
        return false;
    }

    /**
     * 处理一对一，一对多
     */
    private void handleMoreResultField(Field field, Class<?> fieldType) {
        if (field.isAnnotationPresent(DbOneToOne.class)) {
            if (Collection.class.isAssignableFrom(fieldType)) {
                ExThrowsUtil.toIllegal("Annotation DbOneToOne does not support acting on properties of " + fieldType.getName());
            }
            this.oneToOneFieldList.add(field);
        }
        if (field.isAnnotationPresent(DbOneToMany.class)) {
            if (Collection.class.isAssignableFrom(fieldType) || Object.class.equals(fieldType)) {
                this.oneToManyFieldList.add(field);
            } else if (Map.class.isAssignableFrom(fieldType)) {
                ExThrowsUtil.toIllegal("Annotation DbOneToMany does not support acting on properties of " + fieldType.getName());
            }
        }
    }


    /**
     * 向上查找@DbjoinTables注解
     */
    private void mergeDbJoinTables() {
        Class<?> entityClass = this.entityClass;
        if (this.findUpDbJoinTables) {
            while (!entityClass.equals(Object.class)) {
                buildDbJoinTables(entityClass);
                entityClass = entityClass.getSuperclass();
            }
            return;
        }
        this.buildDbJoinTables(entityClass);
    }

    /**
     * 解析@DbJoinTable(s)
     */
    private void buildDbJoinTables(Class<?> entityClass) {
        DbJoinTables joinTables = entityClass.getAnnotation(DbJoinTables.class);
        if (Objects.nonNull(joinTables)) {
            Arrays.stream(joinTables.value()).map(DbJoinTable::value).forEach(this.joinTableParserModels::add);
        }

        DbJoinTable joinTable = entityClass.getAnnotation(DbJoinTable.class);
        if(Objects.nonNull(joinTable)) {
            this.joinTableParserModels.add(joinTable.value());
        }
    }


    /**
     * 构造增改模板
     */
    private void buildUpdateModels(boolean isBuildUpdateModels) {
        for (Field field : fields) {

            if (this.isNotNeedParseProperty(field)) {
                continue;
            }

            if (field.isAnnotationPresent(DbKey.class)
                    && !field.isAnnotationPresent(DbField.class)
                    && Objects.isNull(keyParserModel)) {
                keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias, this.underlineToCamel);

            } else if (field.isAnnotationPresent(DbField.class) && isBuildUpdateModels) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias, this.underlineToCamel, true);
                fieldParserModels.add(fieldParserModel);

            } else if (field.isAnnotationPresent(DbField.class)) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias, this.underlineToCamel, true);
                fieldParserModels.add(fieldParserModel);

            } else {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias, this.underlineToCamel, false);
                fieldParserModels.add(fieldParserModel);
            }
        }
    }

    /**
     * 构造删除模板
     */
    private void buildDeleteModels() {
        Optional<Field> fieldOptional = Arrays.stream(fields).filter(x -> x.isAnnotationPresent(DbKey.class) && !x.isAnnotationPresent(DbField.class)).findFirst();
        fieldOptional.ifPresent(field -> keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias, this.underlineToCamel));
    }


    public List<DbRelationParserModel<T>> getRelatedParserModels() {
        return relatedParserModels;
    }

    public List<DbJoinTableParserModel<T>> getJoinDbMappers() {
        return joinDbMappers;
    }

    public List<String> getJoinTableParserModels() {
        return joinTableParserModels;
    }

    /**
     * 是否存在一对一，一对多的结果注入
     */
    protected boolean existNeedInjectResult() {
        return JudgeUtil.isNotEmpty(this.oneToOneFieldList)
                || JudgeUtil.isNotEmpty(this.oneToManyFieldList);
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

    public Field[] getFields() {
        return fields;
    }

    public String getDesc() {
        return desc;
    }

    public Map<String, String> getFieldMapper() {
        return fieldMapper;
    }

    public Map<String, String> getColumnMapper() {
        return columnMapper;
    }

    public List<Field> getOneToOneFieldList() {
        return oneToOneFieldList;
    }

    public List<Field> getOneToManyFieldList() {
        return oneToManyFieldList;
    }


    public List<ColumnPropertyMap<T>> columnPropertyMaps() {
        return this.columnPropertyMaps;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TableParseModel<T> clone() {
        TableParseModel<T> builder = null;
        try {
            builder = (TableParseModel<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            logger.error(e.toString(), e);
        }
        return builder;
    }



    /**
     * 创建字段属性关联映射
     * <li>为满足更多变的需求，特创建此映射对象，储存多个属性</li>
     * <li>此方法与{@link #buildMapper()} 的字段映射缓存不同，可以说是{@link #buildMapper()}的一个升级版</li>
     * <li>创建此对象并不会让{@link #buildMapper()}受到任何影响，两者均可正常使用</li>
     */
    private void createColumnPropertyMaps() throws IntrospectionException {
        boolean isKeyProperty = true;
        // 全部字段
        for (Field field : this.fields) {

            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            if (!CustomUtil.isBasicClass(fieldType)) {
                continue;
            }

            ColumnPropertyMap<T> cpMap = new ColumnPropertyMap<>();
            cpMap.setPropertyName(fieldName);
            cpMap.setPropertyType(fieldType);
            cpMap.setTargetClass(this.entityClass);

            PropertyDescriptor descriptor = new PropertyDescriptor(fieldName, this.entityClass);
            Method readMethod = descriptor.getReadMethod();
            cpMap.setGetMethodName(readMethod.getName());

            // 设置主键
            if (isKeyProperty && this.keyParserModel != null && this.keyParserModel.getKey().equals(fieldName)) {
                cpMap.setColumn(this.keyParserModel.getDbKey());
                cpMap.setAliasColumn(this.keyParserModel.getFieldSql());
                cpMap.setTableName(this.table);
                this.columnPropertyMaps.add(cpMap);
                isKeyProperty = false;
                continue;
            }

            // 设置剩余表字段
            DbFieldParserModel<T> fieldParserModel = this.fieldParserModels.stream()
                    .filter(op -> fieldName.equals(op.getFieldName()))
                    .findFirst()
                    .orElse(null);


            if (fieldParserModel != null) {
                cpMap.setColumn(fieldParserModel.getColumn());
                cpMap.setAliasColumn(fieldParserModel.getFieldSql());
                cpMap.setTableName(this.table);
            }
            this.columnPropertyMaps.add(cpMap);
        }

    }


}
