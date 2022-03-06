package com.custom.dbaction;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.ExecuteMethod;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.logic.LogicDeleteFieldSqlHandler;
import com.custom.comm.page.DbPageRows;
import com.custom.sqlparser.TableParserModelCache;
import com.custom.sqlparser.TableSqlBuilder;
import com.custom.wrapper.ConditionEntity;
import com.custom.wrapper.LambdaConditionEntity;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/8 14:49
 * @Desc：方法执行处理抽象入口
 **/
public abstract class AbstractSqlBuilder {

    /*--------------------------------------- select ---------------------------------------*/
    public abstract <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, int pageIndex, int pageSize, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, DbPageRows<T> dbPageRows, Object... params) throws Exception;
    public abstract <T> T selectOneByKey(Class<T> t, Object key) throws Exception;
    public abstract <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, DbPageRows<T> dbPageRows, ConditionEntity<T> conditionEntity) throws Exception;
    public abstract <T> List<T> selectList(Class<T> t, ConditionEntity<T> conditionEntity) throws Exception;
    public abstract <T> T selectOneByCondition(ConditionEntity<T> conditionEntity) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, DbPageRows<T> dbPageRows, LambdaConditionEntity<T> conditionEntity) throws Exception;
    public abstract <T> List<T> selectList(Class<T> t, LambdaConditionEntity<T> conditionEntity) throws Exception;
    public abstract <T> T selectOneByCondition(LambdaConditionEntity<T> conditionEntity) throws Exception;


    /*--------------------------------------- delete ---------------------------------------*/
    public abstract <T> int deleteByKey(Class<T> t, Object key) throws Exception;
    public abstract <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception;
    public abstract <T> int deleteByCondition(Class<T> t, ConditionEntity<T> conditionEntity) throws Exception;

    /*--------------------------------------- insert ---------------------------------------*/
    public abstract <T> int insert(T t, boolean isGeneratedKey) throws Exception;
    public abstract <T> int insert(List<T> tList, boolean isGeneratedKey) throws Exception;

    /*--------------------------------------- update ---------------------------------------*/
    public abstract <T> int updateByKey(T t, String... updateDbFields) throws Exception;
    public abstract <T> int updateByCondition(T t, ConditionEntity<T> conditionEntity) throws Exception;

    /*--------------------------------------- comm ---------------------------------------*/
    public abstract <T> long save(T t) throws Exception;
    public abstract void createTables(Class<?>... arr) throws Exception;
    public abstract void dropTables(Class<?>... arr) throws Exception;


    private SqlExecuteAction sqlExecuteAction;
    private DbCustomStrategy dbCustomStrategy;
    private TableParserModelCache tableParserModelCache;
    private boolean enabledTableModel = false;
    private String logicField = SymbolConst.EMPTY;
    private String logicDeleteUpdateSql = SymbolConst.EMPTY;
    private String logicDeleteQuerySql = SymbolConst.EMPTY;
    private final Map<String, Boolean> tableLogicCache = new ConcurrentHashMap<>();

    /**
     * 初始化逻辑删除的sql
     */
    public void initLogic() {
        if(JudgeUtilsAx.isLogicDeleteOpen(dbCustomStrategy)) {
            if(JudgeUtilsAx.isEmpty(dbCustomStrategy.getNotDeleteLogicValue())
                    || JudgeUtilsAx.isEmpty(dbCustomStrategy.getDeleteLogicValue())) {
                throw new CustomCheckException(ExceptionConst.EX_LOGIC_EMPTY_VALUE);
            }
            this.logicField = dbCustomStrategy.getDbFieldDeleteLogic();
            this.logicDeleteUpdateSql = String.format("%s = %s",
                    logicField, dbCustomStrategy.getDeleteLogicValue());
            this.logicDeleteQuerySql = String.format("%s = %s",
                    logicField, dbCustomStrategy.getNotDeleteLogicValue());
        }
    }

    /**
     * 获取根据主键删除的sql
     */
    public String getLogicDeleteKeySql(String key, String dbKey, String table, String alias, boolean isMore) throws Exception {
        String sql;
        String keySql  = String.format("%s.%s %s %s", alias,
                dbKey, isMore ? SymbolConst.IN : SymbolConst.EQUALS, key);

        if (JudgeUtilsAx.isNotEmpty(logicDeleteUpdateSql)) {
            String logicDeleteQuerySql = String.format("%s.%s", alias, this.logicDeleteQuerySql);
            String logicDeleteUpdateSql = String.format("%s.%s", alias, this.logicDeleteUpdateSql);
            if(checkLogicFieldIsExist(table)) {
                sql = String.format("update %s %s set %s where %s and %s", table,
                        alias, logicDeleteUpdateSql, logicDeleteQuerySql, keySql);
            }else {
                sql = String.format("delete from %s %s where %s", table, alias, keySql);
            }
        }else {
            sql = String.format("delete from %s %s where %s", table, alias, keySql);
        }
        return sql;
    }


    /**
     * 由于部分表可能没有逻辑删除字段，所以在每一次执行时，都需检查该表有没有逻辑删除的字段，以保证sql正常执行
     */
    public boolean checkLogicFieldIsExist(String tableName) throws Exception {
        if (tableLogicCache.get(tableName) != null) {
             return tableLogicCache.get(tableName);
        }
        String existSql = String.format("select count(*) count from information_schema.columns where table_name = '%s' and column_name = '%s'", tableName, logicField);
        long count = sqlExecuteAction.executeExist(existSql);
        if (count > 0) {
            tableLogicCache.put(tableName, true);
        }else {
            tableLogicCache.put(tableName, false);
        }
        return count > 0;
    }
    /**
     * 添加逻辑删除的部分sql
     */
    public String checkConditionAndLogicDeleteSql(String alias, final String condition, String logicSql, String tableName) throws Exception {
        if(!checkLogicFieldIsExist(tableName)) {
            logicSql = SymbolConst.EMPTY;
        }
        final String finalLogicSql = logicSql;
        LogicDeleteFieldSqlHandler handler = () -> {
            String sql;
            if (JudgeUtilsAx.isNotEmpty(condition)) {
                if (JudgeUtilsAx.isNotEmpty(finalLogicSql)) {
                    sql = String.format("where %s.%s %s ", alias, finalLogicSql, condition.trim());
                }else {
                    sql = String.format("where %s ", CustomUtil.trimSqlCondition(condition));
                }
            } else {
                if (JudgeUtilsAx.isNotEmpty(finalLogicSql)) {
                    sql = String.format("where %s.%s ", alias, finalLogicSql);
                }else {
                    sql = condition.trim();
                }
            }
            return sql;
        };
        return handler.handleLogic();
    }

    /**
    * 纯sql查询集合
    */
    public <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        return sqlExecuteAction.query(t, sql, params);
    }

    /**
    * 纯sql查询单条记录
    */
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        List<T> queryList = sqlExecuteAction.query(t, sql, params);
        int size = queryList.size();
        if (size == 0) {
            return null;
        } else if (size > 1) {
            throw new CustomCheckException(String.format(ExceptionConst.EX_QUERY_MORE_RESULT, size));
        }
        return queryList.get(SymbolConst.DEFAULT_ZERO);
    }

    /**
    * 纯sql查询当个字段
    */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            throw new CustomCheckException(ExceptionConst.EX_SQL_NOT_EMPTY);
        }
        return sqlExecuteAction.selectOneSql(sql, params);
    }

    /**
    * 纯sql增删改
    */
    public int executeSql(String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            throw new NullPointerException();
        }
        return sqlExecuteAction.executeUpdate(sql, params);
    }

    /**
    * 创建/删除表
    */
    public void execTable(String sql) throws SQLException {
        sqlExecuteAction.executeTableSql(sql);
    }

    /**
     * 查询该表是否存在
     */
    public boolean existTable(String sql) throws Exception {
        return sqlExecuteAction.executeExist(sql) == 0L;
    }

    /**
    * 添加
    */
    public <T> int executeInsert(String sql, List<T> obj, boolean isGeneratedKey, String key, Class<?> keyType,  Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            throw new NullPointerException();
        }
        return isGeneratedKey ? sqlExecuteAction.executeInsert(obj, sql, key, keyType, params) : sqlExecuteAction.executeUpdate(sql, params);
    }


    /**
     * 从缓存中获取实体解析模板，若缓存中没有，就重新构造模板
     */
    protected <T> TableSqlBuilder<T> getEntityModelCache(Class<T> t, ExecuteMethod method) {
        return isEnabledTableModel() ? tableParserModelCache.getTableModel(t.getName()) : new TableSqlBuilder<>(t, method);
    }

    protected <T> TableSqlBuilder<T> getEntityModelCache(Class<T> t) {
        return getEntityModelCache(t, ExecuteMethod.SELECT);
    }

    protected <T> TableSqlBuilder<T> getUpdateEntityModelCache(T t, boolean isBuildUpdateModels) {
        TableSqlBuilder<T> tableModel;
        if(!isEnabledTableModel()) {
            return new TableSqlBuilder<>(t, isBuildUpdateModels);
        }else {
            tableModel = tableParserModelCache.getTableModel(t.getClass().getName());
            if(tableModel == null) {
                return new TableSqlBuilder<>(t, isBuildUpdateModels);
            }
        }
        tableModel.setEntity(t);
        tableModel.setList(Collections.singletonList(t));
        return tableModel;
    }

    protected <T> TableSqlBuilder<T> getInsertEntityModelCache(List<T> tList) {
        T t = tList.get(0);
        TableSqlBuilder<T> tableModel;
        if(!isEnabledTableModel()) {
            return new TableSqlBuilder<>(tList);
        }else {
            tableModel = tableParserModelCache.getTableModel(t.getClass().getName());
            if(tableModel == null) {
                return new TableSqlBuilder<>(tList);
            }
        }
        tableModel.setEntity(t);
        tableModel.setList(tList);
        return tableModel;
    }



    public SqlExecuteAction getSqlExecuteAction() {
        return sqlExecuteAction;
    }

    public void setSqlExecuteAction(SqlExecuteAction sqlExecuteAction) {
        this.sqlExecuteAction = sqlExecuteAction;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    public void setDbCustomStrategy(DbCustomStrategy dbCustomStrategy) {
        this.dbCustomStrategy = dbCustomStrategy;
    }

    public String getLogicDeleteUpdateSql() {
        return logicDeleteUpdateSql;
    }

    public String getLogicDeleteQuerySql() {
        return logicDeleteQuerySql;
    }

    public Map<String, Boolean> getTableLogicCache() {
        return tableLogicCache;
    }

    public String getLogicField() {
        return logicField;
    }

    public TableParserModelCache getTableParserModelCache() {
        return tableParserModelCache;
    }

    public void setTableParserModelCache(TableParserModelCache tableParserModelCache) {
        this.tableParserModelCache = tableParserModelCache;
    }

    public boolean isEnabledTableModel() {
        return enabledTableModel;
    }

    public void setEnabledTableModel(boolean enabledTableModel) {
        this.enabledTableModel = enabledTableModel;
    }
}
