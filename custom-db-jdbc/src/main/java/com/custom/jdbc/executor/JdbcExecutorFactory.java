package com.custom.jdbc.executor;

import com.custom.comm.enums.DatabaseDialect;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.ConvertUtil;
import com.custom.comm.utils.CustomApp;
import com.custom.comm.utils.ReflectUtil;
import com.custom.comm.utils.StrUtils;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.executebody.SaveExecutorBody;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.executebody.SelectMapExecutorBody;
import com.custom.jdbc.configuration.CustomConfigHelper;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.jdbc.dbAdapetr.Mysql5Adapter;
import com.custom.jdbc.dbAdapetr.Mysql8Adapter;
import com.custom.jdbc.dbAdapetr.OracleAdapter;
import com.custom.jdbc.dbAdapetr.SqlServerAdapter;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import com.custom.jdbc.interfaces.TransactionExecutor;
import com.custom.jdbc.session.CustomSqlSessionHelper;
import com.custom.jdbc.session.DefaultSqlSession;
import com.custom.jdbc.utils.DbConnGlobal;
import com.custom.jdbc.interfaces.CustomSqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * jdbc执行对象创建工厂
 * @author  Xiao-Bai
 * @since  2022/6/16 13:18
 */
public class JdbcExecutorFactory {

    private final static Logger log = LoggerFactory.getLogger(JdbcExecutorFactory.class);

    /**
     * jdbc基础操作对象
     */
    private final CustomJdbcExecutor jdbcExecutor;
    private final DbDataSource dbDataSource;
    private final DbGlobalConfig globalConfig;
    private final DbCustomStrategy dbCustomStrategy;
    private DatabaseAdapter databaseAdapter;

    public DbDataSource getDbDataSource() {
        return dbDataSource;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    public DbGlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public DatabaseAdapter getDatabaseAdapter() {
        return databaseAdapter;
    }

    /**
     * 创建请求会话
     */
    public CustomSqlSession createSqlSession(BaseExecutorBody paramInfo) {
        Connection connection = DbConnGlobal.getCurrentConnection(dbDataSource);
        return new DefaultSqlSession(globalConfig, connection, paramInfo);
    }

    public CustomSqlSession createSqlSession() {
        return createSqlSession(null);
    }


    public JdbcExecutorFactory(DbDataSource dbDataSource, DbGlobalConfig globalConfig) {
        this.dbDataSource = dbDataSource;
        this.globalConfig = globalConfig;
        this.dbCustomStrategy = globalConfig.getStrategy();
        this.jdbcExecutor = new DefaultCustomJdbcExecutor();

        if (StrUtils.isBlank(dbDataSource.getDriver())) {
            if (dbDataSource.getDatabaseType() == null) {

                try {
                    // 在没有填写驱动类的情况下，使用默认的驱动去尝试加载
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

        CustomConfigHelper configHelper = new CustomConfigHelper(dbDataSource, globalConfig, databaseAdapter);
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
        T[] arrays = jdbcExecutor.selectArrays(sqlSession);
        this.queryAfterHandle(t, arrays);
        return arrays;
    }


    /**
     * 纯sql查询集合
     */
    public <T> List<T> selectListBySql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        List<T> list = jdbcExecutor.selectList(sqlSession);
        this.queryAfterHandle(t, list);
        return list;
    }

    /**
     * 查询单列的Set集合
     */
    public <T> Set<T> selectSetBySql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        Set<T> set = jdbcExecutor.selectSet(sqlSession);
        this.queryAfterHandle(t, set);
        return set;
    }

    /**
     * 纯sql查询单条记录
     */
    public <T> T selectOneSql(Class<T> t, String sql, Object... params) throws Exception {
        SelectExecutorBody<T> paramInfo = new SelectExecutorBody<>(t, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        T one = jdbcExecutor.selectOne(sqlSession);
        this.queryAfterHandle(t, one);
        return one;
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
        Map<String, V> objectMap = jdbcExecutor.selectOneMap(sqlSession);
        this.queryAfterHandle(t, objectMap);
        return objectMap;
    }

    /**
     * 纯sql查询多条记录(映射到Map)
     */
    public List<Map<String, Object>> selectMapsBySql(String sql, Object... params) throws Exception {
        SelectExecutorBody<Object> paramInfo = new SelectExecutorBody<>(Object.class, sql, params);
        CustomSqlSession sqlSession = this.createSqlSession(paramInfo);
        List<Map<String, Object>> maps = jdbcExecutor.selectListMap(sqlSession);
        this.queryAfterHandle(Map.class, maps);
        return maps;
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
        Object obj = jdbcExecutor.selectObj(sqlSession);
        this.queryAfterHandle(obj.getClass(), obj);
        return obj;
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
        List<T> objs = jdbcExecutor.selectObjs(sqlSession);
        this.queryAfterHandle(t, objs);
        return objs;
    }

    /**
     * 查询映射列表，一般用于聚合查询，并仅限于查询两列
     */
    public <K, V> Map<K, V> selectMap(Class<K> kClass, Class<V> vClass, String sql, Object... params) throws Exception {
        SelectMapExecutorBody<K, V> selectMapExecutorModel = new SelectMapExecutorBody<>(sql, true, params, kClass, vClass);
        CustomSqlSession sqlSession = this.createSqlSession(selectMapExecutorModel);
        Map<K, V> map = jdbcExecutor.selectMap(sqlSession);
        this.queryAfterHandle(Map.class, map);
        return map;
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


    /**
     * 自定义sql查询后的拦截处理
     * @param <T> 查询结果的返回类型
     * @param t 查询结果的类型
     * @param obj 查询结果
     */
    private <T> void queryAfterHandle(Class<T> t, Object obj) throws Exception {
        CustomSqlQueryAfter queryAfter;

        try {
            queryAfter = CustomApp.getBean(CustomSqlQueryAfter.class);
        } catch (NoSuchBeanDefinitionException e) {
            queryAfter = null;
        }

        if (queryAfter == null) {
            Class<? extends CustomSqlQueryAfter> queryAfterClass = globalConfig.getSqlQueryAfter();
            if (queryAfterClass == null) {
                return;
            }
            queryAfter = ReflectUtil.getInstance(queryAfterClass);
        }
        // 处理查询后的结果
        queryAfter.handle(t, obj);
    }

    public CustomJdbcExecutor getJdbcExecutor() {
        return jdbcExecutor;
    }



}
