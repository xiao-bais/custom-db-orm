package com.custom.action.executor;

import com.custom.jdbc.interfaces.SqlSessionExecutor;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.ConvertUtil;
import com.custom.jdbc.session.CustomSqlSession;
import com.custom.jdbc.condition.SaveExecutorModel;
import com.custom.jdbc.condition.SelectExecutorModel;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.executor.CustomJdbcExecutor;
import com.custom.jdbc.select.CustomSelectJdbcBasic;
import com.custom.jdbc.transaction.DbConnGlobal;
import com.custom.jdbc.update.CustomUpdateJdbcBasic;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/16 13:18
 * @Desc jdbc条件封装执行
 */
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

    public Connection createConnection() {
       return DbConnGlobal.getCurrentConnection(dbDataSource);
    }



    /**
     * 查询数组
     */
    public <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectExecutorModel<T> paramInfo = new SelectExecutorModel<>(t, sql, params);
        return selectJdbc.selectArrays(paramInfo);
    }

    /**
     * 纯sql查询集合
     */
    public <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectExecutorModel<T> paramInfo = new SelectExecutorModel<>(t, sql, params);
        return selectJdbc.selectList(paramInfo);
    }

    /**
     * 纯sql查询单条记录
     */
    public <T> T selectOneSql(Class<T> t, String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectExecutorModel<T> paramInfo = new SelectExecutorModel<>(t, sql, params);
        return selectJdbc.selectOne(paramInfo);
    }

    /**
     * 纯sql查询单条记录(映射到Map)
     */
    public Map<String, Object> selectMapBySql(String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectExecutorModel<Object> paramInfo = new SelectExecutorModel<>(Object.class, sql, params);
        return selectJdbc.selectMap(paramInfo);
    }

    /**
     * 纯sql查询多条记录(映射到Map)
     */
    public List<Map<String, Object>> selectMapsBySql(String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectExecutorModel<Object> paramInfo = new SelectExecutorModel<>(Object.class, sql, params);
        return selectJdbc.selectMaps(paramInfo);
    }

    /**
     * 纯sql查询单个字段
     */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectExecutorModel<Object> paramInfo = new SelectExecutorModel<>(Object.class, sql, params);
        return selectJdbc.selectObj(paramInfo);
    }

    /**
     * 纯sql查询单个字段集合
     */
    public List<Object> selectObjsBySql(String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectExecutorModel<Object> paramInfo = new SelectExecutorModel<>(Object.class, sql, params);
        return selectJdbc.selectObjs(paramInfo);
    }

    /**
     * 纯sql增删改
     */
    public int executeAnySql(String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SaveExecutorModel<Object> paramInfo = new SaveExecutorModel<>(sql, true, params);
        return updateJdbc.executeUpdate(paramInfo);
    }

    /**
     * 直接执行查询，属于内部执行
     */
    public <T> List<T> executeQueryNotPrintSql(Class<T> t, String sql, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SelectExecutorModel<T> paramInfo = new SelectExecutorModel<>(t, sql, false, params);
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
        Object obj = selectJdbc.selectObj(new SelectExecutorModel<>(Object.class, sql, false));
        return ConvertUtil.conBool(obj);
    }

    /**
     * 添加
     */
    public <T> int executeInsert(String sql, List<T> obj, Field keyField, Object... params) throws Exception {
        Asserts.notEmpty(sql, "The Sql to be Not Empty");
        SaveExecutorModel<T> paramInfo = new SaveExecutorModel<>(obj, keyField, sql, true, params);
        return updateJdbc.executeSave(paramInfo);
    }




    public String getDatabase() {
        return database;
    }

    public DbDataSource getDbDataSource() {
        return dbDataSource;
    }

}
