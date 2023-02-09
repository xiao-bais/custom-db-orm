package com.custom.jdbc.executor;

import com.custom.comm.enums.DatabaseDialect;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.ConvertUtil;
import com.custom.comm.utils.StrUtils;
import com.custom.jdbc.condition.BaseExecutorBody;
import com.custom.jdbc.condition.SaveExecutorBody;
import com.custom.jdbc.condition.SelectExecutorBody;
import com.custom.jdbc.condition.SelectMapExecutorBody;
import com.custom.jdbc.configuration.CustomConfigHelper;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.dbAdapetr.Mysql5Adapter;
import com.custom.jdbc.dbAdapetr.Mysql8Adapter;
import com.custom.jdbc.dbAdapetr.OracleAdapter;
import com.custom.jdbc.dbAdapetr.SqlServerAdapter;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import com.custom.jdbc.interfaces.TransactionExecutor;
import com.custom.jdbc.session.DefaultSqlSession;
import com.custom.jdbc.utils.DbConnGlobal;
import com.custom.jdbc.interfaces.CustomSqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Xiao-Bai
 * @date 2022/6/16 13:18
 * jdbc条件封装执行
 */
public class JdbcExecutorFactory {

    private final static Logger log = LoggerFactory.getLogger(JdbcExecutorFactory.class);

    /**
     * jdbc基础操作对象
     */
    private final CustomJdbcExecutor jdbcExecutor;
    private final DbDataSource dbDataSource;
    private final DbCustomStrategy dbCustomStrategy;
    private DatabaseAdapter databaseAdapter;

    public DbDataSource getDbDataSource() {
        return dbDataSource;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    public DatabaseAdapter getDatabaseAdapter() {
        return databaseAdapter;
    }

    /**
     * 创建请求会话
     */
    private CustomSqlSession createSqlSession(BaseExecutorBody paramInfo) {
        Connection connection = DbConnGlobal.getCurrentConnection(dbDataSource);
        return new DefaultSqlSession(connection, paramInfo);
    }

    public CustomSqlSession createSqlSession() {
        return createSqlSession(null);
    }


    public JdbcExecutorFactory(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        this.dbDataSource = dbDataSource;
        this.dbCustomStrategy = dbCustomStrategy;
        this.jdbcExecutor = new DefaultCustomJdbcExecutor(dbCustomStrategy);

        if (StrUtils.isBlank(dbDataSource.getDriver())) {
            if (dbDataSource.getDatabaseType() == null) {

                // 在没有填写驱动类的情况下，使用默认的驱动去尝试加载
                try {
                    Class.forName(DatabaseDialect.DEFAULT.getDriverClassName());
                    dbDataSource.setDatabaseType(DatabaseDialect.DEFAULT);
                } catch (ClassNotFoundException e) {
                    throw new CustomCheckException("未指定连接的数据库驱动");
                }
            }

            DatabaseDialect databaseType = dbDataSource.getDatabaseType();
            dbDataSource.setDriver(databaseType.getDriverClassName());

        } else {
            if (dbDataSource.getDatabaseType() == null) {
                DatabaseDialect databaseType = DatabaseDialect.findTypeByDriver(dbDataSource.getDriver());
                dbDataSource.setDatabaseType(databaseType);
            }
        }

        this.createCurrentDbAdapter();
        dbDataSource.setDatabase(databaseAdapter.databaseName());

        CustomConfigHelper configHelper = new CustomConfigHelper(dbDataSource, dbCustomStrategy, databaseAdapter);
        DbConnGlobal.addDataSource(configHelper);

    }


    /**
     * 创建当前数据库的适配对象
     */
    private void createCurrentDbAdapter() {
        DatabaseDialect type = dbDataSource.getDatabaseType();
        if (type == null) {
            throw new NullPointerException();
        }

        switch (type) {

            default:
            case MYSQL8:
                this.databaseAdapter = new Mysql8Adapter(this);
                break;

            case MYSQL5:
                this.databaseAdapter = new Mysql5Adapter(this);
                break;

            case ORACLE:
                this.databaseAdapter = new OracleAdapter(this);
                break;

            case SQL_SERVER:
                this.databaseAdapter = new SqlServerAdapter(this);
        }
    }


    /**
     * 查询数组
     */
    public <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectArrays(sqlSession);
    }


