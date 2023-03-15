package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseDialect;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

/**
 * @author  Xiao-Bai
 * @since  2022/10/27 18:52
 * 
 */
public class OracleAdapter extends AbstractDbAdapter {



    @Override
    public String databaseName() {
        String url = getExecutorFactory().getDbDataSource().getUrl();
        return url.substring(url.lastIndexOf(":"));
    }

    @Override
    public String driverClassName() {
        return DatabaseDialect.ORACLE.getDriverClassName();
    }

    @Override
    public DatabaseDialect getType() {
        return DatabaseDialect.ORACLE;
    }

    @Override
    public String hostName() {
        return null;
    }

    @Override
    public String handlePage(String originSql, long pageIndex, long pageSize) {
        return originSql;
    }

    @Override
    public boolean existTable(String table) {
        return false;
    }

    @Override
    public boolean existColumn(String table, String column) {
        return false;
    }

    public OracleAdapter(JdbcSqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }
}
