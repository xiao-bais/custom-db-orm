package com.custom.action.core;

import com.custom.action.interfaces.TableExecutor;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.jdbc.configuration.CustomConfigHelper;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.utils.DbConnGlobal;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体解析模板缓存
 * @author   Xiao-Bai
 * @since  2022/2/24 11:14
 **/
@SuppressWarnings("unchecked")
public class TableInfoCache {

    /**
     * 实体解析模板缓存
     * <br/>key-实体全路径名称
     * <br/>value-实体解析模板 {@link TableParseModel}
     */
    private final static Map<String, TableParseModel<?>> TABLE_MODEL = new ConcurrentHashMap<>();

    public static <T> TableParseModel<T> getTableModel(Class<T> cls) {
        TableParseModel<T> tableSqlBuilder = (TableParseModel<T>) TABLE_MODEL.get(cls.getName());
        if(Objects.isNull(tableSqlBuilder)) {
            tableSqlBuilder = new TableParseModel<>(cls);
            TABLE_MODEL.put(cls.getName(), tableSqlBuilder);
        }
        return tableSqlBuilder;
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
    private final static Map<Integer, Map<String, Boolean>> TABLE_LOGIC = new ConcurrentHashMap<>();

    public static void setTableLogic(int order, String key, Boolean val) {
        Map<String, Boolean> tableLogicMap = TABLE_LOGIC.get(order);
        if (tableLogicMap == null) {
            tableLogicMap = new ConcurrentHashMap<>();
            TABLE_LOGIC.put(order, tableLogicMap);
        }
        tableLogicMap.put(key, val);
    }

    public static Boolean isExistsLogic(int order, String table) {
        Map<String, Boolean> tableLogicMap = TABLE_LOGIC.get(order);
        if (tableLogicMap == null) {
            return null;
        }
        return tableLogicMap.get(table);
    }

    /**
     * sql构造模板缓存
     */
    private final static Map<String, SqlBuilderTemplate<?>> SQL_BUILDER_TEMPLATE = new ConcurrentHashMap<>();

    protected static <T> SqlBuilderTemplate<T> getSqlBuilderCache(Class<T> entityClass, JdbcExecutorFactory executorFactory) {
        SqlBuilderTemplate<T> optionalSqlBuilder = (SqlBuilderTemplate<T>)
                SQL_BUILDER_TEMPLATE.get(entityClass.getName());
        if (optionalSqlBuilder == null) {
            optionalSqlBuilder = new SqlBuilderTemplate<>(entityClass, executorFactory);
            SQL_BUILDER_TEMPLATE.put(entityClass.getName(), optionalSqlBuilder);
        }
        return optionalSqlBuilder;
    }

    public static <T> HandleSelectSqlBuilder<T> getSelectSqlBuilderCache(Class<T> entityClass, JdbcExecutorFactory executorFactory) {
        SqlBuilderTemplate<T> sqlBuilderCache = getSqlBuilderCache(entityClass, executorFactory);
        return (HandleSelectSqlBuilder<T>) sqlBuilderCache.getSelectSqlBuilder();
    }

    protected static <T> HandleInsertSqlBuilder<T> getInsertSqlBuilderCache(Class<T> entityClass, JdbcExecutorFactory executorFactory) {
        SqlBuilderTemplate<T> sqlBuilderCache = getSqlBuilderCache(entityClass, executorFactory);
        return (HandleInsertSqlBuilder<T>) sqlBuilderCache.getInsertSqlBuilder();
    }

    protected static <T> HandleUpdateSqlBuilder<T> getUpdateSqlBuilderCache(Class<T> entityClass, JdbcExecutorFactory executorFactory) {
        SqlBuilderTemplate<T> sqlBuilderCache = getSqlBuilderCache(entityClass, executorFactory);
        return (HandleUpdateSqlBuilder<T>) sqlBuilderCache.getUpdateSqlBuilder();
    }

    protected static <T> HandleDeleteSqlBuilder<T> getDeleteSqlBuilderCache(Class<T> entityClass, JdbcExecutorFactory executorFactory) {
        SqlBuilderTemplate<T> sqlBuilderCache = getSqlBuilderCache(entityClass, executorFactory);
        return (HandleDeleteSqlBuilder<T>) sqlBuilderCache.getDeleteSqlBuilder();
    }

    protected static <T> EmptySqlBuilder<T> getEmptySqlBuilder(Class<T> entityClass, JdbcExecutorFactory executorFactory) {
        SqlBuilderTemplate<T> sqlBuilderCache = getSqlBuilderCache(entityClass, executorFactory);
        return (EmptySqlBuilder<T>) sqlBuilderCache.getEmptySqlBuilder();
    }



    private final static Map<Class<?>, TableExecutor<?, ?>> TABLE_EXEC_CACHE = new ConcurrentHashMap<>();

    public static <T, P extends Serializable> TableExecutor<T, P> getTableExecutor(int order, Class<T> target) {
        CustomConfigHelper configHelper = DbConnGlobal.getConfigHelper(order);
        if (configHelper == null) {
            throw new CustomCheckException("No data source configured");
        }
        if (configHelper.getDbDataSource() == null) {
            throw new CustomCheckException("No matching data source found");
        }
        if (configHelper.getDbGlobalConfig() == null) {
            configHelper.setDbGlobalConfig(DbGlobalConfig.defaultConfig());
        }
        TableExecutor<T, P> tableExecutor = (TableExecutor<T, P>) TABLE_EXEC_CACHE.get(target);
        if (tableExecutor == null) {
            tableExecutor = new DefaultTableExecutor<>(configHelper.getDbDataSource(), configHelper.getDbGlobalConfig(), target);
            TABLE_EXEC_CACHE.put(target, tableExecutor);
        }
        return tableExecutor;
    }



}
