package com.custom.action.sqlparser;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/24 11:14
 * @Desc：实体解析模板缓存
 **/
@SuppressWarnings("unchecked")
public class TableInfoCache {

    /**
     * 实体解析模板缓存
     * <br/>key-实体全路径名称
     * <br/>value-实体解析模板 {@link TableParseModel}
     */
    private final static Map<String, Object> TABLE_MODEL = new CustomLocalCache();
    private static Boolean underlineToCamel = false;

    public static <T> TableParseModel<T> getTableModel(Class<T> cls) {
        TableParseModel<T> tableSqlBuilder = (TableParseModel<T>) TABLE_MODEL.get(cls.getName());
        if(Objects.isNull(tableSqlBuilder)) {
            tableSqlBuilder = new TableParseModel<>(cls, underlineToCamel);
            TABLE_MODEL.put(cls.getName(), tableSqlBuilder);
        }
        return tableSqlBuilder;
    }
    public static void setUnderlineToCamel(boolean underlineToCamel) {
        TableInfoCache.underlineToCamel = underlineToCamel;
    }


    /**
     * 获取表字段到java字段的映射
     */
    public static <T> Map<String, String> getColumnMap(Class<T> cls) {
        return getTableModel(cls).getColumnMapper();
    }

    /**
     * 获取java字段到表字段的映射
     */
    public static <T> Map<String, String> getFieldMap(Class<T> cls) {
        return getTableModel(cls).getFieldMapper();
    }

    /**
     * 获取表名称
     */
    public static <T> String tableName(Class<T> cls) {
        return getTableModel(cls).getTable();
    }


    /**
     * 表的逻辑删除字段缓存
     * <br/>key-实体全路径名称
     * <br/>value(是否存在逻辑删除字段)-true or false
     */
    private final static Map<String, Object> TABLE_LOGIC = new CustomLocalCache();

    public static void setTableLogic(String key, Object val) {
        TABLE_LOGIC.put(key, val);
    }

    public static Boolean isExistsLogic(String table) {
        return (Boolean) TABLE_LOGIC.get(table);
    }

    /**
     * 实体查询时，是否存在相互引用的情况
     * <br/>key - 实体全路径名称
     * <br/>value - 是否存在相互引用字段: true or false
     */
    private final static Set<Class<?>> ENTITY_EXIST_CROSS_REFERENCE = new CopyOnWriteArraySet<>();

    public static boolean existCrossReference(Class<?> thisClass, Class<?> joinClass) {
        if (ENTITY_EXIST_CROSS_REFERENCE.contains(thisClass)) {
            return true;
        }
        ENTITY_EXIST_CROSS_REFERENCE.add(thisClass);
        if (ENTITY_EXIST_CROSS_REFERENCE.contains(joinClass)) {
            return true;
        }
        ENTITY_EXIST_CROSS_REFERENCE.add(joinClass);
        return false;
    }

    /**
     * sql构造模板缓存
     */
    private final static Map<String, Object> SQL_BUILDER_TEMPLATE = new CustomLocalCache();

    protected static <T> SqlBuilderTemplate<T> getSqlBuilderCache(Class<T> entityClass) {
        SqlBuilderTemplate<T> optionalSqlBuilder = (SqlBuilderTemplate<T>)
                SQL_BUILDER_TEMPLATE.get(entityClass.getName());
        if (optionalSqlBuilder == null) {
            optionalSqlBuilder = new SqlBuilderTemplate<>(entityClass);
            SQL_BUILDER_TEMPLATE.put(entityClass.getName(), optionalSqlBuilder);
        }
        return optionalSqlBuilder;
    }

    protected static <T> HandleSelectSqlBuilder<T> getSelectSqlBuilderCache(Class<T> entityClass) {
        SqlBuilderTemplate<T> sqlBuilderCache = getSqlBuilderCache(entityClass);
        return (HandleSelectSqlBuilder<T>) sqlBuilderCache.getSelectSqlBuilder();
    }

    protected static <T> HandleInsertSqlBuilder<T> getInsertSqlBuilderCache(Class<T> entityClass) {
        SqlBuilderTemplate<T> sqlBuilderCache = getSqlBuilderCache(entityClass);
        return (HandleInsertSqlBuilder<T>) sqlBuilderCache.getInsertSqlBuilder();
    }

    protected static <T> HandleUpdateSqlBuilder<T> getUpdateSqlBuilderCache(Class<T> entityClass) {
        SqlBuilderTemplate<T> sqlBuilderCache = getSqlBuilderCache(entityClass);
        return (HandleUpdateSqlBuilder<T>) sqlBuilderCache.getUpdateSqlBuilder();
    }

    protected static <T> HandleDeleteSqlBuilder<T> getDeleteSqlBuilderCache(Class<T> entityClass) {
        SqlBuilderTemplate<T> sqlBuilderCache = getSqlBuilderCache(entityClass);
        return (HandleDeleteSqlBuilder<T>) sqlBuilderCache.getDeleteSqlBuilder();
    }

    protected static <T> EmptySqlBuilder<T> getEmptySqlBuilder(Class<T> entityClass) {
        SqlBuilderTemplate<T> sqlBuilderCache = getSqlBuilderCache(entityClass);
        return (EmptySqlBuilder<T>) sqlBuilderCache.getEmptySqlBuilder();
    }



}
