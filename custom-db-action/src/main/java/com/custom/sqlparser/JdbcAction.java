package com.custom.sqlparser;

import com.custom.annotations.check.CheckExecute;
import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.page.DbPageRows;
import com.custom.dbaction.AbstractSqlExecutor;
import com.custom.dbaction.SqlExecuteAction;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.ExecuteMethod;
import com.custom.exceptions.ExThrowsUtil;
import com.custom.wrapper.ConditionWrapper;
import com.custom.wrapper.SFunction;
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
        this.setSqlExecuteAction(new SqlExecuteAction(dbDataSource, dbCustomStrategy));
        // 配置sql执行策略
        this.setDbCustomStrategy(dbCustomStrategy);
        // 初始化逻辑删除策略
        initLogic();
    }

    public JdbcAction(){}


    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        condition = checkConditionAndLogicDeleteSql(sqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
        if(JudgeUtilsAx.isNotEmpty(orderBy)) {
            condition += SymbolConst.ORDER_BY + orderBy;
        }
        return selectBySql(t, sqlBuilder.buildSql() + condition, params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, int pageIndex, int pageSize, Object... params) throws Exception {
        DbPageRows<T> dbPageRows = new DbPageRows<>(pageIndex, pageSize);
        return selectPageRows(t, condition, orderBy, dbPageRows, params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        if(dbPageRows == null) {
            dbPageRows = new DbPageRows<>();
        }
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        String finalCondition = checkConditionAndLogicDeleteSql(sqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
        if (JudgeUtilsAx.isNotEmpty(orderBy)) {
            finalCondition += SymbolConst.ORDER_BY + orderBy;
        }
        buildPageResult(t, sqlBuilder.buildSql() + finalCondition, condition, dbPageRows, params);
        return dbPageRows;
    }

    /**
     * 分页数据整合
     */
    private <T> void buildPageResult(Class<T> t, String selectSql, String condition, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        List<T> dataList = new ArrayList<>();
        long count = (long) selectObjBySql(String.format("select count(0) from (%s) xxx ", selectSql), params);
        if (count > 0) {
            selectSql = String.format("%s \nlimit %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
            dataList = selectBySql(t, selectSql, params);
        }
        dbPageRows.setTotal(count);
        if(condition != null) {
            dbPageRows.setCondition(condition);
        }
        dbPageRows.setData(dataList);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOneByKey(Class<T> t, Object key) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        String condition = String.format("and %s = ?", sqlBuilder.getKeyParserModel().getFieldSql());
        condition = checkConditionAndLogicDeleteSql(sqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
        return selectOneBySql(t, sqlBuilder.buildSql() + condition, key);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        StringJoiner symbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        keys.forEach(x -> symbol.add(SymbolConst.QUEST));
        String condition = String.format("and %s in (%s)", sqlBuilder.getKeyParserModel().getFieldSql(), symbol);
        condition = checkConditionAndLogicDeleteSql(sqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
        return selectBySql(t, sqlBuilder.buildSql() + condition, keys.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        condition = checkConditionAndLogicDeleteSql(sqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), sqlBuilder.getTable());
        return selectOneBySql(t, sqlBuilder.buildSql() + condition, params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPageRows(Class<T> t, ConditionWrapper<T> wrapper) throws Exception {
        if(Objects.isNull(wrapper)) {
            ExThrowsUtil.toNull("缺少分页参数");
        }
        if(!wrapper.isHasPageParams()) {
            ExThrowsUtil.toCustom("缺少分页参数：pageIndex：" + wrapper.getPageIndex() + ", pageSize：" + wrapper.getPageSize());
        }
        DbPageRows<T> dbPageRows = new DbPageRows<>(wrapper.getPageIndex(), wrapper.getPageSize());
        String selectSql = getFullSelectSql(t, dbPageRows, wrapper);
        buildPageResult(t, selectSql, null, dbPageRows, wrapper.getParamValues().toArray());
        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(Class<T> t, ConditionWrapper<T> wrapper) throws Exception {
        if(Objects.isNull(wrapper)) {
            return selectBySql(t, getEntityModelCache(t).getSelectSql());
        }
        String selectSql = getFullSelectSql(t, null, wrapper);
        return selectBySql(t, selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    public <T> T selectOneByCondition(ConditionWrapper<T> wrapper) throws Exception {
        if(Objects.isNull(wrapper)) {
            ExThrowsUtil.toCustom("condition cannot be empty");
        }
        String selectSql = getFullSelectSql(wrapper.getEntityClass(), null, wrapper);
        return selectOneBySql(wrapper.getEntityClass(), selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    public <T> long selectCount(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = getFullSelectSql(wrapper.getEntityClass(), null, wrapper);
        return (long) selectObjBySql(String.format("select count(0) from (%s) xxx ", selectSql), wrapper.getParamValues().toArray());
    }

    @Override
    public <T> Object selectObj(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = getFullSelectSql(wrapper.getEntityClass(), null, wrapper);
        return selectObjBySql(selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    public <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = getFullSelectSql(wrapper.getEntityClass(), null, wrapper);
        return selectObjsBySql(selectSql, wrapper.getParamValues().toArray());
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
        int i = executeSql(deleteSql, sqlBuilder.getSqlParams().toArray());
        if(i > 0 && sqlBuilder.checkLogicFieldIsExist()) {
            sqlBuilder.handleLogicDelAfter(t, deleteSql, sqlBuilder.getSqlParams().toArray());
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
    public <T> int insert(T t, boolean isGeneratedKey) throws Exception {
        HandleInsertSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.INSERT);
        String insertSql = sqlBuilder.buildSql();
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        return executeInsert(insertSql, Collections.singletonList(t), isGeneratedKey, keyParserModel.getKey(), keyParserModel.getType(), sqlBuilder.getSqlParams().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(List<T> ts, boolean isGeneratedKey) throws Exception {
        HandleInsertSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(ts, ExecuteMethod.INSERT);
        String insertSql = sqlBuilder.buildSql();
        DbKeyParserModel<T> keyParserModel = sqlBuilder.getKeyParserModel();
        return executeInsert(insertSql, ts, isGeneratedKey, keyParserModel.getKey(), keyParserModel.getType(), sqlBuilder.getSqlParams().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByKey(T t) throws Exception {
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
        String updateSql = sqlBuilder.buildSql();
        return executeSql(updateSql, sqlBuilder.getSqlParams().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByKey(T t, SFunction<T, ?>... updateColumns) throws Exception {
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
        if(updateColumns.length > 0) {
            sqlBuilder.setUpdateFuncColumns(updateColumns);
        }
        String updateSql = sqlBuilder.buildSql();
        return executeSql(updateSql, sqlBuilder.getSqlParams().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByCondition(T t, ConditionWrapper<T> wrapper) throws Exception {
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
        sqlBuilder.setCondition(wrapper.getFinalConditional());
        sqlBuilder.setConditionVals(wrapper.getParamValues());
        String updateSql = sqlBuilder.buildSql();
        return executeSql(updateSql, sqlBuilder.getSqlParams().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByCondition(T t, String condition, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(condition)) {
            ExThrowsUtil.toNull("修改条件不能为空");
        }
        HandleUpdateSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t, ExecuteMethod.UPDATE);
        sqlBuilder.setCondition(condition);
        sqlBuilder.setConditionVals(Arrays.stream(params).collect(Collectors.toList()));
        String updateSql = sqlBuilder.buildSql();
        return executeSql(updateSql, sqlBuilder.getSqlParams().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> long save(T t) throws Exception {
        long update = updateByKey(t);
        if (update == 0) {
            update = insert(t, false);
        }
        return update;
    }

    @Override
    public void createTables(Class<?>... arr) throws Exception {
        TableSqlBuilder<?> tableSqlBuilder;
        for (int i = arr.length - 1; i >= 0; i--) {
            tableSqlBuilder = getEntityModelCache(arr[i]);
            String exitsTableSql = tableSqlBuilder.getExitsTableSql(arr[i]);
            if(hasTableInfo(exitsTableSql)) {
                String createTableSql = tableSqlBuilder.geCreateTableSql();
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
