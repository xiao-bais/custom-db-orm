package com.custom.action.dbaction;

import com.custom.action.util.DbUtil;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.ConvertUtil;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.condition.SaveSqlParamInfo;
import com.custom.jdbc.condition.SelectSqlParamInfo;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.select.CustomSelectJdbcBasic;
import com.custom.jdbc.update.CustomUpdateJdbcBasic;

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

    /**
     * jdbc基础操作对象
     */
    private CustomSelectJdbcBasic selectJdbc;
    private CustomUpdateJdbcBasic updateJdbc;
    private String database;
    private DbDataSource dbDataSource;

    public void setSelectJdbc(CustomSelectJdbcBasic selectJdbc) {
        this.selectJdbc = selectJdbc;
        this.dbDataSource = selectJdbc.getDbDataSource();
    }

    public void setUpdateJdbc(CustomUpdateJdbcBasic updateJdbc) {
        this.updateJdbc = updateJdbc;
        this.database = updateJdbc.getDataBase();
    }

    /**
     * 查询数组
     */
    public <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectSqlParamInfo<T> paramInfo = new SelectSqlParamInfo<>(t, sql, params);
        return selectJdbc.selectArrays(paramInfo);
    }

    /**
     * 纯sql查询集合
     */
    public <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectSqlParamInfo<T> paramInfo = new SelectSqlParamInfo<>(t, sql, params);
        return selectJdbc.selectList(paramInfo);
    }

    /**
     * 纯sql查询单条记录
     */
    public <T> T selectOneSql(Class<T> t, String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectSqlParamInfo<T> paramInfo = new SelectSqlParamInfo<>(t, sql, params);
        return selectJdbc.selectOne(paramInfo);
    }

    /**
     * 纯sql查询单条记录(映射到Map)
     */
    public Map<String, Object> selectMapBySql(String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectSqlParamInfo<Object> paramInfo = new SelectSqlParamInfo<>(Object.class, sql, params);
        return selectJdbc.selectMap(paramInfo);
    }

    /**
     * 纯sql查询多条记录(映射到Map)
     */
    public List<Map<String, Object>> selectMapsBySql(String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectSqlParamInfo<Object> paramInfo = new SelectSqlParamInfo<>(Object.class, sql, params);
        return selectJdbc.selectMaps(paramInfo);
    }

    /**
     * 纯sql查询单个字段
     */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectSqlParamInfo<Object> paramInfo = new SelectSqlParamInfo<>(Object.class, sql, params);
        return selectJdbc.selectObj(paramInfo);
    }

    /**
     * 纯sql查询单个字段集合
     */
    public List<Object> selectObjsBySql(String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectSqlParamInfo<Object> paramInfo = new SelectSqlParamInfo<>(Object.class, sql, params);
        return selectJdbc.selectObjs(paramInfo);
    }

    /**
     * 纯sql增删改
     */
    public int executeAnySql(String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SaveSqlParamInfo<Object> paramInfo = new SaveSqlParamInfo<>(sql, true, params);
        return updateJdbc.executeUpdate(paramInfo);
    }

    /**
     * 直接执行查询，属于内部执行
     */
    public <T> List<T> executeQueryNotPrintSql(Class<T> t, String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
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
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SaveSqlParamInfo<T> paramInfo = new SaveSqlParamInfo<>(obj, keyField, sql, true, params);
        return updateJdbc.executeSave(paramInfo);
    }


    /**
     * 分页数据整合
     */
    protected <T> void buildPageResult(Class<T> t, String selectSql, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        List<T> dataList = new ArrayList<>();
        long count = (long) selectObjBySql(String.format(DbUtil.SELECT_COUNT_TEMPLATE, selectSql), params);
        if (count > 0) {
            selectSql = dbPageRows.getPageIndex() == 1 ?
                    String.format("%s \nLIMIT %s", selectSql, dbPageRows.getPageSize())
                    : String.format("%s \nLIMIT %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
            dataList = selectBySql(t, selectSql, params);
        }
        dbPageRows.setTotal(count).setData(dataList);
    }

    public String getDatabase() {
        return database;
    }

    public DbDataSource getDbDataSource() {
        return dbDataSource;
    }

}
