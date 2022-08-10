package com.custom.action.dbaction;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.action.sqlparser.HandleSelectSqlBuilder;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.condition.SFunction;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.page.DbPageRows;
import com.custom.configuration.DbCustomStrategy;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/8 14:49
 * @Desc：方法执行处理抽象入口
 **/
@SuppressWarnings("unchecked")
public abstract class AbstractSqlExecutor extends JdbcWrapperExecutor {

    /*--------------------------------------- select ---------------------------------------*/
    public abstract <T> List<T> selectList(Class<T> entityClass, String condition, Object... params);
    public abstract <T> List<T> selectListBySql(Class<T> entityClass, String sql, Object... params);
    public abstract <T> DbPageRows<T> selectPage(Class<T> entityClass, String condition, DbPageRows<T> dbPageRows, Object... params);
    public abstract <T> T selectByKey(Class<T> entityClass, Object key);
    public abstract <T> List<T> selectBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys);
    public abstract <T> T selectOne(Class<T> entityClass, String condition, Object... params);
    public abstract <T> T selectOneBySql(Class<T> entityClass, String sql, Object... params);
    public abstract <T> T selectOne(T entity);
    public abstract <T> List<T> selectList(T entity);
    public abstract <T> DbPageRows<T> selectPage(T entity, DbPageRows<T> pageRows);

    /**
     * ConditionWrapper(条件构造器)
     */
    public abstract <T> DbPageRows<T> selectPage(ConditionWrapper<T> wrapper);
    public abstract <T> List<T> selectList(ConditionWrapper<T> wrapper);
    public abstract <T> T selectOne(ConditionWrapper<T> wrapper);
    public abstract <T> long selectCount(ConditionWrapper<T> wrapper);
    public abstract <T> Object selectObj(ConditionWrapper<T> wrapper);
    public abstract <T> List<Object> selectObjs(ConditionWrapper<T> wrapper);
    public abstract <T> Map<String, Object> selectMap(ConditionWrapper<T> wrapper);
    public abstract <T> List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper);
    public abstract <T> DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper);


    /*--------------------------------------- delete ---------------------------------------*/
    public abstract <T> int deleteByKey(Class<T> entityClass, Object key);
    public abstract <T> int deleteBatchKeys(Class<T> entityClass, Collection<?> keys);
    public abstract <T> int deleteByCondition(Class<T> entityClass, String condition, Object... params);
    public abstract <T> int deleteSelective(ConditionWrapper<T> wrapper);

    /*--------------------------------------- insert ---------------------------------------*/
    public abstract <T> int insert(T entity);
    public abstract <T> int insertBatch(List<T> tList);

    /*--------------------------------------- update ---------------------------------------*/
    public abstract <T> int updateByKey(T entity);
    public abstract <T> int updateColumnByKey(T entity, Consumer<List<SFunction<T, ?>>> updateColumns);
    public abstract <T> int updateSelective(T entity, ConditionWrapper<T> wrapper);
    public abstract <T> int updateByCondition(T entity, String condition, Object... params);

    /**
     * updateSet sql set设置器
     */
    public abstract <T> int updateSelective(AbstractUpdateSet<T> updateSet);

    /*--------------------------------------- comm ---------------------------------------*/
    public abstract <T> int save(T entity);
    public abstract int executeSql(String sql, Object... params);
    public abstract void createTables(Class<?>... arr);
    public abstract void dropTables(Class<?>... arr);
    public abstract <T> TableSqlBuilder<T> defaultSqlBuilder(Class<T> entityClass);
    public abstract <T> TableSqlBuilder<T> updateSqlBuilder(List<T> tList);


    private DbCustomStrategy dbCustomStrategy;
    private String logicColumn = SymbolConstant.EMPTY;
    private String logicDeleteQuerySql = SymbolConstant.EMPTY;

    /**
     * 初始化逻辑删除的sql
     */
    public void initLogic() {
        if (isLogicDeleteOpen(dbCustomStrategy)) {
            if (JudgeUtil.isEmpty(dbCustomStrategy.getNotDeleteLogicValue())
                    || JudgeUtil.isEmpty(dbCustomStrategy.getDeleteLogicValue())) {
                ExThrowsUtil.toCustom("The corresponding value of the logical deletion field is not configured");
            }
            this.logicColumn = dbCustomStrategy.getDbFieldDeleteLogic();
            this.logicDeleteQuerySql = String.format("%s = %s",
                    logicColumn, dbCustomStrategy.getNotDeleteLogicValue());
        }
    }

    /**
     * 添加逻辑删除的部分sql
     */
    public FullSqlConditionExecutor handleLogicWithCondition(String alias, final String condition, String logicSql, String tableName) throws Exception {
        return handleLogicWithCondition(alias, condition, logicColumn, logicSql, tableName);
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
            ExThrowsUtil.toNull("Entity object cannot be empty");
        }
        return buildSqlOperationTemplate(Collections.singletonList(entity), method);
    }

    /**
     * 获取实体解析模板中的操作对象
     */
    protected <T, R extends AbstractSqlBuilder<T>> R buildSqlOperationTemplate(List<T> entityList, ExecuteMethod method) {
        TableSqlBuilder<T> tableModelCache = updateTableSqlBuilder(entityList);
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
        TableSqlBuilder<T> tableSqlBuilder = defaultTableSqlBuilder(entityClass);
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
        FullSqlConditionExecutor conditionExecutor = this.handleLogicWithCondition(sqlBuilder.getAlias(),
                wrapper.getFinalConditional(), logicColumn, getLogicDeleteQuerySql(), sqlBuilder.getTable());
        selectSql.append(conditionExecutor.execute());
        if (JudgeUtil.isNotEmpty(wrapper.getCustomizeSql())) {
            selectSql.append(wrapper.getCustomizeSql());
        }
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

    public void throwsException(Exception e) {
        if (e instanceof CustomCheckException) {
            throw new CustomCheckException(e.getMessage());
        }
        if (e instanceof NullPointerException) {
            throw new NullPointerException(e.getMessage());
        }
        throw new RuntimeException(e.fillInStackTrace());
    }
}
