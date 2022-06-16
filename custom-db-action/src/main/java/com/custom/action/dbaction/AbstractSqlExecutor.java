package com.custom.action.dbaction;

import com.custom.action.interfaces.FullSqlExecutorHandler;
import com.custom.action.sqlparser.HandleSelectSqlBuilder;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.action.util.DbUtil;
import com.custom.action.wrapper.ConditionWrapper;
import com.custom.action.wrapper.SFunction;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.page.DbPageRows;
import com.custom.configuration.DbCustomStrategy;

import java.io.Serializable;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/8 14:49
 * @Desc：方法执行处理抽象入口
 **/
@SuppressWarnings("unchecked")
public abstract class AbstractSqlExecutor extends SimpleJdbcExecutor {

    /*--------------------------------------- select ---------------------------------------*/
    public abstract <T> List<T> selectList(Class<T> t, String condition, Object... params) throws Exception;
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

    private DbCustomStrategy dbCustomStrategy;
    private String logicColumn = SymbolConstant.EMPTY;
    private String logicDeleteQuerySql = SymbolConstant.EMPTY;

    /**
     * 初始化逻辑删除的sql
     */
    public void initLogic() {
        if(isLogicDeleteOpen(dbCustomStrategy)) {
            if(JudgeUtil.isEmpty(dbCustomStrategy.getNotDeleteLogicValue()) || JudgeUtil.isEmpty(dbCustomStrategy.getDeleteLogicValue())) {
                ExThrowsUtil.toCustom("The corresponding value of the logical deletion field is not configured");
            }
            this.logicColumn = dbCustomStrategy.getDbFieldDeleteLogic();
            this.logicDeleteQuerySql = String.format("%s = %s",
                    logicColumn, dbCustomStrategy.getNotDeleteLogicValue());
        }
    }

    /**
     * 是否开启了逻辑删除字段
     */
    public static boolean isLogicDeleteOpen(DbCustomStrategy dbCustomStrategy) {
        if(dbCustomStrategy == null) {
            dbCustomStrategy = new DbCustomStrategy();
        }
        return JudgeUtil.isNotEmpty(dbCustomStrategy.getDbFieldDeleteLogic());
    }

    /**
     * 添加逻辑删除的部分sql
     */
    public FullSqlExecutorHandler handleLogicWithCondition(String alias, final String condition, String logicSql, String tableName) throws Exception {
        if(!DbUtil.checkLogicFieldIsExist(tableName, logicColumn, getJdbcExecutor())) {
            logicSql = SymbolConstant.EMPTY;
        }
        final String finalLogicSql = logicSql;
        return () -> {
            if (JudgeUtil.isNotEmpty(condition)) {
                return JudgeUtil.isNotEmpty(finalLogicSql) ?
                        String.format("\nwhere %s.%s \n%s ", alias, finalLogicSql, condition.trim())
                        : String.format("\nwhere %s ", DbUtil.trimAppendSqlCondition(condition));
            } else {
                return JudgeUtil.isNotEmpty(finalLogicSql) ?
                        String.format("\nwhere %s.%s ", alias, finalLogicSql)
                        : condition == null ? SymbolConstant.EMPTY : condition.trim();
            }
        };
    }




    /**
     * 从缓存中获取实体解析模板，若缓存中没有，就重新构造模板（查询、删除、创建删除表）
     */
    protected <T> TableSqlBuilder<T> getEntityModelCache(Class<T> t) {
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(t);
        tableModel.setJdbcExecutor(getJdbcExecutor());
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
        tableModel.setJdbcExecutor(getJdbcExecutor());
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
        tableModel.setLogicFieldInfo(logicColumn, dbCustomStrategy.getDeleteLogicValue(), dbCustomStrategy.getNotDeleteLogicValue());
        return (R) tableModel.getSqlBuilder();
    }

    /**
     * 获取实体解析模板中的操作对象
     */
    protected <T, R extends AbstractSqlBuilder<T>> R buildSqlOperationTemplate(Class<T> entityClass, ExecuteMethod method) {
        TableSqlBuilder<T> tableSqlBuilder = getEntityModelCache(entityClass);
        tableSqlBuilder.buildSqlConstructorModel(method);
        tableSqlBuilder.setLogicFieldInfo(logicColumn, dbCustomStrategy.getDeleteLogicValue(), dbCustomStrategy.getNotDeleteLogicValue());
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
        FullSqlExecutorHandler fullSqlExecutorHandler = handleLogicWithCondition(sqlBuilder.getAlias(),
                wrapper.getFinalConditional(), getLogicDeleteQuerySql(), sqlBuilder.getTable());
        selectSql.append(fullSqlExecutorHandler.execute());
        if(JudgeUtil.isNotEmpty(wrapper.getGroupBy())) {
            selectSql.append(SymbolConstant.GROUP_BY).append(wrapper.getGroupBy());
        }
        if(JudgeUtil.isNotEmpty(wrapper.getHaving())) {
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

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    public void setDbCustomStrategy(DbCustomStrategy dbCustomStrategy) {
        this.dbCustomStrategy = dbCustomStrategy;
    }

    public String getLogicDeleteQuerySql() {
        return logicDeleteQuerySql;
    }


}
