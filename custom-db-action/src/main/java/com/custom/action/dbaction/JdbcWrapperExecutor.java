package com.custom.action.dbaction;

import com.custom.action.interfaces.FullSqlExecutorHandler;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.action.util.DbUtil;
import com.custom.comm.ConvertUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.select.CustomSelectJdbcBasic;
import com.custom.jdbc.update.CustomUpdateJdbcBasic;
import com.custom.jdbc.condition.SaveSqlParamInfo;
import com.custom.jdbc.condition.SelectSqlParamInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/16 13:18
 * @Desc jdbc条件封装执行
 */
@SuppressWarnings("unchecked")
public class JdbcWrapperExecutor {

    private CustomSelectJdbcBasic selectJdbc;
    private CustomUpdateJdbcBasic updateJdbc;

    public void setSelectJdbc(CustomSelectJdbcBasic selectJdbc) {
        this.selectJdbc = selectJdbc;
    }

    public void setUpdateJdbc(CustomUpdateJdbcBasic updateJdbc) {
        this.updateJdbc = updateJdbc;
    }

    /**
     * 查询数组
     */
    public <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception {
        checkEmptySql(sql);
        SelectSqlParamInfo<T> paramInfo = new SelectSqlParamInfo<>(t, sql, params);
        return selectJdbc.selectArrays(paramInfo);
    }

    /**
     * 纯sql查询集合
     */
    public <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        checkEmptySql(sql);
        SelectSqlParamInfo<T> paramInfo = new SelectSqlParamInfo<>(t, sql, params);
        return selectJdbc.selectList(paramInfo);
    }

    /**
     * 纯sql查询单条记录
     */
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        checkEmptySql(sql);
        SelectSqlParamInfo<T> paramInfo = new SelectSqlParamInfo<>(t, sql, params);
        return selectJdbc.selectOne(paramInfo);
    }

    /**
     * 纯sql查询单条记录(映射到Map)
     */
    public Map<String, Object> selectMapBySql(String sql, Object... params) throws Exception {
        checkEmptySql(sql);
        SelectSqlParamInfo<Object> paramInfo = new SelectSqlParamInfo<>(Object.class, sql, params);
        return selectJdbc.selectMap(paramInfo);
    }

    /**
     * 纯sql查询多条记录(映射到Map)
     */
    public List<Map<String, Object>> selectMapsBySql(String sql, Object... params) throws Exception {
        checkEmptySql(sql);
        SelectSqlParamInfo<Object> paramInfo = new SelectSqlParamInfo<>(Object.class, sql, params);
        return selectJdbc.selectMaps(paramInfo);
    }

    /**
     * 纯sql查询单个字段
     */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        checkEmptySql(sql);
        SelectSqlParamInfo<Object> paramInfo = new SelectSqlParamInfo<>(Object.class, sql, params);
        return selectJdbc.selectObj(paramInfo);
    }

    /**
     * 纯sql查询单个字段集合
     */
    public List<Object> selectObjsBySql(String sql, Object... params) throws Exception {
        checkEmptySql(sql);
        SelectSqlParamInfo<Object> paramInfo = new SelectSqlParamInfo<>(Object.class, sql, params);
        return selectJdbc.selectObjs(paramInfo);
    }

    /**
     * 纯sql增删改
     */
    public int executeSql(String sql, Object... params) throws Exception {
        checkEmptySql(sql);
        SaveSqlParamInfo<Object> paramInfo = new SaveSqlParamInfo<>(sql, true, params);
        return updateJdbc.executeUpdate(paramInfo);
    }

    /**
     * 直接执行查询，属于内部执行
     */
    public <T> List<T> executeQueryNotPrintSql(Class<T> t, String sql, Object... params) throws Exception {
        checkEmptySql(sql);
        SelectSqlParamInfo<T> paramInfo = new SelectSqlParamInfo<>(t, sql, false, params);
        return selectJdbc.selectList(paramInfo);
    }

    /**
     * 创建/删除表
     */
    public void execTable(String sql) {
        updateJdbc.execTableInfo(sql);
    }

    /**
     * 查询该表是否存在
     */
    public boolean hasTableInfo(String sql) throws Exception {
        Object obj = selectJdbc.selectObj(new SelectSqlParamInfo<>(Object.class, sql, false));
        return ConvertUtil.conBool(obj);
    }

    /**
     * 添加
     */
    public <T> int executeInsert(String sql, List<T> obj, Field keyField, Object... params) throws Exception {
        checkEmptySql(sql);
        SaveSqlParamInfo<T> paramInfo = new SaveSqlParamInfo<>(obj, keyField, sql, true, params);
        return updateJdbc.executeSave(paramInfo);
    }


    private void checkEmptySql(String sql) {
        if (JudgeUtil.isEmpty(sql)) {
            ExThrowsUtil.toNull("The Sql to be Not Empty");
        }
    }

    /**
     * 分页数据整合
     */
    protected <T> void buildPageResult(Class<T> t, String selectSql, DbPageRows<T> dbPageRows, Object... params) throws Exception {
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
    public FullSqlExecutorHandler handleLogicWithCondition(String alias, final String condition,
                                  String logicColumn, String logicSql, String tableName) throws Exception {
        if(!DbUtil.checkLogicFieldIsExist(tableName, logicColumn, this.selectJdbc)) {
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
        TableSqlBuilder<T> tableModelCache = TableInfoCache.getTableModel(t);
        TableSqlBuilder<T> tableModel = tableModelCache.clone();
        tableModel.setSelectJdbc(this.selectJdbc);
        tableModel.setUpdateJdbc(this.updateJdbc);
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
        tableModel.setSelectJdbc(this.selectJdbc);
        tableModel.setUpdateJdbc(this.updateJdbc);
        return tableModel;
    }
}
