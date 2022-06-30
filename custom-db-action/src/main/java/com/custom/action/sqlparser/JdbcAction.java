package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.interfaces.FullSqlExecutorHandler;
import com.custom.action.wrapper.ConditionWrapper;
import com.custom.action.wrapper.SFunction;
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
import java.util.*;
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
//        this.setJdbcExecutor(new JdbcExecutorImpl(dbDataSource, dbCustomStrategy));
        // 配置sql执行策略
        this.setDbCustomStrategy(dbCustomStrategy);
        // 初始化逻辑删除策略
        initLogic();
    }

    public JdbcAction(){}


    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(Class<T> t, String condition, Object... params) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        FullSqlExecutorHandler fullSqlExecutorHandler = handleLogicWithCondition(sqlBuilder.getAlias(),
                condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
        return selectBySql(t, sqlBuilder.buildSql() + fullSqlExecutorHandler.execute(), params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        if(dbPageRows == null) {
            dbPageRows = new DbPageRows<>();
        }
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        FullSqlExecutorHandler fullSqlExecutorHandler = handleLogicWithCondition(sqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
        buildPageResult(t, sqlBuilder.buildSql() + fullSqlExecutorHandler.execute(), dbPageRows, params);
        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOneByKey(Class<T> t, Object key) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        if (JudgeUtil.isEmpty(sqlBuilder.getKeyParserModel())) {
            if (!sqlBuilder.isMergeSuperDbJoinTable()) {
                ExThrowsUtil.toCustom("%s 中未找到 @DbKey注解, " +
                        "可能是因为该类或父类在@DbTable中使用了mergeSuperDbJoinTables = false的原因，" +
                        "当mergeSuperDbJoinTables = false时，解析注解时只会解析本类的属性字段，不会合并父类的属性字段，" +
                        "该属性请按需要自行判定使用！！！", t);
            }else {
                ExThrowsUtil.toCustom("%s 中未找到 @DbKey注解, " +
                        "猜测该类或父类不存在主键字段，或没有标注@DbKey注解来表示主键，", t);
            }
        }
        String condition = String.format("and %s = ?", sqlBuilder.getKeyParserModel().getFieldSql());
        FullSqlExecutorHandler fullSqlExecutorHandler = handleLogicWithCondition(sqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
        return selectOneBySql(t, sqlBuilder.buildSql() + fullSqlExecutorHandler.execute(), key);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        StringJoiner symbol = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
        keys.forEach(x -> symbol.add(SymbolConstant.QUEST));
        String condition = String.format("and %s in (%s)", sqlBuilder.getKeyParserModel().getFieldSql(), symbol);
        FullSqlExecutorHandler fullSqlExecutorHandler = handleLogicWithCondition(sqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
        return selectBySql(t, sqlBuilder.buildSql() + fullSqlExecutorHandler.execute(), keys.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        FullSqlExecutorHandler fullSqlExecutorHandler = handleLogicWithCondition(sqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
        return selectOneBySql(t, sqlBuilder.buildSql() + fullSqlExecutorHandler.execute(), params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPageRows(ConditionWrapper<T> wrapper) throws Exception {
        if(!wrapper.hasPageParams()) {
            ExThrowsUtil.toCustom("缺少分页参数：pageIndex：%s, pageSize：%s", wrapper.getPageIndex(), wrapper.getPageSize());
        }
        DbPageRows<T> dbPageRows = new DbPageRows<>(wrapper.getPageIndex(), wrapper.getPageSize());
        String selectSql = getFullSelectSql(wrapper);
        buildPageResult(wrapper.getEntityClass(), selectSql, dbPageRows, wrapper.getParamValues().toArray());
        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = getFullSelectSql(wrapper);
        return selectBySql(wrapper.getEntityClass(), selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOneByCondition(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = getFullSelectSql(wrapper);
        return selectOneBySql(wrapper.getEntityClass(), selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> long selectCount(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = getFullSelectSql(wrapper);
        return (long) selectObjBySql(String.format("select count(0) from (%s) xxx ", selectSql), wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> Object selectObj(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = getFullSelectSql(wrapper);
        return selectObjBySql(selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = getFullSelectSql(wrapper);
        return selectObjsBySql(selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> Map<String, Object> selectMap(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = getFullSelectSql(wrapper);
        return selectMapBySql(selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = getFullSelectSql(wrapper);
        return selectMapsBySql(selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    public <T> DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper) throws Exception {
        if(!wrapper.hasPageParams()) {
            ExThrowsUtil.toCustom("缺少分页参数：pageIndex：%s, pageSize：%s", wrapper.getPageIndex(), wrapper.getPageSize());
        }
        DbPageRows<Map<String, Object>> dbPageRows = new DbPageRows<>(wrapper.getPageIndex(), wrapper.getPageSize());
        String selectSql = getFullSelectSql(wrapper);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Object[] params = wrapper.getParamValues().toArray();
        long count = (long) selectObjBySql(String.format("select count(0) from (%s) xxx ", selectSql), params);
        if (count > 0) {
            selectSql = String.format("%s \nlimit %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
            dataList = selectMapsBySql(selectSql, params);
        }
        dbPageRows.setTotal(count);
        dbPageRows.setData(dataList);
        return dbPageRows;
    }


    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByKey(Class<T> t, Object key) throws Exception {
        HandleDeleteSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.DELETE);
        sqlBuilder.setKey(key);
        String deleteSql = sqlBuilder.buildSql();
        int i = executeSql(deleteSql, key);
        if(i > 0 && sqlBuilder.checkLogicFieldIsExist()) {
            sqlBuilder.handleLogicDelAfter(t, deleteSql, sqlBuilder, key);
        }
        return i;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        HandleDeleteSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.DELETE);
        sqlBuilder.setKeys(keys);
        String deleteSql = sqlBuilder.buildSql();
        int i = executeSql(deleteSql, sqlBuilder.getSqlParams());
        if(i > 0 && sqlBuilder.checkLogicFieldIsExist()) {
            sqlBuilder.handleLogicDelAfter(t, deleteSql, sqlBuilder.getSqlParams());
        }
        return i;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception {
        HandleDeleteSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.DELETE);
        sqlBuilder.setSqlParams(Arrays.asList(params));
        sqlBuilder.setDeleteCondition(condition);
        String deleteSql = sqlBuilder.buildSql();
        int i = executeSql(deleteSql, params);
        if(i > 0 && sqlBuilder.checkLogicFieldIsExist()) {
            sqlBuilder.handleLogicDelAfter(t, deleteSql, params);
        }
        return i;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByCondition(ConditionWrapper<T> wrapper) throws Exception {
        return deleteByCondition(wrapper.getEntityClass(), wrapper.getFinalConditional(), wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(T t) throws Exception {
        HandleInsertSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.INSERT);
        String insertSql = sqlBuilder.buildSql();
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        return executeInsert(insertSql, Collections.singletonList(t),  keyParserModel.getField(), sqlBuilder.getSqlParams());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(List<T> ts) throws Exception {
        HandleInsertSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(ts, ExecuteMethod.INSERT);
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        int res = 0;
        String insertSql;
        sqlBuilder.dataInitialize();
        if (sqlBuilder.isHasSubSelect()) {
            for (int i = 0; i < sqlBuilder.getSubCount(); i++) {
                insertSql = sqlBuilder.buildSql();
                res += executeInsert(insertSql, sqlBuilder.getSubList(), keyParserModel.getField(), sqlBuilder.getSqlParams());
            }
        }else {
             insertSql = sqlBuilder.buildSql();
            res = executeInsert(insertSql, ts, keyParserModel.getField(), sqlBuilder.getSqlParams());
        }
        return res;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByKey(T t) throws Exception {
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
        String updateSql = sqlBuilder.buildSql();
        return executeSql(updateSql, sqlBuilder.getSqlParams());
    }

    @SafeVarargs
    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public final <T> int updateByKey(T t, SFunction<T, ?>... updateColumns) throws Exception {
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
        if(updateColumns.length > 0) {
            sqlBuilder.setUpdateFuncColumns(updateColumns);
        }
        String updateSql = sqlBuilder.buildSql();
        return executeSql(updateSql, sqlBuilder.getSqlParams());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByCondition(T t, ConditionWrapper<T> wrapper) throws Exception {
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
        sqlBuilder.setCondition(wrapper.getFinalConditional());
        sqlBuilder.setConditionVals(wrapper.getParamValues());
        String updateSql = sqlBuilder.buildSql();
        return executeSql(updateSql, sqlBuilder.getSqlParams());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByCondition(T t, String condition, Object... params) throws Exception {
        if (JudgeUtil.isEmpty(condition)) {
            ExThrowsUtil.toNull("修改条件不能为空");
        }
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
        sqlBuilder.setCondition(condition);
        sqlBuilder.setConditionVals(Arrays.stream(params).collect(Collectors.toList()));
        String updateSql = sqlBuilder.buildSql();
        return executeSql(updateSql, sqlBuilder.getSqlParams());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> long save(T entity) throws Exception {
        TableSqlBuilder<T> sqlBuilder = getUpdateEntityModelCache(Collections.singletonList(entity));
        return Objects.nonNull(sqlBuilder.getDbKeyVal()) ? updateByKey(entity) : insert(entity);
    }

    @Override
    public void createTables(Class<?>... arr) throws Exception {
        TableSqlBuilder<?> tableSqlBuilder;
        for (int i = arr.length - 1; i >= 0; i--) {
            tableSqlBuilder = getEntityModelCache(arr[i]);
            String exitsTableSql = tableSqlBuilder.getExitsTableSql(arr[i]);
            if(hasTableInfo(exitsTableSql)) {
                String createTableSql = tableSqlBuilder.getCreateTableSql();
                execTable(createTableSql);
                logger.info("createTableSql ->\n " + createTableSql);
            }
        }
    }

    @Override
    public void dropTables(Class<?>... arr) throws Exception {
        for (int i = arr.length - 1; i >= 0; i--) {
            TableSqlBuilder<?> tableSqlBuilder = getEntityModelCache(arr[i]);
            String dropTableSql = tableSqlBuilder.getDropTableSql();
            execTable(dropTableSql);
            logger.warn("drop table '{}' completed\n", tableSqlBuilder.getTable());
        }
    }
}
