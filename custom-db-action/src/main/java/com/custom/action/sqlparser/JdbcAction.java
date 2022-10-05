package com.custom.action.sqlparser;

import com.custom.action.condition.*;
import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.action.util.DbUtil;
import com.custom.comm.Asserts;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.annotations.check.CheckExecute;
import com.custom.comm.enums.ExecuteMethod;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.page.DbPageRows;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.jdbc.CustomSelectJdbcBasicImpl;
import com.custom.jdbc.CustomUpdateJdbcBasicImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Xiao-Bai
 * @date 2022/4/13 20:49
 * @desc:
 */
@SuppressWarnings("unchecked")
public class JdbcAction extends AbstractSqlExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JdbcAction.class);

    public JdbcAction(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        // 配置sql执行器
        this.setSelectJdbc(new CustomSelectJdbcBasicImpl(dbDataSource, dbCustomStrategy));
        this.setUpdateJdbc(new CustomUpdateJdbcBasicImpl(dbDataSource, dbCustomStrategy));
        // 配置sql执行策略
        this.setDbCustomStrategy(dbCustomStrategy);
        // 初始化逻辑删除策略
        this.initLogic();
    }

    public JdbcAction(){}


    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(Class<T> entityClass, String condition, Object... params) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder =  TableInfoCache.getSelectSqlBuilderCache(entityClass);
            FullSqlConditionExecutor executorHandler = this.handleLogicWithCondition(sqlBuilder.getAlias(),
                    condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
            List<T> result = selectBySql(entityClass,
                    sqlBuilder.createTargetSql() + executorHandler.execute(), params);
            this.injectOtherResult(entityClass, sqlBuilder, result);
            return result;
        } catch (Exception e) {
            this.throwsException(e);
            return new ArrayList<>();
        }
    }

    @Override
    public <T> List<T> selectListBySql(Class<T> entityClass, String sql, Object... params) {
        try {
            return this.selectBySql(entityClass, sql, params);
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
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass);
            FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(condition);

            // 封装结果
            String selectSql = sqlBuilder.createTargetSql() + executor.execute();
            this.buildPageResult(entityClass, selectSql, dbPageRows, params);

            // 注入一对一，一对多
            this.injectOtherResult(entityClass, sqlBuilder, dbPageRows.getData());
        }catch (Exception e) {
            this.throwsException(e);
        }
        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectByKey(Class<T> entityClass, Serializable key) {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass);
        String condition = sqlBuilder.createKeyCondition(key);
        return selectOne(entityClass, condition, key);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys) {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass);
        String condition = sqlBuilder.createKeysCondition(keys);
        return selectList(entityClass, condition, keys.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOne(Class<T> entityClass, String condition, Object... params) {
        HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(entityClass);
        try {
            FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(condition);
            String selectSql = sqlBuilder.createTargetSql() + executor.execute();
            T result = selectOneBySql(entityClass, selectSql, params);
            this.injectOtherResult(entityClass, sqlBuilder, result);
            return result;
        }catch (Exception e) {
            this.throwsException(e);
            return null;
        }
    }

    @Override
    public <T> T selectOneBySql(Class<T> entityClass, String sql, Object... params) {
        try {
            return this.selectOneSql(entityClass, sql, params);
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
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPage(ConditionWrapper<T> wrapper) {
        if(!wrapper.hasPageParams()) {
            ExThrowsUtil.toCustom("Missing paging parameter：pageIndex：%s, pageSize：%s", wrapper.getPageIndex(), wrapper.getPageSize());
        }
        DbPageRows<T> dbPageRows = new DbPageRows<>(wrapper.getPageIndex(), wrapper.getPageSize());
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass());
            String selectSql = sqlBuilder.selectExecuteSqlBuilder(wrapper);
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
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass());
            String selectSql = sqlBuilder.selectExecuteSqlBuilder(wrapper);
            List<T> result = selectBySql(wrapper.getEntityClass(), selectSql, wrapper.getParamValues().toArray());
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
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass());
            String selectSql = sqlBuilder.selectExecuteSqlBuilder(wrapper);
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
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass());
            String selectSql = sqlBuilder.selectExecuteSqlBuilder(wrapper);
            return (long) selectObjBySql(String.format("select count(0) from (\n\t%s\t\n) xxx ", selectSql),
                    wrapper.getParamValues().toArray());
        }catch (Exception e) {
            this.throwsException(e);
            return 0L;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> Object selectObj(ConditionWrapper<T> wrapper) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass());
            String selectSql = sqlBuilder.selectExecuteSqlBuilder(wrapper);
            return selectObjBySql(selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            this.throwsException(e);
            return null;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass());
            String selectSql = sqlBuilder.selectExecuteSqlBuilder(wrapper);
            return selectObjsBySql(selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            this.throwsException(e);
            return new ArrayList<>();
        }

    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> Map<String, Object> selectMap(ConditionWrapper<T> wrapper) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass());
            String selectSql = sqlBuilder.selectExecuteSqlBuilder(wrapper);
            return selectMapBySql(selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            this.throwsException(e);
            return new HashMap<>();
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass());
            String selectSql = sqlBuilder.selectExecuteSqlBuilder(wrapper);
            return selectMapsBySql(selectSql, wrapper.getParamValues().toArray());
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
            HandleSelectSqlBuilder<T> sqlBuilder = TableInfoCache.getSelectSqlBuilderCache(wrapper.getEntityClass());
            String selectSql = sqlBuilder.selectExecuteSqlBuilder(wrapper);
            Object[] params = wrapper.getParamValues().toArray();
            count = (long) selectObjBySql(String.format("select count(0) from (%s) xxx ", selectSql), params);
            if (count > 0) {
                selectSql = String.format("%s \nlimit %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
                dataList = selectMapsBySql(selectSql, params);
            }
        }catch (Exception e) {
            this.throwsException(e);
        }
        return dbPageRows.setTotal(count).setData(dataList);
    }


    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByKey(Class<T> entityClass, Object key) {
        HandleDeleteSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(entityClass);
        String deleteSql = sqlBuilder.createTargetSql();
        int i = 0;
        try {
            i = executeSql(deleteSql, key);
            if (i > 0 && sqlBuilder.checkLogicFieldIsExist()) {
                sqlBuilder.handleLogicDelAfter(entityClass, deleteSql, sqlBuilder, key);
            }
        } catch (Exception e) {
            this.throwsException(e);
        }
        return i;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteBatchKeys(Class<T> entityClass, Collection<?> keys) {
        HandleDeleteSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(entityClass);
        sqlBuilder.setKeys(keys);
        String deleteSql = sqlBuilder.createTargetSql();
        int i = 0;
        try {
            i = executeSql(deleteSql, sqlBuilder.getSqlParams());
            if (i > 0 && sqlBuilder.checkLogicFieldIsExist()) {
                sqlBuilder.handleLogicDelAfter(entityClass, deleteSql, sqlBuilder.getSqlParams());
            }
        } catch (Exception e) {
            this.throwsException(e);
        }
        return i;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByCondition(Class<T> entityClass, String condition, Object... params) {
        HandleDeleteSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(entityClass);
        sqlBuilder.setSqlParams(Arrays.asList(params));
        sqlBuilder.setDeleteCondition(condition);
        String deleteSql = sqlBuilder.createTargetSql();
        int i = 0;
        try {
            i = executeSql(deleteSql, params);
            if (i > 0 && sqlBuilder.checkLogicFieldIsExist()) {
                sqlBuilder.handleLogicDelAfter(entityClass, deleteSql, params);
            }
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
        HandleInsertSqlBuilder<T> sqlBuilder = TableInfoCache.getInsertSqlBuilderCache((Class<T>) entity.getClass());
        sqlBuilder.setEntityList(Collections.singletonList(entity));
        String insertSql = sqlBuilder.createTargetSql();
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        try {
            i = this.executeInsert(insertSql,
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
        HandleInsertSqlBuilder<T> sqlBuilder = TableInfoCache.getInsertSqlBuilderCache((Class<T>) ts.get(0).getClass());
        sqlBuilder.setEntityList(ts);
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        int res = 0;
        try {
            String insertSql = sqlBuilder.createTargetSql();
            executeInsert(insertSql, ts, keyParserModel.getField(), sqlBuilder.getSqlParams());
        } catch (Exception e) {
            this.throwsException(e);
        }
        return res;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByKey(T entity) {
        HandleUpdateSqlBuilder<T> sqlBuilder = TableInfoCache.getUpdateSqlBuilderCache((Class<T>) entity.getClass());
        sqlBuilder.setEntity(entity);
        String updateSql = sqlBuilder.createTargetSql();
        try {
            return executeSql(updateSql, sqlBuilder.getSqlParams());
        }catch (Exception e) {
            this.throwsException(e);
            return 0;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateColumnByKey(T entity, Consumer<List<SFunction<T, ?>>> updateColumns) {
        HandleUpdateSqlBuilder<T> sqlBuilder = TableInfoCache.getUpdateSqlBuilderCache((Class<T>) entity.getClass());
        List<SFunction<T, ?>> updateColumnList = new ArrayList<>();
        updateColumns.accept(updateColumnList);
        if(!updateColumnList.isEmpty()) {
            int columns = updateColumnList.size();
            SFunction<T, ?>[] updateColumnArrays = (SFunction<T, ?>[]) Array.newInstance(SFunction.class, columns);
            for (int i = 0; i < columns; i++) {
                updateColumnArrays[i] = updateColumnList.get(i);
            }
            sqlBuilder.setUpdateFuncColumns(updateColumnArrays);
        }
        String updateSql = sqlBuilder.createTargetSql();
        try {
            return executeSql(updateSql, sqlBuilder.getSqlParams());
        } catch (Exception e) {
            this.throwsException(e);
            return 0;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateSelective(T entity, ConditionWrapper<T> wrapper) {
        HandleUpdateSqlBuilder<T> sqlBuilder = TableInfoCache.getUpdateSqlBuilderCache((Class<T>) entity.getClass());
        sqlBuilder.setCondition(wrapper.getFinalConditional());
        sqlBuilder.setConditionVals(wrapper.getParamValues());
        String updateSql = sqlBuilder.createTargetSql();
        try {
            return executeSql(updateSql, sqlBuilder.getSqlParams());
        }catch (Exception e) {
            this.throwsException(e);
            return 0;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByCondition(T entity, String condition, Object... params) {
        if (JudgeUtil.isEmpty(condition)) {
            ExThrowsUtil.toNull("修改条件不能为空");
        }
        HandleUpdateSqlBuilder<T> sqlBuilder = TableInfoCache.getUpdateSqlBuilderCache((Class<T>) entity.getClass());
        sqlBuilder.setCondition(condition);
        sqlBuilder.setConditionVals(Arrays.stream(params).collect(Collectors.toList()));
        String updateSql = sqlBuilder.createTargetSql();
        try {
            return executeSql(updateSql, sqlBuilder.getSqlParams());
        }catch (Exception e) {
            this.throwsException(e);
            return 0;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateSelective(AbstractUpdateSet<T> updateSet) {
        TableParseModel<T> tableSqlBuilder = TableInfoCache.getTableModel(updateSet.thisEntityClass());
        UpdateSetWrapper<T> updateSetWrapper = updateSet.getUpdateSetWrapper();
        ConditionWrapper<T> conditionWrapper = updateSet.getConditionWrapper();
        String table = tableSqlBuilder.getTable();
        String alias = tableSqlBuilder.getAlias();
        String finalConditional = conditionWrapper.getFinalConditional();

        try {
            // 条件拼接
            FullSqlConditionExecutor conditionExecutor = handleLogicWithCondition(alias,
                    finalConditional, getLogicDeleteQuerySql(), table);

            // sql set设置器
            String sqlSetter = updateSetWrapper.getSqlSetter().toString();
            String updateSql = DbUtil.updateSql(table, alias, sqlSetter, conditionExecutor.execute());
            
            List<Object> sqlParams = updateSetWrapper.getSetParams();
            CustomUtil.addParams(sqlParams, conditionWrapper.getParamValues());
            return executeSql(updateSql, sqlParams.toArray());
        }catch (Exception e) {
            this.throwsException(e);
            return 0;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int save(T entity) {
        EmptySqlBuilder<T> sqlBuilder = TableInfoCache.getEmptySqlBuilder((Class<T>) entity.getClass());
        sqlBuilder.setEntity(entity);
        return Objects.nonNull(sqlBuilder.primaryKeyVal()) ? updateByKey(entity) : insert(entity);
    }

    @Override
    public int executeSql(String sql, Object... params) {
        try {
            return this.executeAnySql(sql, params);
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
            String exitsTableSql = tableSqlBuilder.exitsTableSql(arr[i]);
            try {
                if(!hasTableInfo(exitsTableSql)) {
                    String createTableSql = tableSqlBuilder.createTableSql();
                    execTable(createTableSql);
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
            execTable(dropTableSql);
            logger.warn("drop table '{}' completed\n", tableSqlBuilder.getTable());
        }
    }


}
