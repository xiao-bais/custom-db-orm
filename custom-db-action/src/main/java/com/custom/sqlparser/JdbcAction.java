package com.custom.sqlparser;

import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbaction.SqlExecuteAction;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.page.DbPageRows;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/8 14:20
 * @Desc：方法执行处理入口
 **/
public class JdbcAction extends AbstractSqlBuilder {

    private SqlExecuteAction sqlExecuteAction;

    public JdbcAction(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy){
        this.setSqlExecuteAction(new SqlExecuteAction(dbDataSource, dbCustomStrategy));
        this.sqlExecuteAction = getSqlExecuteAction();
        this.setDbCustomStrategy(dbCustomStrategy);
        initLogic();
    }

    public JdbcAction(){}


    @Override
    public <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception {
        TableSqlBuilder<T> tableSqlBuilder = new TableSqlBuilder<>(t);
        condition = checkConditionAndLogicDeleteSql(tableSqlBuilder.getAlias(), condition, getLogicDeleteQuerySql());
        String selectSql = String.format("%s %s %s", tableSqlBuilder.getSelectSql(), JudgeUtilsAx.isNotEmpty(condition) ? condition : SymbolConst.EMPTY,
                JudgeUtilsAx.isNotEmpty(orderBy) ? orderBy : SymbolConst.EMPTY);
        return sqlExecuteAction.query(t, selectSql, params);
    }

    @Override
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, int pageIndex, int pageSize, Object... params) throws Exception {
        DbPageRows<T> dbPageRows = new DbPageRows<>(pageIndex, pageSize);
        return selectPageRows(t, condition, orderBy, dbPageRows, params);
    }

    @Override
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
        long count = (long) sqlExecuteAction.selectOneSql(String.format("select count(0) from (%s) xxx ", selectSql), params);
        if (count > 0) {
            selectSql = String.format("%s \nlimit %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
            dataList = sqlExecuteAction.query(t, selectSql, params);
        }
        dbPageRows.setTotal(count);
        dbPageRows.setData(dataList);
        return dbPageRows;
    }

    @Override
    public <T> T selectOneByKey(Class<T> t, Object key) throws Exception {
        return null;
    }

    @Override
    public <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        return null;
    }

    @Override
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception {
        return null;
    }

    @Override
    public <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        return null;
    }

    @Override
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        return null;
    }

    @Override
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        return null;
    }

    @Override
    public <T> int deleteByKey(Class<T> t, Object key) throws Exception {
        return 0;
    }

    @Override
    public <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        return 0;
    }

    @Override
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception {
        return 0;
    }

    @Override
    public <T> int insert(T t, boolean isGeneratedKey) throws Exception {
        return 0;
    }

    @Override
    public <T> int insert(List<T> ts, boolean isGeneratedKey) throws Exception {
        return 0;
    }

    @Override
    public <T> int updateByKey(T t, String... updateDbFields) throws Exception {
        return 0;
    }

    @Override
    public <T> long save(T t) throws Exception {
        return 0;
    }

    @Override
    public int executeSql(String sql, Object... params) throws Exception {
        return 0;
    }
}
