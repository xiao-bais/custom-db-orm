package com.custom.sqlparser;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbaction.SqlExecuteAction;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbSymbol;
import com.custom.enums.ExecuteMethod;
import com.custom.annotations.check.CheckExecute;
import com.custom.comm.page.DbPageRows;
import com.custom.exceptions.CustomCheckException;
import com.custom.wrapper.ConditionWrapper;
import com.custom.wrapper.ConditionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/8 14:20
 * @Desc：方法执行处理入口
 **/
public class JdbcAction extends AbstractSqlBuilder {

    private static final Logger logger = LoggerFactory.getLogger(JdbcAction.class);

    public JdbcAction(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy){
        this.setSqlExecuteAction(new SqlExecuteAction(dbDataSource, dbCustomStrategy));
        this.setDbCustomStrategy(dbCustomStrategy);
        initLogic();
    }

    public JdbcAction(){}


    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = getEntityModelCache(t);
        condition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), tableSqlBuilder.getTable());
        String selectSql = tableSqlBuilder.getSelectSql();
        if(JudgeUtilsAx.isNotEmpty(orderBy)) {
            condition += SymbolConst.ORDER_BY + orderBy;
        }
        return selectBySql(t, selectSql + condition, params);
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
        TableSqlBuilder<T> tableSqlBuilder = getEntityModelCache(t);
        String selectSql = tableSqlBuilder.getSelectSql();
        String finalCondition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), tableSqlBuilder.getTable());
        if (JudgeUtilsAx.isNotEmpty(orderBy)) {
            finalCondition += SymbolConst.ORDER_BY + orderBy;
        }
        buildPageResult(t, selectSql + finalCondition, condition, dbPageRows, params);
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
        TableSqlBuilder<T> tableSqlBuilder = getEntityModelCache(t);
        String condition = String.format("and %s = ?", tableSqlBuilder.getKeyParserModel().getFieldSql());
        condition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), tableSqlBuilder.getTable());
        return selectOneBySql(t, tableSqlBuilder.getSelectSql() + condition, key);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = getEntityModelCache(t);
        StringJoiner symbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        keys.forEach(x -> symbol.add(SymbolConst.QUEST));
        String condition = String.format("and %s in (%s)", tableSqlBuilder.getKeyParserModel().getFieldSql(), symbol);
        condition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), tableSqlBuilder.getTable());
        return selectBySql(t, tableSqlBuilder.getSelectSql() + condition, keys.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = getEntityModelCache(t);
        condition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), condition, getLogicDeleteQuerySql(), tableSqlBuilder.getTable());
        return selectOneBySql(t, tableSqlBuilder.getSelectSql() + condition, params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> DbPageRows<T> selectPageRows(Class<T> t, ConditionWrapper<T> wrapper) throws Exception {
        DbPageRows<T> dbPageRows = new DbPageRows<>();
        if(wrapper == null) {
            throw new NullPointerException("缺少分页参数");
        }
        dbPageRows.setPageIndex(wrapper.getPageIndex()).setPageSize(wrapper.getPageSize());
        String selectSql = getFullSelectSql(t, dbPageRows, wrapper);
        buildPageResult(t, selectSql, null, dbPageRows, wrapper.getParamValues().toArray());
        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(Class<T> t, ConditionWrapper<T> wrapper) throws Exception {
        if(wrapper == null) {
            return selectBySql(t, getEntityModelCache(t).getSelectSql());
        }
        String selectSql = getFullSelectSql(t, null, wrapper);
        return selectBySql(t, selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    public <T> T selectOneByCondition(ConditionWrapper<T> wrapper) throws Exception {
        if(wrapper == null) {
            throw new CustomCheckException("condition cannot be empty");
        }
        String selectSql = getFullSelectSql(wrapper.getCls(), null, wrapper);
        return selectOneBySql(wrapper.getCls(), selectSql, wrapper.getParamValues().toArray());
    }

    @Override
    public <T> long selectCount(ConditionWrapper<T> wrapper) throws Exception {
        String selectSql = getFullSelectSql(wrapper.getCls(), null, wrapper);
        return (long) selectObjBySql(String.format("select count(0) from (%s) xxx ", selectSql), wrapper.getParamValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByKey(Class<T> t, Object key) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = getEntityModelCache(t);
        DbKeyParserModel<T> keyParserModel = tableSqlBuilder.getKeyParserModel();
        String deleteSql = getLogicDeleteKeySql(SymbolConst.QUEST, keyParserModel.getDbKey(), tableSqlBuilder.getTable(), tableSqlBuilder.getAlias(), false);
        if(!CustomUtil.isKeyAllowType(keyParserModel.getType(), key)) {
            throw new CustomCheckException("Illegal primary key parameter : " + key);
        }
        return executeSql(deleteSql, key);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = getEntityModelCache(t);
        DbKeyParserModel<T> keyParserModel = tableSqlBuilder.getKeyParserModel();
        StringJoiner delSymbols = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        IntStream.range(0, keys.size()).mapToObj(i -> SymbolConst.QUEST).forEach(delSymbols::add);
        String deleteSql = getLogicDeleteKeySql(String.format("(%s)", delSymbols), keyParserModel.getDbKey(), tableSqlBuilder.getTable(), tableSqlBuilder.getAlias(), true);
        return executeSql(deleteSql, keys.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = getEntityModelCache(t);
        String deleteSql;
        if(JudgeUtilsAx.isNotEmpty(getLogicDeleteUpdateSql()) && checkLogicFieldIsExist(tableSqlBuilder.getTable())) {
            deleteSql = String.format("update %s %s set %s.%s where %s.%s %s", tableSqlBuilder.getTable(), tableSqlBuilder.getAlias(),
                    tableSqlBuilder.getAlias(), getLogicDeleteUpdateSql(), tableSqlBuilder.getAlias(), getLogicDeleteQuerySql(), condition);
        }else {
            deleteSql = String.format("delete from %s %s where %s", tableSqlBuilder.getTable(), tableSqlBuilder.getAlias(), CustomUtil.trimSqlCondition(condition));
        }
        return executeSql(deleteSql, params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByCondition(ConditionWrapper<T> wrapper) throws Exception {
        if(JudgeUtilsAx.isEmpty(wrapper) || JudgeUtilsAx.isEmpty(wrapper.getFinalConditional())) {
            throw new CustomCheckException("delete condition cannot be empty");
        }
        return deleteByCondition(wrapper.getCls(), wrapper.getFinalConditional());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(T t, boolean isGeneratedKey) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = getUpdateEntityModelCache(t);
        String insertSql = tableSqlBuilder.getInsertSql();
        DbKeyParserModel<T> keyParserModel = tableSqlBuilder.getKeyParserModel();
        return executeInsert(insertSql, Collections.singletonList(t), isGeneratedKey, keyParserModel.getKey(), keyParserModel.getType(), tableSqlBuilder.getOneObjValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(List<T> ts, boolean isGeneratedKey) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = getUpdateEntityModelCache(ts);
        String insertSql = tableSqlBuilder.getInsertSql();
        DbKeyParserModel<T> keyParserModel = tableSqlBuilder.getKeyParserModel();
        return executeInsert(insertSql, ts, isGeneratedKey, keyParserModel.getKey(), keyParserModel.getType(), tableSqlBuilder.getManyObjValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByKey(T t, String... updateDbFields) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = getUpdateEntityModelCache(t);
        tableSqlBuilder.buildUpdateSql(updateDbFields, getLogicDeleteQuerySql());
        return executeSql(tableSqlBuilder.getUpdateSql().toString(), tableSqlBuilder.getObjValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByCondition(T t, ConditionWrapper<T> wrapper) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = getUpdateEntityModelCache(t);
        String condition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), wrapper.getFinalConditional(), getLogicDeleteQuerySql(), tableSqlBuilder.getTable());
        tableSqlBuilder.buildUpdateField(condition, wrapper.getParamValues());
        return executeSql(tableSqlBuilder.getUpdateSql().toString(), tableSqlBuilder.getObjValues().toArray());
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
            if(existTable(exitsTableSql)) {
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
