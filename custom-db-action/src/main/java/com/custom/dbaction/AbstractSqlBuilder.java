package com.custom.dbaction;

import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.ExecuteMethod;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.handler.CheckExecute;
import com.custom.handler.logic.LogicDeleteFieldSqlHandler;
import com.custom.page.DbPageRows;

import java.io.Serializable;
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
    public abstract <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception;
    public abstract <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception;
    public abstract Object selectObjBySql(String sql, Object... params) throws Exception;

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
    public abstract int executeSql(String sql, Object... params) throws Exception;




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
     * 添加逻辑删除的部分sql
     */
    public String checkConditionAndLogicDeleteSql(String alias, String condition, String logicSql) {
        LogicDeleteFieldSqlHandler handler = () -> {
            String sql;
            if (JudgeUtilsAx.isNotEmpty(condition)) {
                sql = JudgeUtilsAx.isNotEmpty(logicSql) ?
                        String.format(" \nwhere %s.%s %s ", alias, logicSql, condition) : String.format(" \nwhere 1 = 1 %s ", condition);
            } else {
                sql = JudgeUtilsAx.isNotEmpty(logicSql) ?
                        String.format(" \nwhere %s.%s ", alias, logicSql) : condition;
            }
            return sql;
        };
        return handler.handleSql();
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
