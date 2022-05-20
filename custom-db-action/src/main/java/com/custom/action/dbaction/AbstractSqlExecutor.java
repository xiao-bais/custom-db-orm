package com.custom.action.dbaction;

import com.custom.action.interfaces.LogicDeleteFieldSqlHandler;
import com.custom.action.sqlparser.HandleSelectSqlBuilder;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.action.wrapper.ConditionWrapper;
import com.custom.action.wrapper.SFunction;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.page.DbPageRows;
import com.custom.configuration.DbCustomStrategy;
import com.custom.jdbc.ExecuteSqlHandler;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/8 14:49
 * @Desc：方法执行处理抽象入口
 **/
@SuppressWarnings("unchecked")
public abstract class AbstractSqlExecutor {

    /*--------------------------------------- select ---------------------------------------*/
    public abstract <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, int pageIndex, int pageSize, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, DbPageRows<T> dbPageRows, Object... params) throws Exception;
    public abstract <T> T selectOneByKey(Class<T> t, Object key) throws Exception;
    public abstract <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception;

    /**
     * ConditionWrapper(条件构造器)
     */
    public abstract <T> DbPageRows<T> selectPageRows(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> List<T> selectList(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> T selectOneByCondition(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> long selectCount(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> Object selectObj(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> Map<String, Object> selectMap(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper) throws Exception;


    /*--------------------------------------- delete ---------------------------------------*/
    public abstract <T> int deleteByKey(Class<T> t, Object key) throws Exception;
    public abstract <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception;
    public abstract <T> int deleteByCondition(ConditionWrapper<T> wrapper) throws Exception;

    /*--------------------------------------- insert ---------------------------------------*/
    public abstract <T> int insert(T t) throws Exception;
    public abstract <T> int insert(List<T> tList) throws Exception;

    /*--------------------------------------- update ---------------------------------------*/
    public abstract <T> int updateByKey(T t) throws Exception;
    public abstract <T> int updateByKey(T t, SFunction<T, ?>... updateColumns) throws Exception;
    public abstract <T> int updateByCondition(T t, ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> int updateByCondition(T t, String condition, Object... params) throws Exception;

    /*--------------------------------------- comm ---------------------------------------*/
    public abstract <T> long save(T t) throws Exception;
    public abstract void createTables(Class<?>... arr) throws Exception;
    public abstract void dropTables(Class<?>... arr) throws Exception;


    private ExecuteSqlHandler executeSqlHandler;
    private DbCustomStrategy dbCustomStrategy;
    private String logicField = SymbolConstant.EMPTY;
    private String logicDeleteUpdateSql = SymbolConstant.EMPTY;
    private String logicDeleteQuerySql = SymbolConstant.EMPTY;

    /**
     * 初始化逻辑删除的sql
     */
    public void initLogic() {
        if(isLogicDeleteOpen(dbCustomStrategy)) {
            if(JudgeUtilsAx.isEmpty(dbCustomStrategy.getNotDeleteLogicValue()) || JudgeUtilsAx.isEmpty(dbCustomStrategy.getDeleteLogicValue())) {
                ExThrowsUtil.toCustom("The corresponding value of the logical deletion field is not configured");
            }
            this.logicField = dbCustomStrategy.getDbFieldDeleteLogic();
            this.logicDeleteUpdateSql = String.format("%s = %s",
                    logicField, dbCustomStrategy.getDeleteLogicValue());
            this.logicDeleteQuerySql = String.format("%s = %s",
                    logicField, dbCustomStrategy.getNotDeleteLogicValue());
        }
    }

    /**
     * 是否开启了逻辑删除字段
     */
    public static boolean isLogicDeleteOpen(DbCustomStrategy dbCustomStrategy) {
        if(dbCustomStrategy == null) {
            dbCustomStrategy = new DbCustomStrategy();
        }
        return JudgeUtilsAx.isNotEmpty(dbCustomStrategy.getDbFieldDeleteLogic());
    }



    /**
     * 由于部分表可能没有逻辑删除字段，所以在每一次执行时，都需检查该表有没有逻辑删除的字段，以保证sql正常执行
     */
    public boolean checkLogicFieldIsExist(String tableName) throws Exception {
        Boolean existsLogic = TableInfoCache.isExistsLogic(tableName);
        if (existsLogic != null) {
             return existsLogic;
        }
        String existSql = String.format("select count(*) count from information_schema.columns where table_name = '%s' and column_name = '%s'", tableName, logicField);
        long count = executeSqlHandler.executeExist(existSql);
        TableInfoCache.setTableLogic(tableName, count > 0);
        return count > 0;
    }
    /**
     * 添加逻辑删除的部分sql
     */
    public String checkConditionAndLogicDeleteSql(String alias, final String condition, String logicSql, String tableName) throws Exception {
        if(!checkLogicFieldIsExist(tableName)) {
            logicSql = SymbolConstant.EMPTY;
        }
        final String finalLogicSql = logicSql;
        LogicDeleteFieldSqlHandler handler = () -> {
            if (JudgeUtilsAx.isNotEmpty(condition)) {
                return JudgeUtilsAx.isNotEmpty(finalLogicSql) ? String.format("\nwhere %s.%s %s ", alias, finalLogicSql, condition.trim())
                        : String.format("\nwhere %s ", CustomUtil.trimAppendSqlCondition(condition));
            } else {
                return JudgeUtilsAx.isNotEmpty(finalLogicSql) ? String.format("\nwhere %s.%s ", alias, finalLogicSql)
                        : condition == null ? SymbolConstant.EMPTY : condition.trim();
            }
        };
        return handler.handleLogic();
    }


    /**
    * 纯sql查询集合
    */
    public <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        return executeSqlHandler.query(t, true, sql, params);
    }

    /**
    * 纯sql查询单条记录
    */
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        List<T> queryList = executeSqlHandler.query(t, true, sql, params);
        int size = queryList.size();
        if (size == 0) {
            return null;
        } else if (size > 1) {
            throw new CustomCheckException(String.format("One was queried, but more were found:(%s) ", size));
        }
        return queryList.get(SymbolConstant.DEFAULT_ZERO);
    }

    /**
     * 纯sql查询单条记录(映射到Map)
     */
    public Map<String, Object> selectMapBySql(String sql, Object... params) throws Exception {
        List<Map<String, Object>> mapList = executeSqlHandler.selectMapsSql(sql, true, params);
        return mapList.get(0);
    }

    /**
     * 纯sql查询多条记录(映射到Map)
     */
    public List<Map<String, Object>> selectMapsBySql(String sql, Object... params) throws Exception {
        return executeSqlHandler.selectMapsSql(sql, true, params);
    }

    /**
    * 纯sql查询单个字段
    */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            ExThrowsUtil.toCustom("The Sql to be Not Empty");
        }
        return executeSqlHandler.selectObjSql(sql, params);
    }

    /**
     * 纯sql查询单个字段集合
     */
    public List<Object> selectObjsBySql(String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return executeSqlHandler.selectObjsSql(sql, params);
    }

    /**
    * 纯sql增删改
    */
    public int executeSql(String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return executeSqlHandler.executeUpdate(sql, params);
    }

    /**
     * 直接执行查询，属于内部执行
     */
    public <T> List<T> executeQueryNotPrintSql(Class<T> t, String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return executeSqlHandler.query(t, false, sql, params);
    }

    /**
    * 创建/删除表
    */
    public void execTable(String sql) throws SQLException {
        executeSqlHandler.executeTableSql(sql);
    }

    /**
     * 查询该表是否存在
     */
    public boolean hasTableInfo(String sql) throws Exception {
        return executeSqlHandler.executeExist(sql) > 0L;
    }

    /**
    * 添加
    */
    public <T> int executeInsert(String sql, List<T> obj, boolean isGeneratedKey, String key, Class<?> keyType,  Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return isGeneratedKey ? executeSqlHandler.executeInsert(obj, sql, key, keyType, params) : executeSqlHandler.executeUpdate(sql, params);
    }


    /**
     * 从缓存中获取实体解析模板，若缓存中没有，就重新构造模板（查询、删除、创建删除表）
     */
    protected <T> TableSqlBuilder<T> getEntityModelCache(Class<T> t) {
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(t);
        tableModel.setSqlExecuteAction(executeSqlHandler);
        return tableModel;
    }

    /**
     * 从缓存中获取实体解析模板，若缓存中没有，就重新构造模板（批量增加记录）
     */
    protected <T> TableSqlBuilder<T> getUpdateEntityModelCache(List<T> tList) {
        TableSqlBuilder<T> tableModelCache = (TableSqlBuilder<T>) TableInfoCache.getTableModel(tList.get(0).getClass());
        TableSqlBuilder<T> tableModel = tableModelCache.clone();
        tableModel.setEntity(tList.get(0));
        tableModel.setList(tList);
        tableModel.setSqlExecuteAction(executeSqlHandler);
        return tableModel;
    }

    /**
     * 获取实体解析模板中的操作对象
     */
    protected <T, R extends AbstractSqlBuilder<T>> R buildSqlOperationTemplate(Class<T> entityClass) {
        return buildSqlOperationTemplate(entityClass, ExecuteMethod.SELECT);
    }

    /**
     * 获取实体解析模板中的操作对象
     */
    protected <T, R extends AbstractSqlBuilder<T>> R buildSqlOperationTemplate(T entity, ExecuteMethod method) {
        if(Objects.isNull(entity)) {
            ExThrowsUtil.toNull("实体对象不能为空");
        }
        return buildSqlOperationTemplate(Collections.singletonList(entity), method);
    }

    /**
     * 获取实体解析模板中的操作对象
     */
    protected <T, R extends AbstractSqlBuilder<T>> R buildSqlOperationTemplate(List<T> entityList, ExecuteMethod method) {
        TableSqlBuilder<T> tableModelCache = getUpdateEntityModelCache(entityList);
        TableSqlBuilder<T> tableModel = tableModelCache.clone();
        tableModel.setEntity(entityList.get(0));
        tableModel.setList(entityList);
        tableModel.buildSqlConstructorModel(method);
        tableModel.setLogicFieldInfo(logicField, dbCustomStrategy.getDeleteLogicValue(), dbCustomStrategy.getNotDeleteLogicValue());
        return (R) tableModel.getSqlBuilder();
    }

    /**
     * 获取实体解析模板中的操作对象
     */
    protected <T, R extends AbstractSqlBuilder<T>> R buildSqlOperationTemplate(Class<T> entityClass, ExecuteMethod method) {
        TableSqlBuilder<T> tableSqlBuilder = getEntityModelCache(entityClass);
        tableSqlBuilder.buildSqlConstructorModel(method);
        tableSqlBuilder.setLogicFieldInfo(logicField, dbCustomStrategy.getDeleteLogicValue(), dbCustomStrategy.getNotDeleteLogicValue());
        return (R) tableSqlBuilder.getSqlBuilder();
    }



    /**
     * 公共获取完整查询sql
     */
    protected <T> String getFullSelectSql(ConditionWrapper<T> wrapper) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(wrapper.getEntityClass());
        sqlBuilder.setPrimaryTable(wrapper.getPrimaryTable());
        StringBuilder selectSql = new StringBuilder();
        if(wrapper.getSelectColumns() != null) {
            selectSql.append(sqlBuilder.selectColumns(wrapper.getSelectColumns()));
        }else {
            selectSql.append(sqlBuilder.buildSql());
        }
        String condition = checkConditionAndLogicDeleteSql(sqlBuilder.getAlias(), wrapper.getFinalConditional(), getLogicDeleteQuerySql(), sqlBuilder.getTable());
        selectSql.append(condition);
        if(JudgeUtilsAx.isNotEmpty(wrapper.getGroupBy())) {
            selectSql.append(SymbolConstant.GROUP_BY).append(wrapper.getGroupBy());
        }
        if(JudgeUtilsAx.isNotEmpty(wrapper.getHaving())) {
            selectSql.append(SymbolConstant.HAVING).append(wrapper.getHaving());
        }
        if(CustomUtil.isNotBlank(wrapper.getOrderBy().toString())) {
            selectSql.append(SymbolConstant.ORDER_BY).append(wrapper.getOrderBy());
        }
        if(!wrapper.getHavingParams().isEmpty()) {
            wrapper.getParamValues().addAll(wrapper.getHavingParams());
        }
        return selectSql.toString();
    }



    public ExecuteSqlHandler getSqlExecuteAction() {
        return executeSqlHandler;
    }

    public void setSqlExecuteAction(ExecuteSqlHandler executeSqlHandler) {
        this.executeSqlHandler = executeSqlHandler;
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

    public String getLogicField() {
        return logicField;
    }


}
