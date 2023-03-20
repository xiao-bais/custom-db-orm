package com.custom.jdbc.session;

import com.custom.comm.enums.DatabaseDialect;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.StrUtils;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.configuration.CustomConfigHelper;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.jdbc.dbAdapetr.Mysql5Adapter;
import com.custom.jdbc.dbAdapetr.Mysql8Adapter;
import com.custom.jdbc.dbAdapetr.OracleAdapter;
import com.custom.jdbc.dbAdapetr.PostgresqlAdapter;
import com.custom.jdbc.executor.CustomJdbcExecutor;
import com.custom.jdbc.executor.DefaultCustomJdbcExecutor;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import com.custom.jdbc.interfaces.TransactionExecutor;
import com.custom.jdbc.utils.DbConnGlobal;
import com.custom.jdbc.interfaces.CustomSqlSession;

import java.sql.Connection;

/**
 * jdbc会话创建工厂
 * @author  Xiao-Bai
 * @since  2022/6/16 13:18
 */
public class JdbcSqlSessionFactory {

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


    public JdbcSqlSessionFactory(DbDataSource dbDataSource, DbGlobalConfig globalConfig) {
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

            case POSTGRESQL:
                this.databaseAdapter = new PostgresqlAdapter(this);
        }
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

    public CustomJdbcExecutor getJdbcExecutor() {
        return jdbcExecutor;
    }



}
