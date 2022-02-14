package com.custom.dbaction;

import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.ExecuteMethod;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.annotations.check.CheckExecute;
import com.custom.logic.LogicDeleteFieldSqlHandler;
import com.custom.comm.page.DbPageRows;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/8 14:49
 * @Desc：方法执行处理抽象入口
 **/
public abstract class AbstractSqlBuilder {

    /*--------------------------------------- select ---------------------------------------*/
    public abstract <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, int pageIndex, int pageSize, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, DbPageRows<T> dbPageRows, Object... params) throws Exception;
    public abstract <T> T selectOneByKey(Class<T> t, Object key) throws Exception;
    public abstract <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception;
    public abstract <T> List<T> selectList(Class<T> t, T searchEntity) throws Exception;

    /*--------------------------------------- delete ---------------------------------------*/
    public abstract <T> int deleteByKey(Class<T> t, Object key) throws Exception;
    public abstract <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception;

    /*--------------------------------------- insert ---------------------------------------*/
    public abstract <T> int insert(T t, boolean isGeneratedKey) throws Exception;
    public abstract <T> int insert(List<T> tList, boolean isGeneratedKey) throws Exception;

    /*--------------------------------------- update ---------------------------------------*/
    public abstract <T> int updateByKey(T t, String... updateDbFields) throws Exception;

    /*--------------------------------------- comm ---------------------------------------*/
    public abstract <T> long save(T t) throws Exception;
    public abstract void createTables(Class<?>... arr) throws Exception;
    public abstract void dropTables(Class<?>... arr) throws Exception;
    public abstract <T> int rollbackLogicByKey(Class<T> t, Object key);
    public abstract <T> int rollbackLogicByKeys(Class<T> t, Collection<? extends Serializable> keys);
    public abstract <T> int rollbackLogicByCondition(Class<T> t, String condition, Object... params);



    private SqlExecuteAction sqlExecuteAction;
    private DbCustomStrategy dbCustomStrategy;
    private String logicDeleteUpdateSql = SymbolConst.EMPTY;
    private String logicDeleteQuerySql = SymbolConst.EMPTY;

    /**
     * 初始化逻辑删除的sql
     */
    public void initLogic() {

        if(JudgeUtilsAx.isLogicDeleteOpen(dbCustomStrategy)) {
            if(JudgeUtilsAx.isEmpty(dbCustomStrategy.getNotDeleteLogicValue())
                    || JudgeUtilsAx.isEmpty(dbCustomStrategy.getDeleteLogicValue())) {
                throw new CustomCheckException(ExceptionConst.EX_LOGIC_EMPTY_VALUE);
            }

            this.logicDeleteUpdateSql = String.format("`%s` = %s ",
                    dbCustomStrategy.getDbFieldDeleteLogic(), dbCustomStrategy.getDeleteLogicValue());
            this.logicDeleteQuerySql = String.format("`%s` = %s ",
                    dbCustomStrategy.getDbFieldDeleteLogic(), dbCustomStrategy.getNotDeleteLogicValue());
        }
    }

    /**
     * 获取删除的sql
     */
    public String getLogicDeleteSql(String key, String dbKey, String table, String alias, boolean isMore) {
        String sql;
        String keySql  = String.format(" %s.`%s` %s %s", alias,
                dbKey, isMore ? SymbolConst.IN : SymbolConst.EQUALS, key);

        if (JudgeUtilsAx.isNotEmpty(logicDeleteUpdateSql)) {
            sql = String.format("update %s %s set %s.%s where %s.%s and %s ", table,
                    alias, alias, logicDeleteUpdateSql, alias, logicDeleteQuerySql, keySql);
        }else {
            sql = String.format("delete from %s %s where %s", table,
                    alias, keySql);
        }
        return sql;
    }

    /**
    * 获取修改的逻辑删除字段sql
    */
    public String getLogicUpdateSql(String key) {
        return JudgeUtilsAx.isNotBlank(logicDeleteQuerySql) ? String.format("%s and %s = ?", logicDeleteQuerySql, key) : String.format("%s = ?", key);
    }

    /**
     * 添加逻辑删除的部分sql
     */
    public String checkConditionAndLogicDeleteSql(String alias, String condition, String logicSql) {
        LogicDeleteFieldSqlHandler handler = () -> {
            String sql;
            if (JudgeUtilsAx.isNotEmpty(condition)) {
                sql = JudgeUtilsAx.isNotEmpty(logicSql) ?
                        String.format("where %s.%s %s ", alias, logicSql, condition) : String.format("where 1 = 1 %s ", condition);
            } else {
                sql = JudgeUtilsAx.isNotEmpty(logicSql) ?
                        String.format("where %s.%s ", alias, logicSql) : condition;
            }
            return sql;
        };
        return handler.handleSql();
    }

    /**
    * 纯sql查询集合
    */
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        return sqlExecuteAction.query(t, sql, params);
    }

    /**
    * 纯sql查询单条记录
    */
    @CheckExecute(target = ExecuteMethod.SELECT)
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        List<T> queryList = sqlExecuteAction.query(t, sql, params);
        if (queryList.size() == 0) {
            return null;
        } else if (queryList.size() > 1) {
            throw new CustomCheckException(String.format(ExceptionConst.EX_QUERY_MORE_RESULT, queryList.size()));
        }
        return queryList.get(SymbolConst.DEFAULT_ZERO);
    }

    /**
    * 纯sql查询当个字段
    */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            throw new CustomCheckException(ExceptionConst.EX_SQL_NOT_EMPTY);
        }
        return sqlExecuteAction.selectOneSql(sql, params);
    }

    /**
    * 纯sql增删改
    */
    public int executeSql(String sql, Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            throw new NullPointerException();
        }
        return sqlExecuteAction.executeUpdate(sql, params);
    }

    /**
    * 创建/删除表
    */
    public void execTable(String sql) throws SQLException {
        sqlExecuteAction.executeTableSql(sql);
    }

    /**
    * 添加
    */
    public <T> int executeInsert(String sql, List<T> obj, boolean isGeneratedKey, String key, Class<?> keyType,  Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            throw new NullPointerException();
        }
        return isGeneratedKey ? sqlExecuteAction.executeUpdate(sql, params) :
        sqlExecuteAction.executeInsert(obj, sql, key, keyType, params);
    }


    public SqlExecuteAction getSqlExecuteAction() {
        return sqlExecuteAction;
    }

    public void setSqlExecuteAction(SqlExecuteAction sqlExecuteAction) {
        this.sqlExecuteAction = sqlExecuteAction;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    public void setDbCustomStrategy(DbCustomStrategy dbCustomStrategy) {
        this.dbCustomStrategy = dbCustomStrategy;
    }

    public String getLogicDeleteUpdateSql() {
        return logicDeleteUpdateSql;
    }

    public String getLogicDeleteQuerySql() {
        return logicDeleteQuerySql;
    }
}
