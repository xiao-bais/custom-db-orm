package com.custom.action.core;

import com.custom.action.condition.*;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.extend.MultiResultInjector;
import com.custom.action.interfaces.SqlQueryAfter;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.comm.annotations.check.CheckExecute;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.page.DbPageRows;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import com.custom.jdbc.interfaces.TransactionWrapper;
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
public class JdbcAction extends AbstractSqlExecutor implements SqlQueryAfter {

    private static final Logger logger = LoggerFactory.getLogger(JdbcAction.class);
    private DbDataSource dbDataSource;
    private JdbcExecutorFactory executorFactory;

    public JdbcAction(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        // 创建sql执行器
        this.executorFactory = new JdbcExecutorFactory(dbDataSource, dbCustomStrategy);
        this.dbDataSource = dbDataSource;
    }

    public JdbcAction(){}


    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(Class<T> entityClass, String condition, Object... params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass, executorFactory);
        FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(condition);

        // 封装结果
        String selectSql = sqlBuilder.createTargetSql() + executor.execute();
        List<T> result = executorFactory.selectListBySql(entityClass, selectSql, params);
        this.handle(entityClass, result);
        return result;
    }

    @Override
    public <T> List<T> selectListBySql(Class<T> entityClass, String sql, Object... params) throws Exception {
        return executorFactory.selectListBySql(entityClass, sql, params);
    }


    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPage(Class<T> entityClass, String condition, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        if(dbPageRows == null) {
            dbPageRows = new DbPageRows<>();
        }
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass, executorFactory);
        FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(condition);

        // 封装结果
        String selectSql = sqlBuilder.createTargetSql() + executor.execute();
        this.buildPageResult(entityClass, selectSql, dbPageRows, params);

        // 注入一对一，一对多
        this.handle(entityClass, dbPageRows.getData());
        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectByKey(Class<T> entityClass, Serializable key) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass, executorFactory);
        String condition = sqlBuilder.createKeyCondition(key);
        return selectOne(entityClass, condition, key);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass, executorFactory);
        String condition = sqlBuilder.createKeysCondition(keys);
        return selectList(entityClass, condition, keys.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOne(Class<T> entityClass, String condition, Object... params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass, executorFactory);
        FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(condition);

        String selectSql = sqlBuilder.createTargetSql() + executor.execute();
        T result = selectOneBySql(entityClass, selectSql, params);

        this.handle(entityClass, Collections.singletonList(result));
        return result;
    }

    @Override
    public <T> T selectOneBySql(Class<T> entityClass, String sql, Object... params) throws Exception {
       return executorFactory.selectOneSql(entityClass, sql, params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOne(T entity) throws Exception {
        ConditionWrapper<T> conditionWrapper = Conditions.allEqQuery(entity);
        return selectOne(conditionWrapper);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(T entity) throws Exception {
        ConditionWrapper<T> conditionWrapper = Conditions.allEqQuery(entity);
        return selectList(conditionWrapper);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPage(T entity, DbPageRows<T> pageRows) throws Exception {
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
    public <K, V> Map<K, V> selectMap(Class<K> kClass, Class<V> vClass, String sql, Object... params) throws Exception {
       return executorFactory.selectMap(kClass, vClass, sql, params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPage(ConditionWrapper<T> wrapper) throws Exception {
        if(!wrapper.hasPageParams()) {
            throw new CustomCheckException("Missing paging parameter：pageIndex：%s, pageSize：%s", wrapper.getPageIndex(), wrapper.getPageSize());
        }
        DbPageRows<T> dbPageRows = new DbPageRows<>(wrapper.getPageIndex(), wrapper.getPageSize());
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), executorFactory);

        String selectSql = sqlBuilder.executeSqlBuilder(wrapper);

        this.buildPageResult(wrapper.getEntityClass(), selectSql, dbPageRows, wrapper.getParamValues().toArray());
        this.handle(wrapper.getEntityClass(), dbPageRows.getData());

        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(ConditionWrapper<T> wrapper) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), executorFactory);
        String selectSql = sqlBuilder.executeSqlBuilder(wrapper);

        List<T> result = executorFactory.selectListBySql(wrapper.getEntityClass(), selectSql, wrapper.getParamValues().toArray());
        this.handle(wrapper.getEntityClass(), result);
        return result;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOne(ConditionWrapper<T> wrapper) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), executorFactory);
        String selectSql = sqlBuilder.executeSqlBuilder(wrapper);

        T result = selectOneBySql(wrapper.getEntityClass(), selectSql, wrapper.getParamValues().toArray());
        this.handle(wrapper.getEntityClass(), Collections.singletonList(result));

        return result;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> long selectCount(ConditionWrapper<T> wrapper) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), executorFactory);
        // 创建SQL
        String selectCountSql = sqlBuilder.createSelectCountSql(wrapper);
        return (long) executorFactory.selectObjBySql(selectCountSql, wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> Object selectObj(ConditionWrapper<T> wrapper) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), executorFactory);
        String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
        return executorFactory.selectObjBySql(selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), executorFactory);
        String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
        return executorFactory.selectObjsBySql(selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> Map<String, Object> selectOneMap(ConditionWrapper<T> wrapper) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), executorFactory);
        String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
        return executorFactory.selectMapBySql(selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<Map<String, Object>> selectListMap(ConditionWrapper<T> wrapper) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), executorFactory);
        String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
        return executorFactory.selectMapsBySql(selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    public <T> DbPageRows<Map<String, Object>> selectPageMap(ConditionWrapper<T> wrapper) throws Exception {
        if(!wrapper.hasPageParams()) {
            throw new CustomCheckException("Missing paging parameter：pageIndex：%s, pageSize：%s", wrapper.getPageIndex(), wrapper.getPageSize());
        }

        DbPageRows<Map<String, Object>> dbPageRows = new DbPageRows<>(wrapper.getPageIndex(), wrapper.getPageSize());
        List<Map<String, Object>> dataList = new ArrayList<>();

        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), executorFactory);
        // 创建查询SQL
        String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
        // selectCountSql
        String selectCountSql = SqlExecTemplate.format(SqlExecTemplate.SELECT_COUNT, selectSql);
        Object[] params = wrapper.getParamValues().toArray();

        long count = (long) executorFactory.selectObjBySql(selectCountSql, params);
        if (count > 0) {
            DatabaseAdapter databaseAdapter = executorFactory.getDatabaseAdapter();
            selectSql = databaseAdapter.handlePage(selectSql, dbPageRows.getPageIndex(), dbPageRows.getPageSize());
            dataList = executorFactory.selectMapsBySql(selectSql, params);
        }
        return dbPageRows.setTotal(count).setData(dataList);
    }

    @Override
    public <T, K, V> Map<K, V> selectMap(ConditionWrapper<T> wrapper, Class<K> kClass, Class<V> vClass) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass(), executorFactory);
        String selectSql = sqlBuilder.executeSqlBuilder(wrapper);
        return executorFactory.selectMap(kClass, vClass, selectSql, wrapper.getParamValues().toArray());
    }


    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByKey(Class<T> entityClass, Serializable key) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(entityClass, executorFactory);
        String condition = sqlBuilder.createKeyCondition(key);
        return this.deleteByCondition(entityClass, condition, key);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(entityClass, executorFactory);
        String condition = sqlBuilder.createKeysCondition(keys);
        return this.deleteByCondition(entityClass, condition, keys.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByCondition(Class<T> entityClass, String condition, Object... params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(entityClass, executorFactory);
        String deleteSql = sqlBuilder.createTargetSql();

        FullSqlConditionExecutor conditionExecutor = sqlBuilder.addLogicCondition(condition);
        deleteSql = deleteSql + conditionExecutor.execute();

        return this.executeSql(deleteSql, params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteSelective(ConditionWrapper<T> wrapper) throws Exception {
        return this.deleteByCondition(wrapper.getEntityClass(),
                wrapper.getFinalConditional(),
                wrapper.getParamValues().toArray()
        );
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(T entity) throws Exception {
        Class<T> targetClass = (Class<T>) entity.getClass();
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getInsertSqlBuilderCache(targetClass, executorFactory);
        List<T> list = Collections.singletonList(entity);

        List<Object> sqlParamList = new ArrayList<>();
        String insertSql = sqlBuilder.createTargetSql(list, sqlParamList);
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();

        return executorFactory.executeInsert(insertSql, list, keyParserModel.getField(), sqlParamList.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insertBatch(List<T> ts) throws Exception {
        Asserts.notEmpty(ts, "insert data cannot be empty ");
        Class<T> targetClass = (Class<T>) ts.get(0).getClass();
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getInsertSqlBuilderCache(targetClass, executorFactory);

        List<Object> sqlParamList = new ArrayList<>();
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
         String insertSql = sqlBuilder.createTargetSql(ts, sqlParamList);

        return executorFactory.executeInsert(insertSql, ts, keyParserModel.getField(), sqlParamList.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByKey(T entity) throws Exception {
        Class<T> targetClass = (Class<T>) entity.getClass();
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getUpdateSqlBuilderCache(targetClass, executorFactory);

        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        Serializable value = (Serializable) keyParserModel.getValue(entity);

        String condition = sqlBuilder.createKeyCondition(value);
        return this.updateByCondition(entity, condition, value);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateSelective(T entity, ConditionWrapper<T> wrapper) throws Exception {
        return this.updateByCondition(entity, wrapper.getFinalConditional(),
                wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByCondition(T entity, String condition, Object... params) throws Exception {
        if (JudgeUtil.isEmpty(condition)) {
            throw new NullPointerException("修改条件不能为空");
        }

        // 创建update sql创建对象
        Class<T> targetClass = (Class<T>) entity.getClass();
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getUpdateSqlBuilderCache(targetClass, executorFactory);

        // 创建update sql
        List<Object> sqlParamList = new ArrayList<>();
        String updateSql = sqlBuilder.createTargetSql(entity, sqlParamList);
        CustomUtil.addParams(sqlParamList, params);

        // 拼接sql
        FullSqlConditionExecutor conditionExecutor = sqlBuilder.addLogicCondition(condition);
        updateSql = updateSql + conditionExecutor.execute();

        return executeSql(updateSql, sqlParamList.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateSelective(AbstractUpdateSet<T> updateSet) throws Exception {
        Class<T> entityClass = updateSet.thisEntityClass();
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getUpdateSqlBuilderCache(entityClass, executorFactory);
        UpdateSetWrapper<T> updateSetWrapper = updateSet.getUpdateSetWrapper();
        ConditionWrapper<T> conditionWrapper = updateSet.getConditionWrapper();

        FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(conditionWrapper.getFinalConditional());
        String finalConditional = executor.execute();
        List<Object> sqlParams = updateSetWrapper.getSetParams();
        CustomUtil.addParams(sqlParams, conditionWrapper.getParamValues());

        // 创建SQL
        String updateSql = SqlExecTemplate.format(SqlExecTemplate.UPDATE_DATA, sqlBuilder.getTable(), sqlBuilder.getAlias(),
                updateSetWrapper.getSqlSetter(), finalConditional);
        return executeSql(updateSql, sqlParams.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int save(T entity) throws Exception {
        Class<T> targetClass = (Class<T>) entity.getClass();
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getEmptySqlBuilder(targetClass, executorFactory);
        return Objects.nonNull(sqlBuilder.primaryKeyVal(entity)) ? updateByKey(entity) : insert(entity);
    }

    @Override
    public int executeSql(String sql, Object... params) throws Exception {
        return executorFactory.executeAnySql(sql, params);
    }

    @Override
    public void createTables(Class<?>... arr) throws Exception {
        TableParseModel<?> tableSqlBuilder;
        for (int i = arr.length - 1; i >= 0; i--) {
            tableSqlBuilder = TableInfoCache.getTableModel(arr[i]);
            String exitsTableSql = DbConnGlobal.exitsTableSql(tableSqlBuilder.getTable(), dbDataSource);
            if(!executorFactory.hasTableInfo(exitsTableSql)) {
                String createTableSql = tableSqlBuilder.createTableSql();
                executorFactory.execTable(createTableSql);
                logger.info("createTableSql ->\n " + createTableSql);
            }
        }
    }

    @Override
    public void dropTables(Class<?>... arr) throws Exception {
        for (int i = arr.length - 1; i >= 0; i--) {
            TableParseModel<?> tableSqlBuilder = TableInfoCache.getTableModel(arr[i]);
            String dropTableSql = tableSqlBuilder.dropTableSql();
            executorFactory.execTable(dropTableSql);
            logger.warn("drop table '{}' completed\n", tableSqlBuilder.getTable());
        }
    }

    @Override
    public DbDataSource getDbDataSource() {
        return this.dbDataSource;
    }

    @Override
    public JdbcExecutorFactory getExecutorFactory() {
        return this.executorFactory;
    }

    @Override
    public void execTrans(TransactionWrapper wrapper) throws Exception {
        executorFactory.handleTransaction(wrapper);
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
            DatabaseAdapter databaseAdapter = executorFactory.getDatabaseAdapter();
            selectSql = databaseAdapter.handlePage(selectSql, dbPageRows.getPageIndex(), dbPageRows.getPageSize());
            dataList = this.executorFactory.selectListBySql(t, selectSql, params);
        }
        dbPageRows.setTotal(count).setData(dataList);
    }


    @Override
    public <T> void handle(Class<T> target, List<T> result) throws Exception {
        HandleSelectSqlBuilder<T> selectSqlBuilder = TableInfoCache.getSelectSqlBuilderCache(target, executorFactory);
        if (selectSqlBuilder.isExistNeedInjectResult() && result != null) {
            MultiResultInjector<T> resultInjector = new MultiResultInjector<>(target, this, target);
            resultInjector.injectorValue(result);
        }
    }
}
