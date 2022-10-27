package com.custom.jdbc.executor;

import com.custom.comm.enums.DatabaseType;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.ConvertUtil;
import com.custom.comm.utils.StrUtils;
import com.custom.jdbc.CustomConfigHelper;
import com.custom.jdbc.condition.BaseExecutorModel;
import com.custom.jdbc.condition.SaveExecutorModel;
import com.custom.jdbc.condition.SelectExecutorModel;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.dbAdapetr.Mysql5Adapter;
import com.custom.jdbc.dbAdapetr.Mysql8Adapter;
import com.custom.jdbc.dbAdapetr.OracleAdapter;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import com.custom.jdbc.interfaces.SqlSessionExecutor;
import com.custom.jdbc.session.CustomSqlSession;
import com.custom.jdbc.transaction.DbConnGlobal;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        if (StrUtils.isBlank(dbDataSource.getDriver())) {

            if (dbDataSource.getDatabaseType() == null) {

                // 在没有填写驱动类的情况下，使用默认的驱动去尝试加载
                try {
                    Class.forName(DatabaseType.DEFAULT.getDriverClassName());
                    dbDataSource.setDatabaseType(DatabaseType.DEFAULT);
                } catch (ClassNotFoundException e) {
                    throw new CustomCheckException("未指定连接的数据库驱动");
                }
            }

            DatabaseType databaseType = dbDataSource.getDatabaseType();
            dbDataSource.setDriver(databaseType.getDriverClassName());

        } else {
            if (dbDataSource.getDatabaseType() == null) {
                DatabaseType databaseType = DatabaseType.findTypeByDriver(dbDataSource.getDriver());
                dbDataSource.setDatabaseType(databaseType);
            }
        }

        DatabaseAdapter databaseAdapter = getDatabaseAdapter();
        dbDataSource.setDatabase(databaseAdapter.databaseName());

        CustomConfigHelper configHelper = new CustomConfigHelper(dbDataSource, dbCustomStrategy, databaseAdapter);
        this.jdbcExecutor = new DefaultCustomJdbcExecutor(dbCustomStrategy);
        DbConnGlobal.addDataSource(configHelper);

    }


    private DatabaseAdapter getDatabaseAdapter() {
        DatabaseType type = dbDataSource.getDatabaseType();
        if (type == null) {
            throw new NullPointerException();
        }
        DatabaseAdapter databaseAdapter;

        switch (type) {

            default:
            case MYSQL8:
                databaseAdapter = new Mysql8Adapter(dbDataSource);
                break;

            case MYSQL5:
                databaseAdapter = new Mysql5Adapter(dbDataSource);
                break;

            case ORACLE:
                databaseAdapter = new OracleAdapter(dbDataSource);
        }
        return databaseAdapter;
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
     * 查询单列的Set集合
     */
    public <T> Set<T> selectSetBySql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorModel<T> paramInfo = new SelectExecutorModel<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectSet(sqlSession);
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
