package com.custom.action.core;

import com.custom.action.condition.DefaultColumnParseHandler;
import com.custom.comm.annotations.*;
import com.custom.comm.enums.TableNameStrategy;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.*;
import com.custom.jdbc.configuration.CustomConfigHelper;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.jdbc.utils.DbConnGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实体类的解析对象
 * @author   Xiao-Bai
 * @since  2021/12/2 14:10
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
    private final List<Field> fields;
    /**
     * 全局配置
     */
    private DbGlobalConfig globalConfig;
    /**
     * 驼峰转下划线
     */
    private boolean underlineToCamel;
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
     * 全字段解析对象
     */
    private List<ColumnPropertyMap<T>> columnPropertyMaps;
    /**
     * 属性信息描述集合
     */
    private List<PropertyDescriptor> propertyList;


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

        createTableSql.append(String.format("CREATE TABLE `%s` (\n%s)", this.table, fieldSql));

        if (JudgeUtil.isNotEmpty(this.desc)) {
            createTableSql.append(String.format(" COMMENT = '%s'", this.desc));
        }
        return createTableSql.toString();
    }

    /**
     * 删除表结构
     */
    public String dropTableSql() {
        return String.format("DROP TABLE IF EXISTS `%s`", this.table);
    }


    /**
     * 构建字段映射
     */
    private void buildMapper() {
        if (Objects.nonNull(keyParserModel)) {
            columnMapper.put(keyParserModel.getFieldSql(), keyParserModel.getKey());
            fieldMapper.put(keyParserModel.getKey(), keyParserModel.getFieldSql());
        }

        List<DbFieldParserModel<T>> dbFieldParseModels = getDbFieldParseModels();
        if (!dbFieldParseModels.isEmpty()) {
            // 基础字段
            dbFieldParseModels.forEach(x -> {
                columnMapper.put(x.getFieldSql(), x.getFieldName());
                fieldMapper.put(x.getFieldName(), x.getFieldSql());
            });
        }
        // 关联字段1
        if (!joinDbMappers.isEmpty()) {
            joinDbMappers.forEach(x -> {
                columnMapper.put(x.getJoinName(), x.getFieldName());
                fieldMapper.put(x.getFieldName(), x.getJoinName());
            });
        }

        // 关联字段2
        if (!relatedParserModels.isEmpty()) {
            relatedParserModels.forEach(x -> {
                columnMapper.put(x.getFieldSql(), x.getFieldName());
                fieldMapper.put(x.getFieldName(), x.getFieldSql());
            });
        }
    }

    /**
     * 默认构造方法为查询
     */
    TableParseModel(Class<T> cls) {
        // 初始化本对象属性
        initLocalProperty(cls);

        // 加载所有字段
        this.fields =  ReflectUtil.loadFields(this.entityClass);

        // 构建字段解析模板
        this.buildFieldModels();

        // 创建字段属性映射
        this.createColumnPropertyMaps();

        // 构建字段映射缓存
        this.buildMapper();
        // 创建字段描述信息
        this.createProperty();
        // 创建Lambda解析缓存
        DefaultColumnParseHandler.createColumnCache(this);

    }

    /**
     * 初始化本对象属性
     */
    private void initLocalProperty(Class<T> cls) {
        this.entityClass = cls;
        DbTable dbTable = cls.getAnnotation(DbTable.class);
        if (Objects.isNull(dbTable)) {
            throw new CustomCheckException(cls.getName() + " 未标注@DbTable注解");
        }

        this.table = dbTable.value();
        this.alias = dbTable.alias();
        this.desc = dbTable.desc();
        int order = dbTable.order();
        CustomConfigHelper configHelper = DbConnGlobal.getConfigHelper(order);
        AssertUtil.notNull(configHelper, JdbcAction.class.getName() +"实例化之前，不允许构造实体解析模板");

        this.globalConfig = configHelper.getDbGlobalConfig();
        this.underlineToCamel = this.globalConfig.getStrategy().isUnderlineToCamel();

        // 未填写表名的情况下，由策略生成表名
        if (StrUtils.isBlank(this.table)) {
            this.table = this.generateTableName(entityClass.getSimpleName());
        }
        this.columnPropertyMaps = new ArrayList<>();
        this.propertyList = new ArrayList<>();
    }

    /**
     * 根据类名以及前缀生成表名
     */
    private String generateTableName(String name) {
        TableNameStrategy nameStrategy = globalConfig.getTableNameStrategy();
        if (nameStrategy == TableNameStrategy.APPEND) {
            return globalConfig.getTableNamePrefix() + name;
        }
        if (nameStrategy == TableNameStrategy.LOWERCASE) {
            return globalConfig.getTableNamePrefix() + name.toLowerCase(Locale.ROOT);
        }
        String prefix = globalConfig.getTableNamePrefix();
        if (StrUtils.isNotBlank(prefix)) {
            prefix = globalConfig.getTableNamePrefix() + Constants.UNDERLINE;
        }
        return prefix + StrUtils.camelToUnderline(name);
    }

    /**
     * 构造查询模板
     */
    private void buildFieldModels() {
        // 解析@DbJoinTables注解
        List<String> joinTables = this.buildDbJoinTables();
        this.joinTableParserModels.addAll(joinTables);

        for (Field field : fields) {
            if (!CustomUtil.isBasicClass(field.getType())) {
                continue;
            }

            // 只支持一个主键
            if (field.isAnnotationPresent(DbKey.class) && Objects.isNull(keyParserModel)) {
                keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias, this.underlineToCamel);

            } else if (field.isAnnotationPresent(DbField.class)) {

                DbField dbField = field.getAnnotation(DbField.class);
                DbFieldParserModel<T> fieldParserModel;

                if (dbField.exist()) {
                    fieldParserModel  = new DbFieldParserModel<>(field, this.table, this.alias,
                            this.underlineToCamel, true);
                }else {
                    fieldParserModel = new DbFieldParserModel<>(field);
                }
                fieldParserModels.add(fieldParserModel);

            } else if (field.isAnnotationPresent(DbJoinField.class)) {

                // 若当前是本类时，则注解生效，否则不视为表字段
                if (field.getDeclaringClass().equals(this.entityClass)) {
                    DbJoinTableParserModel<T> joinTableParserModel = new DbJoinTableParserModel<>(this.entityClass, field);
                    joinDbMappers.add(joinTableParserModel);
                }else {
                    DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field);
                    fieldParserModels.add(fieldParserModel);
                }


            } else if (field.isAnnotationPresent(DbRelated.class)) {

                // 若当前是本类时，则注解生效，否则不视为表字段
                if (field.getDeclaringClass().equals(this.entityClass)) {
                    DbRelationParserModel<T> relatedParserModel = new DbRelationParserModel<>(this.entityClass, field,
                            this.table, this.alias, this.underlineToCamel);
                    relatedParserModels.add(relatedParserModel);
                }else {
                    DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field);
                    fieldParserModels.add(fieldParserModel);
                }

            } else if (field.isAnnotationPresent(DbNotField.class)) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field);
                fieldParserModels.add(fieldParserModel);

            } else {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias,
                        this.underlineToCamel, false);
                fieldParserModels.add(fieldParserModel);
            }

        }
    }

    /**
     * 解析@DbJoinTable(s)
     */
    private List<String> buildDbJoinTables() {

        List<String> joinList = new ArrayList<>();
        DbJoinTables joinTables = entityClass.getAnnotation(DbJoinTables.class);

        if (Objects.nonNull(joinTables)) {
            Arrays.stream(joinTables.value()).map(DbJoinTable::value).forEach(joinList::add);
        }

        DbJoinTable joinTable = entityClass.getAnnotation(DbJoinTable.class);
        if (Objects.nonNull(joinTable)) {
            String joinSql = joinTable.value();
            if (!joinList.contains(joinSql)) {
                joinList.add(joinTable.value());
            }
        }

        return joinList;
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

    public List<PropertyDescriptor> getPropertyList() {
        return propertyList;
    }

    public Class<T> getEntityClass() {
        return entityClass;
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

    public List<DbFieldParserModel<T>> getDbFieldParseModels() {
        return fieldParserModels.stream().filter(DbFieldParserModel::isDbField).collect(Collectors.toList());
    }

    public List<Field> getFields() {
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

    public List<ColumnPropertyMap<T>> columnPropertyMaps() {
        return this.columnPropertyMaps;
    }


    /**
     * 创建字段属性关联映射
     * <li>为满足更多变的需求，特创建此映射对象，储存多个属性</li>
     * <li>此方法与{@link #buildMapper()} 的字段映射缓存不同，可以说是{@link #buildMapper()}的一个升级版</li>
     * <li>创建此对象并不会让{@link #buildMapper()}受到任何影响，两者均可正常使用</li>
     */
    private void createColumnPropertyMaps() {
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

            PropertyDescriptor descriptor = null;
            try {
                descriptor = new PropertyDescriptor(fieldName, this.entityClass);
            } catch (IntrospectionException e) {
                logger.error(e.toString(), e);
                return;
            }
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


    /**
     * 初始化所有字段描述信息
     */
    private void createProperty() {
        try {
            this.propertyList = ReflectUtil.getProperties(this.entityClass);
        } catch (IntrospectionException e) {
            logger.error(e.toString(), e);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public TableParseModel<T> clone() {
        try {
            return (TableParseModel<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