    /**
     * 纯sql查询集合
     */
    public <T> List<T> selectListBySql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectList(sqlSession);
    }

    /**
     * 查询单列的Set集合
     */
    public <T> Set<T> selectSetBySql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectSet(sqlSession);
    }

    /**
     * 纯sql查询单条记录
     */
    public <T> T selectOneSql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectOne(sqlSession);
    }

    /**
     * 纯sql查询单条记录(映射到Map)
     */
    public Map<String, Object> selectMapBySql(String sql, Object... params) throws Exception {
       return selectMapBySql(Object.class, sql, params);
    }

    public <V> Map<String, V> selectMapBySql(Class<V> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<V> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectOneMap(sqlSession);
    }

    /**
     * 纯sql查询多条记录(映射到Map)
     */
    public List<Map<String, Object>> selectMapsBySql(String sql, Object... params) throws Exception {
        SelectExecutorBody<Object> paramInfo = new SelectExecutorBody<>(Object.class, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectListMap(sqlSession);
    }

    /**
     * 纯sql查询单个字段
     */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        return selectObjBySql(true, sql, params);
    }

    public Object selectObjBySql(boolean sqlPrintSupport, String sql, Object... params) throws Exception {
        SelectExecutorBody<Object> paramInfo = new SelectExecutorBody<>(Object.class, sql, sqlPrintSupport, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectObj(sqlSession);
    }

    /**
     * 纯sql查询单个字段集合
     */
    public List<Object> selectObjsBySql(String sql, Object... params) throws Exception {
       return selectObjsBySql(Object.class, true, sql, params);
    }

    public <T> List<T> selectObjsBySql(Class<T> t, boolean sqlPrintSupport, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, sqlPrintSupport, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectObjs(sqlSession);
    }

    /**
     * 查询映射列表，一般用于聚合查询，并仅限于查询两列
     */
    public <K, V> Map<K, V> selectMap(Class<K> kClass, Class<V> vClass, String sql, Object... params) throws Exception {
        SelectMapExecutorBody<K, V> selectMapExecutorModel = new SelectMapExecutorBody<>(sql, true, params, kClass, vClass);
        CustomSqlSession sqlSession = this.createSqlSession(selectMapExecutorModel);
        return jdbcExecutor.selectMap(sqlSession);
    }

    /**
     * 纯sql增删改
     */
    public int executeAnySql(String sql, Object... params) throws Exception {
        SaveExecutorBody<Object> paramInfo = new SaveExecutorBody<>(sql, true, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.executeUpdate(sqlSession);
    }

    /**
     * 直接执行查询，属于内部执行
     */
    public <T> List<T> executeQueryNotPrintSql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, false, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.selectList(sqlSession);
    }

    /**
     * 创建/删除表
     */
    public void execTable(String sql) throws Exception {
        BaseExecutorBody paramInfo = new BaseExecutorBody(sql, false, new Object[]{});
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        jdbcExecutor.execTableInfo(sqlSession);
    }

    /**
     * 查询该表是否存在
     */
    public boolean hasTableInfo(String sql) throws Exception {
        BaseExecutorBody paramInfo = new BaseExecutorBody(sql, false, new Object[]{});
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        Object obj = jdbcExecutor.selectObj(sqlSession);
        return ConvertUtil.conBool(obj);
    }

    /**
     * 添加
     */
    public <T> int executeInsert(String sql, List<T> obj, Field keyField, Object... params) throws Exception {
        SaveExecutorBody<T> paramInfo = new SaveExecutorBody<>(obj, keyField, sql, true, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        return jdbcExecutor.executeSave(sqlSession);
    }


    /**
     * 处理事务
     */
    public void handleTransaction(TransactionExecutor executor) throws Exception {
        CustomSqlSession sqlSession = createSqlSession();
        try {
            sqlSession.checkConnState(dbDataSource);
            sqlSession.openSession();
            executor.doing();
            sqlSession.checkConnState(dbDataSource);
            sqlSession.commit();
            sqlSession.closeSession();
        } catch (Exception e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.closeResources();
        }
    }



}
