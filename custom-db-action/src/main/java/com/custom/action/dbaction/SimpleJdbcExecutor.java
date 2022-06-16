package com.custom.action.dbaction;

import com.custom.action.interfaces.FullSqlExecutorHandler;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.action.util.DbUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.CustomJdbcExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/16 13:18
 * @Desc 简单的jdbc执行器
 */
@SuppressWarnings("unchecked")
public class SimpleJdbcExecutor {

    private CustomJdbcExecutor jdbcExecutor;

    public void setJdbcExecutor(CustomJdbcExecutor jdbcExecutor) {
        this.jdbcExecutor = jdbcExecutor;
    }

    public CustomJdbcExecutor getJdbcExecutor() {
        return jdbcExecutor;
    }

    /**
     * 查询数组
     */
    public <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception {
        return jdbcExecutor.selectArray(t, sql, params);
    }


    /**
     * 纯sql查询集合
     */
    public <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        return jdbcExecutor.selectList(t, true, sql, params);
    }

    /**
     * 纯sql查询单条记录
     */
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        List<T> queryList = jdbcExecutor.selectList(t, true, sql, params);
        int size = queryList.size();
        if (size == 0) {
            return null;
        } else if (size > 1) {
            throw new CustomCheckException(String.format("One was queried, but more were found:(%s) ", size));
        }
        return queryList.get(SymbolConstant.DEFAULT_ZERO);
    }

    /**
     * 纯sql查询单条记录(映射到Map)
     */
    public Map<String, Object> selectMapBySql(String sql, Object... params) throws Exception {
        List<Map<String, Object>> mapList = jdbcExecutor.selectMapsBySql(sql, false, params);
        return mapList.get(0);
    }

    /**
     * 纯sql查询多条记录(映射到Map)
     */
    public List<Map<String, Object>> selectMapsBySql(String sql, Object... params) throws Exception {
        return jdbcExecutor.selectMapsBySql(sql, true, params);
    }

    /**
     * 纯sql查询单个字段
     */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        if (JudgeUtil.isEmpty(sql)) {
            ExThrowsUtil.toCustom("The Sql to be Not Empty");
        }
        return jdbcExecutor.selectObjBySql(sql, params);
    }

    /**
     * 纯sql查询单个字段集合
     */
    public List<Object> selectObjsBySql(String sql, Object... params) throws Exception {
        if (JudgeUtil.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return jdbcExecutor.selectObjsSql(sql, params);
    }

    /**
     * 纯sql增删改
     */
    public int executeSql(String sql, Object... params) throws Exception {
        if (JudgeUtil.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return jdbcExecutor.executeUpdate(sql, params);
    }

    /**
     * 直接执行查询，属于内部执行
     */
    public <T> List<T> executeQueryNotPrintSql(Class<T> t, String sql, Object... params) throws Exception {
        if (JudgeUtil.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return jdbcExecutor.selectList(t, false, sql, params);
    }

    /**
     * 创建/删除表
     */
    public void execTable(String sql) {
        jdbcExecutor.executeTableSql(sql);
    }

    /**
     * 查询该表是否存在
     */
    public boolean hasTableInfo(String sql) throws Exception {
        return jdbcExecutor.executeExist(sql) > 0L;
    }

    /**
     * 添加
     */
    public <T> int executeInsert(String sql, List<T> obj, String key, Class<?> keyType,  Object... params) throws Exception {
        if (JudgeUtil.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
        return jdbcExecutor.executeInsert(obj, sql, key, keyType, params);
    }

    /**
     * 分页数据整合
     */
    protected  <T> void buildPageResult(Class<T> t, String selectSql, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        List<T> dataList = new ArrayList<>();
        long count = (long) selectObjBySql(String.format("select count(0) from (%s) xxx ", selectSql), params);
        if (count > 0) {
            selectSql = dbPageRows.getPageIndex() == 1 ?
                    String.format("%s \nlimit %s", selectSql, dbPageRows.getPageSize())
                    : String.format("%s \nlimit %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
            dataList = selectBySql(t, selectSql, params);
        }
        dbPageRows.setTotal(count);
        dbPageRows.setData(dataList);
    }

    /**
     * 添加逻辑删除的部分sql
     */
    public FullSqlExecutorHandler handleLogicWithCondition(String alias, final String condition, String logicColumn, String logicSql, String tableName) throws Exception {
        if(!DbUtil.checkLogicFieldIsExist(tableName, logicColumn, getJdbcExecutor())) {
            logicSql = SymbolConstant.EMPTY;
        }
        final String finalLogicSql = logicSql;
        return () -> {
            if (JudgeUtil.isNotEmpty(condition)) {
                return JudgeUtil.isNotEmpty(finalLogicSql) ?
                        String.format("\nwhere %s.%s \n%s ", alias, finalLogicSql, condition.trim())
                        : String.format("\nwhere %s ", DbUtil.trimAppendSqlCondition(condition));
            } else {
                return JudgeUtil.isNotEmpty(finalLogicSql) ?
                        String.format("\nwhere %s.%s ", alias, finalLogicSql)
                        : condition == null ? SymbolConstant.EMPTY : condition.trim();
            }
        };
    }

    /**
     * 从缓存中获取实体解析模板，若缓存中没有，就重新构造模板（查询、删除、创建删除表）
     */
    protected <T> TableSqlBuilder<T> getEntityModelCache(Class<T> t) {
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(t);
        tableModel.setJdbcExecutor(getJdbcExecutor());
        return tableModel;
    }

    /**
     * 从缓存中获取实体解析模板，若缓存中没有，就重新构造模板（批量增加记录）
     */
    protected <T> TableSqlBuilder<T> getUpdateEntityModelCache(List<T> tList) {
        TableSqlBuilder<T> tableModelCache = (TableSqlBuilder<T>) TableInfoCache.getTableModel(tList.get(0).getClass());
        TableSqlBuilder<T> tableModel = tableModelCache.clone();
        tableModel.setEntity(tList.get(0));
        tableModel.setList(tList);
        tableModel.setJdbcExecutor(getJdbcExecutor());
        return tableModel;
    }
}
