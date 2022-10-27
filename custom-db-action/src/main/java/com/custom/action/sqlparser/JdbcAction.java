package com.custom.action.sqlparser;

import com.custom.action.condition.*;
import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.executor.JdbcExecutorFactory;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.comm.annotations.check.CheckExecute;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.page.DbPageRows;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.jdbc.CustomConfigHelper;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.transaction.DbConnGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2022/4/13 20:49
 * @desc:
 */
@SuppressWarnings("unchecked")
public class JdbcAction extends AbstractSqlExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JdbcAction.class);
    private int order;
    private DbDataSource dbDataSource;
    private JdbcExecutorFactory executorFactory;

    public JdbcAction(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        // 创建sql执行器
        this.executorFactory = new JdbcExecutorFactory(dbDataSource, dbCustomStrategy);
        this.dbDataSource = dbDataSource;
        this.order = dbDataSource.getOrder();
    }

    public JdbcAction(){}


    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(Class<T> entityClass, String condition, Object... params) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass, order);
            FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(condition);

            // 封装结果
            String selectSql = sqlBuilder.createTargetSql() + executor.execute();
            List<T> result = executorFactory.selectBySql(entityClass, selectSql, params);
            this.injectOtherResult(entityClass, sqlBuilder, result);

            // 清除暂存
            sqlBuilder.clear();
            return result;
        } catch (Exception e) {
            this.throwsException(e);
            return new ArrayList<>();
        }
    }

    @Override
    public <T> List<T> selectListBySql(Class<T> entityClass, String sql, Object... params) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass, order);
            List<T> result = executorFactory.selectBySql(entityClass, sql, params);
            this.injectOtherResult(entityClass, sqlBuilder, result);
            return result;
        }catch (Exception e) {
            this.throwsException(e);
            return new ArrayList<>();
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPage(Class<T> entityClass, String condition, DbPageRows<T> dbPageRows, Object... params) {
        if(dbPageRows == null) {
            dbPageRows = new DbPageRows<>();
        }
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass, order);
            FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(condition);

            // 封装结果
            String selectSql = sqlBuilder.createTargetSql() + executor.execute();
            this.buildPageResult(entityClass, selectSql, dbPageRows, params);

            // 注入一对一，一对多
            this.injectOtherResult(entityClass, sqlBuilder, dbPageRows.getData());

            // 清除暂存
            sqlBuilder.clear();
        }catch (Exception e) {
            this.throwsException(e);
        }
        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectByKey(Class<T> entityClass, Serializable key) {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass, order);
        String condition = sqlBuilder.createKeyCondition(key);
        return selectOne(entityClass, condition, key);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys) {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass, order);
        String condition = sqlBuilder.createKeysCondition(keys);
        return selectList(entityClass, condition, keys.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOne(Class<T> entityClass, String condition, Object... params) {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass, order);
        try {
            FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(condition);
            String selectSql = sqlBuilder.createTargetSql() + executor.execute();
            T result = selectOneBySql(entityClass, selectSql, params);
            this.injectOtherResult(entityClass, sqlBuilder, result);
            sqlBuilder.clear();
            return result;
        }catch (Exception e) {
            this.throwsException(e);
            return null;
        }
    }

    @Override
    public <T> T selectOneBySql(Class<T> entityClass, String sql, Object... params) {
        try {
            return executorFactory.selectOneSql(entityClass, sql, params);
        }catch (Exception e) {
            this.throwsException(e);
            return null;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOne(T entity) {
        ConditionWrapper<T> conditionWrapper = Conditions.allEqQuery(entity);
        return selectOne(conditionWrapper);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(T entity) {
        ConditionWrapper<T> conditionWrapper = Conditions.allEqQuery(entity);
        return selectList(conditionWrapper);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPage(T entity, DbPageRows<T> pageRows) {
        Asserts.npe(pageRows, "Missing paging parameter");
        DefaultConditionWrapper<T> conditionWrapper = Conditions.allEqQuery(entity);
        conditionWrapper.pageParams(pageRows.getPageIndex(), pageRows.getPageSize());
        return selectPage(conditionWrapper);
    }

    @Override
    public <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception {
        return executorFactory.selectArrays(t, sql, params);
    }

    @Override
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        return executorFactory.selectObjBySql(sql, params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPage(ConditionWrapper<T> wrapper) {
        if(!wrapper.hasPageParams()) {
            ExThrowsUtil.toCustom("Missing paging parameter：pageIndex：%s, pageSize：%s", wrapper.getPageIndex(), wrapper.getPageSize());
        }
        DbPageRows<T> dbPageRows = new DbPageRows<>(wrapper.getPageIndex(), wrapper.getPageSize());
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), order);
            String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
            this.buildPageResult(wrapper.getEntityClass(), selectSql, dbPageRows, wrapper.getParamValues().toArray());
            this.injectOtherResult(wrapper.getEntityClass(), sqlBuilder, dbPageRows.getData());
        }catch (Exception e) {
            this.throwsException(e);
        }
        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(ConditionWrapper<T> wrapper) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), order);
            String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
            List<T> result = executorFactory.selectBySql(wrapper.getEntityClass(), selectSql, wrapper.getParamValues().toArray());
            this.injectOtherResult(wrapper.getEntityClass(), sqlBuilder, result);
            return result;
        }catch (Exception e) {
            this.throwsException(e);
            return new ArrayList<>();
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOne(ConditionWrapper<T> wrapper) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), order);
            String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
            T result = selectOneBySql(wrapper.getEntityClass(), selectSql, wrapper.getParamValues().toArray());
            this.injectOtherResult(wrapper.getEntityClass(), sqlBuilder, result);
            return result;
        }catch (Exception e) {
            this.throwsException(e);
            return null;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> long selectCount(ConditionWrapper<T> wrapper) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), order);
            // 创建SQL
            String selectCountSql = sqlBuilder.createSelectCountSql(wrapper);
            return (long) executorFactory.selectObjBySql(selectCountSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            this.throwsException(e);
            return 0L;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> Object selectObj(ConditionWrapper<T> wrapper) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), order);
            String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
            return executorFactory.selectObjBySql(selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            this.throwsException(e);
            return null;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), order);
            String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
            return executorFactory.selectObjsBySql(selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            this.throwsException(e);
            return new ArrayList<>();
        }

    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> Map<String, Object> selectMap(ConditionWrapper<T> wrapper) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), order);
            String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
            return executorFactory.selectMapBySql(selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            this.throwsException(e);
            return new HashMap<>();
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), order);
            String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
            return executorFactory.selectMapsBySql(selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            this.throwsException(e);
            return new ArrayList<>();
        }
    }

    @Override
    public <T> DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper) {
        if(!wrapper.hasPageParams()) {
            ExThrowsUtil.toCustom("Missing paging parameter：pageIndex：%s, pageSize：%s", wrapper.getPageIndex(), wrapper.getPageSize());
        }

        DbPageRows<Map<String, Object>> dbPageRows = new DbPageRows<>(wrapper.getPageIndex(), wrapper.getPageSize());
        List<Map<String, Object>> dataList = new ArrayList<>();
        long count = 0;

        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), order);
            // 创建查询SQL
            String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
            // selectCountSql
            String selectCountSql = SqlExecTemplate.format(SqlExecTemplate.SELECT_COUNT, selectSql);
            Object[] params = wrapper.getParamValues().toArray();

            count = (long) executorFactory.selectObjBySql(selectCountSql, params);
            if (count > 0) {
                selectSql = String.format("%s \nLIMIT %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
                dataList = executorFactory.selectMapsBySql(selectSql, params);
            }

            sqlBuilder.clear();

        } catch (Exception e) {
            this.throwsException(e);
        }
        return dbPageRows.setTotal(count).setData(dataList);
    }


    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByKey(Class<T> entityClass, Serializable key) {
        HandleDeleteSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(entityClass, order);
        String condition = sqlBuilder.createKeyCondition(key);
        return this.deleteByCondition(entityClass, condition, key);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys) {
        HandleDeleteSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(entityClass, order);
        String condition = sqlBuilder.createKeysCondition(keys);
        return this.deleteByCondition(entityClass, condition, keys.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByCondition(Class<T> entityClass, String condition, Object... params) {
        HandleDeleteSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(entityClass, order);
        String deleteSql = sqlBuilder.createTargetSql();
        int i = 0;

        try {
            FullSqlConditionExecutor conditionExecutor = sqlBuilder.addLogicCondition(condition);
            deleteSql = deleteSql + conditionExecutor.execute();
            i = this.executeSql(deleteSql, params);
            sqlBuilder.clear();
        } catch (Exception e) {
            this.throwsException(e);
        }
        return i;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteSelective(ConditionWrapper<T> wrapper) {
        return this.deleteByCondition(wrapper.getEntityClass(),
                wrapper.getFinalConditional(),
                wrapper.getParamValues().toArray()
        );
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(T entity)  {
        int i = 0;
        Class<T> targetClass = (Class<T>) entity.getClass();
        HandleInsertSqlBuilder<T> sqlBuilder = TableInfoCache.getInsertSqlBuilderCache(targetClass, order);
        sqlBuilder.setEntityList(Collections.singletonList(entity));
        String insertSql = sqlBuilder.createTargetSql();
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        try {
            i = executorFactory.executeInsert(insertSql,
                    Collections.singletonList(entity),
                    keyParserModel.getField(),
                    sqlBuilder.getSqlParams());
        } catch (Exception e) {
            this.throwsException(e);
        }
        return i;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insertBatch(List<T> ts) {
        Asserts.notEmpty(ts, "insert data cannot be empty ");
        Class<T> targetClass = (Class<T>) ts.get(0).getClass();
        HandleInsertSqlBuilder<T> sqlBuilder = TableInfoCache.getInsertSqlBuilderCache(targetClass, order);
        sqlBuilder.setEntityList(ts);
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        int res = 0;
        try {
            String insertSql = sqlBuilder.createTargetSql();
            executorFactory.executeInsert(insertSql, ts, keyParserModel.getField(), sqlBuilder.getSqlParams());
        } catch (Exception e) {
            this.throwsException(e);
        }
        return res;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByKey(T entity) {
        Class<T> targetClass = (Class<T>) entity.getClass();
        HandleUpdateSqlBuilder<T> sqlBuilder = TableInfoCache.getUpdateSqlBuilderCache(targetClass, order);
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        Serializable value = (Serializable) keyParserModel.getValue(entity);
        String condition = sqlBuilder.createKeyCondition(value);
        return this.updateByCondition(entity, condition, value);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateSelective(T entity, ConditionWrapper<T> wrapper) {
        return this.updateByCondition(entity, wrapper.getFinalConditional(),
                wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByCondition(T entity, String condition, Object... params) {
        if (JudgeUtil.isEmpty(condition)) {
            ExThrowsUtil.toNull("修改条件不能为空");
        }
        try {
            // 创建update sql创建对象
            Class<T> targetClass = (Class<T>) entity.getClass();
            HandleUpdateSqlBuilder<T> sqlBuilder = TableInfoCache.getUpdateSqlBuilderCache(targetClass, order);
            sqlBuilder.injectEntity(entity);

            // 创建update sql
            String updateSql = sqlBuilder.createTargetSql();
            List<Object> sqlParamList = new ArrayList<>(sqlBuilder.getSqlParamList());
            CustomUtil.addParams(sqlParamList, params);

            // 拼接sql
            FullSqlConditionExecutor conditionExecutor = sqlBuilder.addLogicCondition(condition);
            updateSql = updateSql + conditionExecutor.execute();

            // 清除暂存
            sqlBuilder.clear();

            return executeSql(updateSql, sqlParamList.toArray());
        } catch (Exception e) {
            this.throwsException(e);
            return 0;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateSelective(AbstractUpdateSet<T> updateSet) {
        Class<T> entityClass = updateSet.thisEntityClass();
        HandleUpdateSqlBuilder<T> sqlBuilder = TableInfoCache.getUpdateSqlBuilderCache(entityClass, order);
        UpdateSetWrapper<T> updateSetWrapper = updateSet.getUpdateSetWrapper();
        ConditionWrapper<T> conditionWrapper = updateSet.getConditionWrapper();

        try {
            FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(conditionWrapper.getFinalConditional());
            String finalConditional = executor.execute();
            List<Object> sqlParams = updateSetWrapper.getSetParams();
            CustomUtil.addParams(sqlParams, conditionWrapper.getParamValues());

            // 创建SQL
            String updateSql = SqlExecTemplate.format(SqlExecTemplate.UPDATE_DATA, sqlBuilder.getTable(), sqlBuilder.getAlias(),
                    updateSetWrapper.getSqlSetter(), finalConditional);
            return executeSql(updateSql, sqlParams.toArray());
        }catch (Exception e) {
            this.throwsException(e);
            return 0;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int save(T entity) {
        Class<T> targetClass = (Class<T>) entity.getClass();
        EmptySqlBuilder<T> sqlBuilder = TableInfoCache.getEmptySqlBuilder(targetClass, order);
        sqlBuilder.injectEntity(entity);
        return Objects.nonNull(sqlBuilder.primaryKeyVal()) ? updateByKey(entity) : insert(entity);
    }

    @Override
    public int executeSql(String sql, Object... params) {
        try {
            return executorFactory.executeAnySql(sql, params);
        }catch (Exception e) {
            this.throwsException(e);
            return 0;
        }
    }

    @Override
    public void createTables(Class<?>... arr) {
        TableParseModel<?> tableSqlBuilder;
        for (int i = arr.length - 1; i >= 0; i--) {
            tableSqlBuilder = TableInfoCache.getTableModel(arr[i]);
            String exitsTableSql = DbConnGlobal.exitsTableSql(tableSqlBuilder.getTable(), dbDataSource);
            try {
                if(!executorFactory.hasTableInfo(exitsTableSql)) {
                    String createTableSql = tableSqlBuilder.createTableSql();
                    executorFactory.execTable(createTableSql);
                    logger.info("createTableSql ->\n " + createTableSql);
                }
            }catch (Exception e) {
                this.throwsException(e);
            }
        }
    }

    @Override
    public void dropTables(Class<?>... arr) {
        for (int i = arr.length - 1; i >= 0; i--) {
            TableParseModel<?> tableSqlBuilder = TableInfoCache.getTableModel(arr[i]);
            String dropTableSql = tableSqlBuilder.dropTableSql();
            try {
                executorFactory.execTable(dropTableSql);
                logger.warn("drop table '{}' completed\n", tableSqlBuilder.getTable());
            }catch (Exception e) {
                this.throwsException(e);
            }
        }
    }

    @Override
    public DbDataSource getDbDataSource() {
        return this.dbDataSource;
    }

    /**
     * 分页数据整合
     */
    protected <T> void buildPageResult(Class<T> t, String selectSql, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        List<T> dataList = new ArrayList<>();

        // 格式化并获取selectCountSQL
        String selectCountSql = SqlExecTemplate.format(SqlExecTemplate.SELECT_COUNT, selectSql);
        long count = (long) executorFactory.selectObjBySql(selectCountSql, params);

        if (count > 0) {
            selectSql = dbPageRows.getPageIndex() == 1 ?
                    String.format("%s \nLIMIT %s", selectSql, dbPageRows.getPageSize())
                    : String.format("%s \nLIMIT %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
            dataList = executorFactory.selectBySql(t, selectSql, params);
        }
        dbPageRows.setTotal(count).setData(dataList);
    }


}
