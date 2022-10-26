package com.custom.action.executor;

import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.ConvertUtil;
import com.custom.jdbc.condition.BaseExecutorModel;
import com.custom.jdbc.condition.SaveExecutorModel;
import com.custom.jdbc.condition.SelectExecutorModel;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.executor.CustomJdbcExecutor;
import com.custom.jdbc.executor.DefaultCustomJdbcExecutor;
import com.custom.jdbc.interfaces.SqlSessionExecutor;
import com.custom.jdbc.session.CustomSqlSession;
import com.custom.jdbc.transaction.DbConnGlobal;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/16 13:18
 * @Desc jdbc条件封装执行
 */
public class JdbcExecutorFactory {

    /**
     * jdbc基础操作对象
     */
    private final DbDataSource dbDataSource;
    private final CustomJdbcExecutor jdbcExecutor;


    /**
     * 创建请求会话
     */
    private CustomSqlSession createSqlSession(BaseExecutorModel paramInfo) {
        SqlSessionExecutor sessionExecutor = (connection) -> new CustomSqlSession(connection, paramInfo);
        Connection connection = DbConnGlobal.getCurrentConnection(dbDataSource);
        return sessionExecutor.createSession(connection);
    }


    public JdbcExecutorFactory(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        this.dbDataSource = dbDataSource;
        this.jdbcExecutor = new DefaultCustomJdbcExecutor(dbCustomStrategy);
    }

    /**
     * 查询数组
     */
    public <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorModel<T> paramInfo = new SelectExecutorModel<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectArrays(sqlSession);
    }


    /**
     * 纯sql查询集合
     */
    public <T> List<T> selectBySql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorModel<T> paramInfo = new SelectExecutorModel<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectList(sqlSession);
    }

    /**
     * 纯sql查询单条记录
     */
    public <T> T selectOneSql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorModel<T> paramInfo = new SelectExecutorModel<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectOne(sqlSession);
    }

    /**
     * 纯sql查询单条记录(映射到Map)
     */
    public Map<String, Object> selectMapBySql(String sql, Object... params) throws Exception {
        SelectExecutorModel<Object> paramInfo = new SelectExecutorModel<>(Object.class, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectOneMap(sqlSession);
    }

    /**
     * 纯sql查询多条记录(映射到Map)
     */
    public List<Map<String, Object>> selectMapsBySql(String sql, Object... params) throws Exception {
        SelectExecutorModel<Object> paramInfo = new SelectExecutorModel<>(Object.class, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectListMap(sqlSession);
    }

    /**
     * 纯sql查询单个字段
     */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        SelectExecutorModel<Object> paramInfo = new SelectExecutorModel<>(Object.class, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectObj(sqlSession);
    }

    /**
     * 纯sql查询单个字段集合
     */
    public List<Object> selectObjsBySql(String sql, Object... params) throws Exception {
        SelectExecutorModel<Object> paramInfo = new SelectExecutorModel<>(Object.class, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectObjs(sqlSession);
    }

    /**
     * 纯sql增删改
     */
    public int executeAnySql(String sql, Object... params) throws Exception {
        SaveExecutorModel<Object> paramInfo = new SaveExecutorModel<>(sql, true, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.executeUpdate(sqlSession);
    }

    /**
     * 直接执行查询，属于内部执行
     */
    public <T> List<T> executeQueryNotPrintSql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorModel<T> paramInfo = new SelectExecutorModel<>(t, sql, false, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectList(sqlSession);
    }

    /**
     * 创建/删除表
     */
    public void execTable(String sql) throws Exception {
        BaseExecutorModel paramInfo = new BaseExecutorModel(sql, false, new Object[]{});
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        jdbcExecutor.execTableInfo(sqlSession);
    }

    /**
     * 查询该表是否存在
     */
    public boolean hasTableInfo(String sql) throws Exception {
        BaseExecutorModel paramInfo = new BaseExecutorModel(sql, false, new Object[]{});
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        Object obj = jdbcExecutor.selectObj(sqlSession);
        return ConvertUtil.conBool(obj);
    }

    /**
     * 添加
     */
    public <T> int executeInsert(String sql, List<T> obj, Field keyField, Object... params) throws Exception {
        SaveExecutorModel<T> paramInfo = new SaveExecutorModel<>(obj, keyField, sql, true, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.executeSave(sqlSession);
    }

}
