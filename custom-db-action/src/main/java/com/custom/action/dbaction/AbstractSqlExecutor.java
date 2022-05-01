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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/8 14:49
 * @Desc：方法执行处理抽象入口
 **/
@SuppressWarnings("unchecked")
public abstract class AbstractSqlExecutor {

    /*--------------------------------------- select ---------------------------------------*/
    public abstract <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, int pageIndex, int pageSize, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, DbPageRows<T> dbPageRows, Object... params) throws Exception;
    public abstract <T> T selectOneByKey(Class<T> t, Object key) throws Exception;
    public abstract <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception;

    public abstract <T> DbPageRows<T> selectPageRows(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> List<T> selectList(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> T selectOneByCondition(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> long selectCount(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> Object selectObj(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception;


    /*--------------------------------------- delete ---------------------------------------*/
    public abstract <T> int deleteByKey(Class<T> t, Object key) throws Exception;
    public abstract <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception;
    public abstract <T> int deleteByCondition(ConditionWrapper<T> wrapper) throws Exception;

    /*--------------------------------------- insert ---------------------------------------*/
    public abstract <T> int insert(T t, boolean isGeneratedKey) throws Exception;
    public abstract <T> int insert(List<T> tList, boolean isGeneratedKey) throws Exception;

    /*--------------------------------------- update ---------------------------------------*/
    public abstract <T> int updateByKey(T t) throws Exception;
    public abstract <T> int updateByKey(T t, SFunction<T, ?>... updateColumns) throws Exception;
    public abstract <T> int updateByCondition(T t, ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> int updateByCondition(T t, String condition, Object... params) throws Exception;

    /*--------------------------------------- comm ---------------------------------------*/
    public abstract <T> long save(T t) throws Exception;
    public abstract void createTables(Class<?>... arr) throws Exception;
    public abstract void dropTables(Class<?>... arr) throws Exception;


    private SqlExecuteAction sqlExecuteAction;
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
        long count = sqlExecuteAction.executeExist(existSql);
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
        return sqlExecuteAction.query(t, true, sql, params);
    }

    /**
    * 纯sql查询单条记录
    */
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        List<T> queryList = sqlExecuteAction.query(t, true, sql, params);
        int size = queryList.size();
        if (size == 0) {
            return null;
        } else if (size > 1) {
            throw new CustomCheckException(String.format("One was queried, but more were found:(%s) ", size));
        }
        return queryList.get(SymbolConstant.DEFAULT_ZERO);
    }

    /**
    * 纯sql查询单个字段
    */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            ExThrowsUtil.toCustom("The Sql to be Not Empty");
        }
        return sqlExecuteAction.selectObjSql(sql, params);
    }

    /**
     * 纯sql查询单个字段集合
     */
    public List<Object> selectObjsBySql(String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return sqlExecuteAction.selectObjsSql(sql, params);
    }

    /**
    * 纯sql增删改
    */
    public int executeSql(String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return sqlExecuteAction.executeUpdate(sql, params);
    }

    /**
     * 直接执行查询，属于内部执行
     */
    public <T> List<T> executeQueryNotPrintSql(Class<T> t, String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return sqlExecuteAction.query(t, false, sql, params);
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
    public boolean hasTableInfo(String sql) throws Exception {
        return sqlExecuteAction.executeExist(sql) == 0L;
    }

    /**
    * 添加
    */
    public <T> int executeInsert(String sql, List<T> obj, boolean isGeneratedKey, String key, Class<?> keyType,  Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return isGeneratedKey ? sqlExecuteAction.executeInsert(obj, sql, key, keyType, params) : sqlExecuteAction.executeUpdate(sql, params);
    }


    /**
     * 从缓存中获取实体解析模板，若缓存中没有，就重新构造模板（查询、删除、创建删除表）
     */
    protected <T> TableSqlBuilder<T> getEntityModelCache(Class<T> t) {
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(t);
        tableModel.setSqlExecuteAction(sqlExecuteAction);
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
        tableModel.setSqlExecuteAction(sqlExecuteAction);
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
     * 公共获取查询sql
     */
    protected  <T> String getFullSelectSql(Class<T> t, DbPageRows<T> dbPageRows, ConditionWrapper<T> wrapper) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        StringBuilder selectSql = new StringBuilder();
        if(wrapper.getSelectColumns() != null) {
            selectSql.append(sqlBuilder.selectColumns(wrapper.getSelectColumns()));
        }else {
            selectSql.append(sqlBuilder.buildSql());
        }
        String condition = checkConditionAndLogicDeleteSql(sqlBuilder.getAlias(), wrapper.getFinalConditional(), getLogicDeleteQuerySql(), sqlBuilder.getTable());
        if(dbPageRows != null) {
            dbPageRows.setCondition(condition);
        }
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

    public String getLogicField() {
        return logicField;
    }


}
