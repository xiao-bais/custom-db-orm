package com.custom.action.sqlparser;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.UpdateSetWrapper;
import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.condition.SFunction;
import com.custom.action.util.DbUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
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
    public <T> List<T> selectList(Class<T> t, String condition, Object... params) {
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
            FullSqlConditionExecutor executorHandler = this.handleLogicWithCondition(sqlBuilder.getAlias(), condition,
                    getLogicDeleteQuerySql(), sqlBuilder.getTable());
            return selectBySql(t, sqlBuilder.buildSql() + executorHandler.execute(), params);
        } catch (Exception e) {
            throwsException(e);
            return new ArrayList<>();
        }
    }

    @Override
    public <T> List<T> selectListBySql(Class<T> t, String sql, Object... params) {
        try {
            return this.selectBySql(t, sql, params);
        }catch (Exception e) {
            throwsException(e);
            return new ArrayList<>();
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, DbPageRows<T> dbPageRows, Object... params) {
        if(dbPageRows == null) {
            dbPageRows = new DbPageRows<>();
        }
        try {
            HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
            FullSqlConditionExecutor conditionExecutor = this.handleLogicWithCondition(sqlBuilder.getAlias(),
                    condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
            this.buildPageResult(t, sqlBuilder.buildSql() + conditionExecutor.execute(), dbPageRows, params);
        }catch (Exception e) {
            throwsException(e);
        }
        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectByKey(Class<T> t, Object key) {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        if (JudgeUtil.isEmpty(sqlBuilder.getKeyParserModel())) {
            if (!sqlBuilder.isMergeSuperDbJoinTable()) {
                ExThrowsUtil.toCustom("%s 中未找到 @DbKey注解, " +
                        "可能是因为该类或父类在@DbTable中使用了mergeSuperDbJoinTables = false的原因，" +
                        "当mergeSuperDbJoinTables = false时，解析注解时只会解析本类的属性字段，不会合并父类的属性字段，" +
                        "该属性请按需要自行判定使用！！！", t);
            } else {
                ExThrowsUtil.toCustom("%s 中未找到 @DbKey注解, " +
                        "猜测该类或父类不存在主键字段，或没有标注@DbKey注解来表示主键，", t);
            }
        }
        String condition = String.format("and %s = ?", sqlBuilder.getKeyParserModel().getFieldSql());
        try {
            FullSqlConditionExecutor conditionExecutor = this.handleLogicWithCondition(sqlBuilder.getAlias(),
                    condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
            return selectOneBySql(t, sqlBuilder.buildSql() + conditionExecutor.execute(), key);
        } catch (Exception e) {
            throwsException(e);
            return null;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectBatchKeys(Class<T> t, Collection<? extends Serializable> keys) {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        StringJoiner symbol = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
        keys.forEach(x -> symbol.add(SymbolConstant.QUEST));
        String condition = String.format("and %s in (%s)", sqlBuilder.getKeyParserModel().getFieldSql(), symbol);
        try {
            FullSqlConditionExecutor conditionExecutor = this.handleLogicWithCondition(sqlBuilder.getAlias()
                    , condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
            return selectBySql(t, sqlBuilder.buildSql() + conditionExecutor.execute(), keys.toArray());
        }catch (Exception e) {
            throwsException(e);
            return new ArrayList<>();
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOne(Class<T> t, String condition, Object... params) {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        try {
            FullSqlConditionExecutor conditionExecutor = this.handleLogicWithCondition(sqlBuilder.getAlias()
                    , condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
            return selectOneBySql(t, sqlBuilder.buildSql() + conditionExecutor.execute(), params);
        }catch (Exception e) {
            throwsException(e);
            return null;
        }
    }

    @Override
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) {
        try {
            return this.selectOneSql(t, sql, params);
        }catch (Exception e) {
            throwsException(e);
            return null;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPageRows(ConditionWrapper<T> wrapper) {
        if(!wrapper.hasPageParams()) {
            ExThrowsUtil.toCustom("缺少分页参数：pageIndex：%s, pageSize：%s", wrapper.getPageIndex(), wrapper.getPageSize());
        }
        DbPageRows<T> dbPageRows = new DbPageRows<>(wrapper.getPageIndex(), wrapper.getPageSize());
        try {
            String selectSql = getFullSelectSql(wrapper);
            buildPageResult(wrapper.getEntityClass(), selectSql, dbPageRows, wrapper.getParamValues().toArray());
        }catch (Exception e){
            throwsException(e);
        }
        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(ConditionWrapper<T> wrapper) {
        try {
            String selectSql = getFullSelectSql(wrapper);
            return selectBySql(wrapper.getEntityClass(), selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            throwsException(e);
            return new ArrayList<>();
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOne(ConditionWrapper<T> wrapper) {
        try {
            String selectSql = getFullSelectSql(wrapper);
            return selectOneBySql(wrapper.getEntityClass(), selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            throwsException(e);
            return null;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> long selectCount(ConditionWrapper<T> wrapper) {
        try {
            String selectSql = getFullSelectSql(wrapper);
            return (long) selectObjBySql(String.format("select count(0) from (%s) xxx ", selectSql), wrapper.getParamValues().toArray());
        }catch (Exception e){
            throwsException(e);
            return 0L;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> Object selectObj(ConditionWrapper<T> wrapper) {
        try {
            String selectSql = getFullSelectSql(wrapper);
            return selectObjBySql(selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            throwsException(e);
            return null;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) {
        try {
            String selectSql = getFullSelectSql(wrapper);
            return selectObjsBySql(selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            throwsException(e);
            return new ArrayList<>();
        }

    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> Map<String, Object> selectMap(ConditionWrapper<T> wrapper) {
        try {
            String selectSql = getFullSelectSql(wrapper);
            return selectMapBySql(selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            throwsException(e);
            return new HashMap<>();
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper) {
        try {
            String selectSql = getFullSelectSql(wrapper);
            return selectMapsBySql(selectSql, wrapper.getParamValues().toArray());
        }catch (Exception e) {
            throwsException(e);
            return new ArrayList<>();
        }
    }

    @Override
    public <T> DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper) {
        if(!wrapper.hasPageParams()) {
            ExThrowsUtil.toCustom("缺少分页参数：pageIndex：%s, pageSize：%s", wrapper.getPageIndex(), wrapper.getPageSize());
        }
        DbPageRows<Map<String, Object>> dbPageRows = new DbPageRows<>(wrapper.getPageIndex(), wrapper.getPageSize());
        List<Map<String, Object>> dataList = new ArrayList<>();
        long count = 0;
        try {
            String selectSql = getFullSelectSql(wrapper);
            Object[] params = wrapper.getParamValues().toArray();
            count = (long) selectObjBySql(String.format("select count(0) from (%s) xxx ", selectSql), params);
            if (count > 0) {
                selectSql = String.format("%s \nlimit %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
                dataList = selectMapsBySql(selectSql, params);
            }
        }catch (Exception e) {
            throwsException(e);
        }
        return dbPageRows.setTotal(count).setData(dataList);
    }


    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByKey(Class<T> t, Object key) {
        HandleDeleteSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.DELETE);
        sqlBuilder.setKey(key);
        String deleteSql = sqlBuilder.buildSql();
        int i = 0;
        try {
            i = executeSql(deleteSql, key);
            if (i > 0 && sqlBuilder.checkLogicFieldIsExist()) {
                sqlBuilder.handleLogicDelAfter(t, deleteSql, sqlBuilder, key);
            }
        } catch (Exception e) {
            throwsException(e);
        }
        return i;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteBatchKeys(Class<T> t, Collection<?> keys) {
        HandleDeleteSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.DELETE);
        sqlBuilder.setKeys(keys);
        String deleteSql = sqlBuilder.buildSql();
        int i = 0;
        try {
            i = executeSql(deleteSql, sqlBuilder.getSqlParams());
            if (i > 0 && sqlBuilder.checkLogicFieldIsExist()) {
                sqlBuilder.handleLogicDelAfter(t, deleteSql, sqlBuilder.getSqlParams());
            }
        } catch (Exception e) {
            throwsException(e);
        }
        return i;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) {
        HandleDeleteSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.DELETE);
        sqlBuilder.setSqlParams(Arrays.asList(params));
        sqlBuilder.setDeleteCondition(condition);
        String deleteSql = sqlBuilder.buildSql();
        int i = 0;
        try {
            i = executeSql(deleteSql, params);
            if (i > 0 && sqlBuilder.checkLogicFieldIsExist()) {
                sqlBuilder.handleLogicDelAfter(t, deleteSql, params);
            }
        } catch (Exception e) {
            throwsException(e);
        }
        return i;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByCondition(ConditionWrapper<T> wrapper) {
        return deleteByCondition(wrapper.getEntityClass(), wrapper.getFinalConditional(), wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(T t)  {
        int i = 0;
        HandleInsertSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.INSERT);
        String insertSql = sqlBuilder.buildSql();
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        try {
            i = executeInsert(insertSql, Collections.singletonList(t), keyParserModel.getField(), sqlBuilder.getSqlParams());
        } catch (Exception e) {
            throwsException(e);
        }
        return i;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insertBatch(List<T> ts) {
        HandleInsertSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(ts, ExecuteMethod.INSERT);
        sqlBuilder.setSaveSubSelection(getDbCustomStrategy().getSaveSubSelect());
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        int res = 0;
        String insertSql;
        sqlBuilder.dataInitialize();
        try {
            if (sqlBuilder.isHasSubSelect()) {
                for (int i = 0; i < sqlBuilder.getSubCount(); i++) {
                    insertSql = sqlBuilder.buildSql();
                    res += executeInsert(insertSql, sqlBuilder.getSubList(), keyParserModel.getField(), sqlBuilder.getSqlParams());
                }
            } else {
                insertSql = sqlBuilder.buildSql();
                res = executeInsert(insertSql, ts, keyParserModel.getField(), sqlBuilder.getSqlParams());
            }
        } catch (Exception e) {
            throwsException(e);
        }
        return res;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByKey(T t) {
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
        String updateSql = sqlBuilder.buildSql();
        try {
            return executeSql(updateSql, sqlBuilder.getSqlParams());
        }catch (Exception e) {
            throwsException(e);
            return 0;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateColumnByKey(T t, Consumer<List<SFunction<T, ?>>> updateColumns) {
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
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
        String updateSql = sqlBuilder.buildSql();
        try {
            return executeSql(updateSql, sqlBuilder.getSqlParams());
        } catch (Exception e) {
            throwsException(e);
            return 0;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByCondition(T t, ConditionWrapper<T> wrapper) {
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
        sqlBuilder.setCondition(wrapper.getFinalConditional());
        sqlBuilder.setConditionVals(wrapper.getParamValues());
        String updateSql = sqlBuilder.buildSql();
        try {
            return executeSql(updateSql, sqlBuilder.getSqlParams());
        }catch (Exception e) {
            throwsException(e);
            return 0;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByCondition(T t, String condition, Object... params) {
        if (JudgeUtil.isEmpty(condition)) {
            ExThrowsUtil.toNull("修改条件不能为空");
        }
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
        sqlBuilder.setCondition(condition);
        sqlBuilder.setConditionVals(Arrays.stream(params).collect(Collectors.toList()));
        String updateSql = sqlBuilder.buildSql();
        try {
            return executeSql(updateSql, sqlBuilder.getSqlParams());
        }catch (Exception e) {
            throwsException(e);
            return 0;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateSelective(AbstractUpdateSet<T> updateSet) {
        TableSqlBuilder<T> tableSqlBuilder = TableInfoCache.getTableModel(updateSet.thisEntityClass());
        UpdateSetWrapper<T> updateSetWrapper = updateSet.getUpdateSetWrapper();
        ConditionWrapper<T> conditionWrapper = updateSet.getConditionWrapper();
        String table = tableSqlBuilder.getTable();
        String alias = tableSqlBuilder.getAlias();
        String finalConditional = conditionWrapper.getFinalConditional();

        try {
            FullSqlConditionExecutor conditionExecutor = handleLogicWithCondition(alias,
                    finalConditional, getLogicDeleteQuerySql(), table);
            String sqlSetter = updateSetWrapper.getSqlSetter().toString();
            String updateSql = DbUtil.updateSql(table, alias, sqlSetter, conditionExecutor.execute());
            List<Object> sqlParams = new ArrayList<>(updateSetWrapper.getSetParams());
            sqlParams.addAll(conditionWrapper.getParamValues());
            return executeSql(updateSql, sqlParams.toArray());
        }catch (Exception e) {
            throwsException(e);
            return 0;
        }
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int save(T entity) {
        TableSqlBuilder<T> sqlBuilder = updateTableSqlBuilder(Collections.singletonList(entity));
        return Objects.nonNull(sqlBuilder.primaryKeyVal()) ? updateByKey(entity) : insert(entity);
    }

    @Override
    public int executeSql(String sql, Object... params) {
        try {
            return this.executeAnySql(sql, params);
        }catch (Exception e) {
            throwsException(e);
            return 0;
        }
    }

    @Override
    public void createTables(Class<?>... arr) {
        TableSqlBuilder<?> tableSqlBuilder;
        for (int i = arr.length - 1; i >= 0; i--) {
            tableSqlBuilder = defaultTableSqlBuilder(arr[i]);
            String exitsTableSql = tableSqlBuilder.exitsTableSql(arr[i]);
            try {
                if(!hasTableInfo(exitsTableSql)) {
                    String createTableSql = tableSqlBuilder.createTableSql();
                    execTable(createTableSql);
                    logger.info("createTableSql ->\n " + createTableSql);
                }
            }catch (Exception e) {
                throwsException(e);
            }
        }
    }

    @Override
    public void dropTables(Class<?>... arr) {
        for (int i = arr.length - 1; i >= 0; i--) {
            TableSqlBuilder<?> tableSqlBuilder = defaultTableSqlBuilder(arr[i]);
            String dropTableSql = tableSqlBuilder.dropTableSql();
            execTable(dropTableSql);
            logger.warn("drop table '{}' completed\n", tableSqlBuilder.getTable());
        }
    }

    @Override
    public <T> TableSqlBuilder<T> defaultSqlBuilder(Class<T> t) {
        return this.defaultTableSqlBuilder(t);
    }

    @Override
    public <T> TableSqlBuilder<T> updateSqlBuilder(List<T> tList) {
        return this.updateTableSqlBuilder(tList);
    }
}
