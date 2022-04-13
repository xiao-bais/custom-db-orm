package com.custom.dbaction;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.CustomApplicationUtils;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.ExecuteMethod;
import com.custom.enums.FillStrategy;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.fieldfill.AutoFillColumnHandler;
import com.custom.fieldfill.TableFillObject;
import com.custom.interfaces.LogicDeleteFieldSqlHandler;
import com.custom.comm.page.DbPageRows;
import com.custom.sqlparser.HandleSelectSqlBuilder;
import com.custom.sqlparser.TableInfoCache;
import com.custom.sqlparser.TableSqlBuilder;
import com.custom.wrapper.ConditionWrapper;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/8 14:49
 * @Desc：方法执行处理抽象入口
 **/
@SuppressWarnings("unchecked")
public abstract class AbstractSqlExecutor {

    /*--------------------------------------- select ---------------------------------------*/
    public abstract <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, int pageIndex, int pageSize, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, String orderBy, DbPageRows<T> dbPageRows, Object... params) throws Exception;
    public abstract <T> T selectOneByKey(Class<T> t, Object key) throws Exception;
    public abstract <T> List<T> selectBatchByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPageRows(Class<T> t,  ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> List<T> selectList(Class<T> t, ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> T selectOneByCondition(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> long selectCount(ConditionWrapper<T> wrapper) throws Exception;


    /*--------------------------------------- delete ---------------------------------------*/
    public abstract <T> int deleteByKey(Class<T> t, Object key) throws Exception;
    public abstract <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception;
    public abstract <T> int deleteByCondition(ConditionWrapper<T> wrapper) throws Exception;

    /*--------------------------------------- insert ---------------------------------------*/
    public abstract <T> int insert(T t, boolean isGeneratedKey) throws Exception;
    public abstract <T> int insert(List<T> tList, boolean isGeneratedKey) throws Exception;

    /*--------------------------------------- update ---------------------------------------*/
    public abstract <T> int updateByKey(T t, String... updateDbFields) throws Exception;
    public abstract <T> int updateByCondition(T t, ConditionWrapper<T> wrapper) throws Exception;

    /*--------------------------------------- comm ---------------------------------------*/
    public abstract <T> long save(T t) throws Exception;
    public abstract void createTables(Class<?>... arr) throws Exception;
    public abstract void dropTables(Class<?>... arr) throws Exception;


    private SqlExecuteAction sqlExecuteAction;
    private DbCustomStrategy dbCustomStrategy;
    private String logicField = SymbolConst.EMPTY;
    private String logicDeleteUpdateSql = SymbolConst.EMPTY;
    private String logicDeleteQuerySql = SymbolConst.EMPTY;

    /**
     * 初始化逻辑删除的sql
     */
    public void initLogic() {
        if(JudgeUtilsAx.isLogicDeleteOpen(dbCustomStrategy)) {
            if(JudgeUtilsAx.isEmpty(dbCustomStrategy.getNotDeleteLogicValue()) || JudgeUtilsAx.isEmpty(dbCustomStrategy.getDeleteLogicValue())) {
                throw new CustomCheckException(ExceptionConst.EX_LOGIC_EMPTY_VALUE);
            }
            this.logicField = dbCustomStrategy.getDbFieldDeleteLogic();
            this.logicDeleteUpdateSql = String.format("%s = %s",
                    logicField, dbCustomStrategy.getDeleteLogicValue());
            this.logicDeleteQuerySql = String.format("%s = %s",
                    logicField, dbCustomStrategy.getNotDeleteLogicValue());
        }
    }

    /**
     * 获取根据主键删除的sql
     */
    public String getLogicDeleteKeySql(String key, String dbKey, String table, String alias, boolean isMore) throws Exception {
        String sql;
        String keySql  = String.format("%s.%s%s%s", alias,
                dbKey, isMore ? SymbolConst.IN : SymbolConst.EQUALS, key);

        if (JudgeUtilsAx.isNotEmpty(logicDeleteUpdateSql)) {
            String logicDeleteQuerySql = String.format("%s.%s", alias, this.logicDeleteQuerySql);
            String logicDeleteUpdateSql = String.format("%s.%s", alias, this.logicDeleteUpdateSql);
            if(checkLogicFieldIsExist(table)) {
                sql = String.format("update %s %s set %s where %s and %s", table,
                        alias, logicDeleteUpdateSql, logicDeleteQuerySql, keySql);
            }else {
                sql = String.format("delete from %s %s where %s", table, alias, keySql);
            }
        }else {
            sql = String.format("delete from %s %s where %s", table, alias, keySql);
        }
        return sql;
    }


    /**
     * 由于部分表可能没有逻辑删除字段，所以在每一次执行时，都需检查该表有没有逻辑删除的字段，以保证sql正常执行
     */
    public boolean checkLogicFieldIsExist(String tableName) throws Exception {
        Boolean existsLogic = TableInfoCache.isExistsLogic(tableName);
        if (existsLogic != null) {
             return existsLogic;
        }
        String existSql = String.format("select count(*) count from information_schema.columns where table_name = '%s' and column_name = '%s'", tableName, logicField);
        long count = sqlExecuteAction.executeExist(existSql);
        TableInfoCache.setTableLogic(tableName, count > 0);
        return count > 0;
    }
    /**
     * 添加逻辑删除的部分sql
     */
    public String checkConditionAndLogicDeleteSql(String alias, final String condition, String logicSql, String tableName) throws Exception {
        if(!checkLogicFieldIsExist(tableName)) {
            logicSql = SymbolConst.EMPTY;
        }
        final String finalLogicSql = logicSql;
        LogicDeleteFieldSqlHandler handler = () -> {
            String sql;
            if (JudgeUtilsAx.isNotEmpty(condition)) {
                if (JudgeUtilsAx.isNotEmpty(finalLogicSql)) {
                    sql = String.format("\nwhere %s.%s %s ", alias, finalLogicSql, condition.trim());
                }else {
                    sql = String.format("\nwhere %s ", CustomUtil.trimAppendSqlCondition(condition));
                }
            } else {
                if (JudgeUtilsAx.isNotEmpty(finalLogicSql)) {
                    sql = String.format("\nwhere %s.%s ", alias, finalLogicSql);
                }else {
                    sql = condition == null ? SymbolConst.EMPTY : condition.trim();
                }
            }
            return sql;
        };
        return handler.handleLogic();
    }

    /**
     * 在删除数据时，若是有逻辑删除，则在逻辑删除后，进行固定字段的自动填充
     */
    public <T> void handleLogicDelAfter(Class<?> t, String deleteSql, TableSqlBuilder<T> tableSqlBuilder, Object... params) throws Exception {
        AutoFillColumnHandler fillColumnHandler = CustomApplicationUtils.getBean(AutoFillColumnHandler.class);
        if(Objects.isNull(fillColumnHandler)) {
            return;
        }
        Optional<TableFillObject> first = fillColumnHandler.fillStrategy().stream().filter(x -> x.getEntityClass().equals(t)).findFirst();
        first.ifPresent(op -> {
            String autoUpdateWhereSqlCondition = deleteSql.substring(deleteSql.indexOf(SymbolConst.WHERE)).replace(getLogicDeleteQuerySql(), getLogicDeleteUpdateSql());
            FillStrategy strategy = op.getStrategy();
            if(strategy.equals(FillStrategy.DEFAULT)) {
                return;
            }
            String autoUpdateSql = tableSqlBuilder.buildLogicDelAfterAutoUpdateSql(strategy, autoUpdateWhereSqlCondition, params);
            if(!ObjectUtils.isEmpty(autoUpdateSql)) {
                try {
                    executeUpdateNotPrintSql(autoUpdateSql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    /**
    * 纯sql查询集合
    */
    public <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        return sqlExecuteAction.query(t, sql, params);
    }

    /**
    * 纯sql查询单条记录
    */
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        List<T> queryList = sqlExecuteAction.query(t, sql, params);
        int size = queryList.size();
        if (size == 0) {
            return null;
        } else if (size > 1) {
            throw new CustomCheckException(String.format(ExceptionConst.EX_QUERY_MORE_RESULT, size));
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
     * 直接执行，属于内部执行
     */
    public void executeUpdateNotPrintSql(String sql) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            throw new NullPointerException();
        }
        sqlExecuteAction.executeUpdateNotPrintSql(sql);
    }

    /**
    * 创建/删除表
    */
    public void execTable(String sql) throws SQLException {
        sqlExecuteAction.executeTableSql(sql);
    }

    /**
     * 查询该表是否存在
     */
    public boolean hasTableInfo(String sql) throws Exception {
        return sqlExecuteAction.executeExist(sql) == 0L;
    }

    /**
    * 添加
    */
    public <T> int executeInsert(String sql, List<T> obj, boolean isGeneratedKey, String key, Class<?> keyType,  Object... params) throws Exception {
        if (JudgeUtilsAx.isEmpty(sql)) {
            throw new NullPointerException();
        }
        return isGeneratedKey ? sqlExecuteAction.executeInsert(obj, sql, key, keyType, params) : sqlExecuteAction.executeUpdate(sql, params);
    }


    /**
     * 从缓存中获取实体解析模板，若缓存中没有，就重新构造模板（查询、删除、创建删除表）
     */
    protected <T> TableSqlBuilder<T> getEntityModelCache(Class<T> t) {
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(t);
        tableModel.setSqlExecuteAction(sqlExecuteAction);
        return tableModel;
    }

    /**
     * 从缓存中获取实体解析模板，若缓存中没有，就重新构造模板（增改记录）
     */
    protected <T> TableSqlBuilder<T> getUpdateEntityModelCache(T t) {
        return getUpdateEntityModelCache(Collections.singletonList(t));
    }

    /**
     * 从缓存中获取实体解析模板，若缓存中没有，就重新构造模板（批量增加记录）
     */
    protected <T> TableSqlBuilder<T> getUpdateEntityModelCache(List<T> tList) {
        TableSqlBuilder<T> tableModelCache = (TableSqlBuilder<T>) TableInfoCache.getTableModel(tList.get(0).getClass());
        TableSqlBuilder<T> tableModel = tableModelCache.clone();
        tableModel.setEntity(tList.get(0));
        tableModel.setList(tList);
        tableModel.setSqlExecuteAction(sqlExecuteAction);
        return tableModel;
    }

    /**
     * 获取实体解析模板中的操作对象
     */
    protected <T, R extends AbstractSqlBuilder<T>> R buildSqlOperationTemplate(Class<T> entityClass) {
        return buildSqlOperationTemplate(entityClass, ExecuteMethod.SELECT);
    }

    /**
     * 获取实体解析模板中的操作对象
     */
    protected <T, R extends AbstractSqlBuilder<T>> R buildSqlOperationTemplate(Class<T> entityClass, ExecuteMethod method) {
        TableSqlBuilder<T> tableSqlBuilder = getEntityModelCache(entityClass);
        tableSqlBuilder.buildSqlConstructorModel(method);
        return (R) tableSqlBuilder.getSqlBuilder();
    }



    /**
     * 公共获取查询sql
     */
    protected  <T> String getFullSelectSql(Class<T> t, DbPageRows<T> dbPageRows, ConditionWrapper<T> wrapper) throws Exception {
        HandleSelectSqlBuilder<T> sqlBuilder = buildSqlOperationTemplate(t);
        StringBuilder selectSql = new StringBuilder();
        if(wrapper.getSelectColumns() != null) {
            selectSql.append(sqlBuilder.selectColumns(wrapper.getSelectColumns()));
        }else {
            selectSql.append(sqlBuilder.buildSql());
        }
        String condition = checkConditionAndLogicDeleteSql(sqlBuilder.getAlias(), wrapper.getFinalConditional(), getLogicDeleteQuerySql(), sqlBuilder.getTable());
        if(dbPageRows != null) {
            dbPageRows.setCondition(condition);
        }
        selectSql.append(condition);
        if(JudgeUtilsAx.isNotEmpty(wrapper.getGroupBy())) {
            selectSql.append(SymbolConst.GROUP_BY).append(wrapper.getGroupBy());
        }
        if(JudgeUtilsAx.isNotEmpty(wrapper.getHaving())) {
            selectSql.append(SymbolConst.HAVING).append(wrapper.getHaving());
        }
        if(CustomUtil.isNotBlank(wrapper.getOrderBy().toString())) {
            selectSql.append(SymbolConst.ORDER_BY).append(wrapper.getOrderBy());
        }
        if(!wrapper.getHavingParams().isEmpty()) {
            wrapper.getParamValues().addAll(wrapper.getHavingParams());
        }
        return selectSql.toString();
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

    public String getLogicField() {
        return logicField;
    }


}
