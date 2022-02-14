package com.custom.sqlparser;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbaction.SqlExecuteAction;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.ExecuteMethod;
import com.custom.annotations.check.CheckExecute;
import com.custom.comm.page.DbPageRows;
import com.custom.exceptions.CustomCheckException;
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

    private static Logger logger = LoggerFactory.getLogger(JdbcAction.class);

    public JdbcAction(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy){
        this.setSqlExecuteAction(new SqlExecuteAction(dbDataSource, dbCustomStrategy));
        this.setDbCustomStrategy(dbCustomStrategy);
        initLogic();
    }

    public JdbcAction(){}


    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(t);
        condition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), condition, getLogicDeleteQuerySql());
        String selectSql = String.format("%s %s %s", tableSqlBuilder.getSelectSql(), JudgeUtilsAx.isNotEmpty(condition) ? condition : SymbolConst.EMPTY,
                JudgeUtilsAx.isNotEmpty(orderBy) ? orderBy : SymbolConst.EMPTY);
        return selectBySql(t, selectSql, params);
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
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(t);
        condition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), condition, getLogicDeleteQuerySql());
        if (JudgeUtilsAx.isNotEmpty(orderBy)) {
            orderBy = String.format("\norder by %s", orderBy);
        }
        String selectSql = String.format("%s %s %s", tableSqlBuilder.getSelectSql(), JudgeUtilsAx.isNotEmpty(condition) ? condition : SymbolConst.EMPTY,
                JudgeUtilsAx.isNotEmpty(orderBy) ? orderBy : SymbolConst.EMPTY);

        List<T> dataList = new ArrayList<>();
        long count = (long) selectObjBySql(String.format("select count(0) from (%s) xxx ", selectSql), params);
        if (count > 0) {
            selectSql = String.format("%s \nlimit %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
            dataList = selectBySql(t, selectSql, params);
        }
        dbPageRows.setTotal(count);
        dbPageRows.setCondition(condition);
        dbPageRows.setData(dataList);
        return dbPageRows;
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOneByKey(Class<T> t, Object key) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(t);
        String condition = String.format("and %s = ?", tableSqlBuilder.getKeyParserModel().getFieldSql());
        condition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), condition, getLogicDeleteQuerySql());
        String selectSql = String.format("%s %s", tableSqlBuilder.getSelectSql(), condition);
        return selectOneBySql(t, selectSql, key);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(t);
        StringJoiner symbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
        keys.forEach(x -> symbol.add(SymbolConst.QUEST));
        String condition = String.format("and %s in (%s)", tableSqlBuilder.getKeyParserModel().getFieldSql(), symbol.toString());
        condition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), condition, getLogicDeleteQuerySql());
        String selectSql = String.format("%s %s", tableSqlBuilder.getSelectSql(), condition);
        return selectBySql(t, selectSql, keys.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(t);
        condition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), condition, getLogicDeleteQuerySql());
        String selectSql = String.format("%s %s", tableSqlBuilder.getSelectSql(), condition);
        return selectOneBySql(t, selectSql, params);
    }

    @Override
    public <T> List<T> selectList(Class<T> t, T searchEntity) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<T>(t);
        String selectSql = tableSqlBuilder.getSelectSql();
        String conditions = tableSqlBuilder.buildEntityConditions(searchEntity, super.getDbCustomStrategy().getDbFieldDeleteLogic());
        conditions = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), conditions, getLogicDeleteQuerySql());
        return selectBySql(t, selectSql + conditions, tableSqlBuilder.getObjValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByKey(Class<T> t, Object key) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(t, ExecuteMethod.DELETE);
        DbKeyParserModel<T> keyParserModel = tableSqlBuilder.getKeyParserModel();
        String deleteSql = getLogicDeleteSql(SymbolConst.QUEST, keyParserModel.getDbKey(), tableSqlBuilder.getTable(), tableSqlBuilder.getAlias(), false);
        return executeSql(deleteSql, key);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(t, ExecuteMethod.DELETE);
        DbKeyParserModel<T> keyParserModel = tableSqlBuilder.getKeyParserModel();
        StringJoiner delSymbols = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        IntStream.range(0, keys.size()).mapToObj(i -> SymbolConst.QUEST).forEach(delSymbols::add);
        String deleteSql = getLogicDeleteSql(String.format("(%s)", delSymbols), keyParserModel.getDbKey(), tableSqlBuilder.getTable(), tableSqlBuilder.getAlias(), true);
        return executeSql(deleteSql, keys.toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.DELETE)
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(t, ExecuteMethod.DELETE);
        String deleteSql;
        if(JudgeUtilsAx.isNotEmpty(getLogicDeleteUpdateSql())) {
            deleteSql = String.format("update %s %s set %s.%s where %s.%s %s", tableSqlBuilder.getTable(), tableSqlBuilder.getAlias(),
                    tableSqlBuilder.getAlias(), getLogicDeleteUpdateSql(), tableSqlBuilder.getAlias(), getLogicDeleteQuerySql(), condition);
        }else {
            deleteSql = String.format("delete from %s %s where 1 = 1 %s", tableSqlBuilder.getTable(), tableSqlBuilder.getAlias(), condition);
        }
        return executeSql(deleteSql, params);
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(T t, boolean isGeneratedKey) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(t, false);
        String insertSql = tableSqlBuilder.getInsertSql();
        DbKeyParserModel<T> keyParserModel = tableSqlBuilder.getKeyParserModel();
        return executeInsert(insertSql, Collections.singletonList(t), isGeneratedKey, keyParserModel.getKey(), keyParserModel.getType(), tableSqlBuilder.getOneObjValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.INSERT)
    public <T> int insert(List<T> ts, boolean isGeneratedKey) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(ts);
        String insertSql = tableSqlBuilder.getInsertSql();
        DbKeyParserModel<T> keyParserModel = tableSqlBuilder.getKeyParserModel();
        return executeInsert(insertSql, ts, isGeneratedKey, keyParserModel.getKey(), keyParserModel.getType(), tableSqlBuilder.getManyObjValues().toArray());
    }

    @Override
    @CheckExecute(target = ExecuteMethod.UPDATE)
    public <T> int updateByKey(T t, String... updateDbFields) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(t, true);
        DbKeyParserModel<T> keyParserModel = tableSqlBuilder.getKeyParserModel();
        StringJoiner updateFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        List<Object> objectValues = new ArrayList<>();

        if(updateDbFields.length > 0) {
            for (String field : updateDbFields) {
                Optional<DbFieldParserModel<T>> updateFieldOP = tableSqlBuilder.getFieldParserModels().stream().filter(x -> x.getColumn().equals(field)).findFirst();
                updateFieldOP.ifPresent(op -> {
                    updateFieldSql.add(String.format("%s = ?", op.getFieldSql()));
                    objectValues.add(op.getValue());
                });
            }
        }else {
            List<DbFieldParserModel<T>> fieldParserModels = tableSqlBuilder.getFieldParserModels();
            fieldParserModels.forEach(x -> {
                Object value = x.getValue();
                if (value != null) {
                    updateFieldSql.add(String.format("%s = ?", x.getFieldSql()));
                    objectValues.add(value);
                }
            });
        }
        String updateSql = String.format("update %s %s set %s where %s", tableSqlBuilder.getTable(),
                tableSqlBuilder.getAlias(), updateFieldSql.toString(), getLogicUpdateSql(keyParserModel.getFieldSql()));
        objectValues.add(keyParserModel.getValue(t));
        return executeSql(updateSql, objectValues.toArray());
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
            tableSqlBuilder = new TableSqlBuilder<>(arr[i]);
            String exitsTableSql = tableSqlBuilder.getExitsTableSql(arr[i]);
            long count = (long) selectObjBySql(exitsTableSql);
            if(count == 0) {
                String createTableSql = tableSqlBuilder.geCreateTableSql();
                execTable(createTableSql);
                logger.info("createTableSql ->\n " + createTableSql);
            }
        }
    }

    @Override
    public void dropTables(Class<?>... arr) throws Exception {
        for (int i = arr.length - 1; i >= 0; i--) {
            TableSqlBuilder<?> tableSqlBuilder = new TableSqlBuilder<>(arr[i], ExecuteMethod.NONE);
            String dropTableSql = tableSqlBuilder.getDropTableSql();
            execTable(dropTableSql);
            logger.warn("drop table '{}' completed\n", tableSqlBuilder.getTable());
        }
    }

    @Override
    public <T> int rollbackLogicByKey(Class<T> t, Object key) {
        return 0;
    }

    @Override
    public <T> int rollbackLogicByKeys(Class<T> t, Collection<? extends Serializable> keys) {
        return 0;
    }

    @Override
    public <T> int rollbackLogicByCondition(Class<T> t, String condition, Object... params) {
        return 0;
    }
}
