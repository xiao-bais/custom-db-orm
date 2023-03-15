package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseDialect;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

/**
 * @author  Xiao-Bai
 * @since  2022/10/28 0028 17:50
 */
public class SqlServerAdapter extends AbstractDbAdapter {

    @Override
    public String databaseName() {
        String url = getExecutorFactory().getDbDataSource().getUrl();
        String key = "DatabaseName";
        return url.substring(url.lastIndexOf(key) + key.length() + 1);
    }

    @Override
    public String driverClassName() {
        return DatabaseDialect.SQL_SERVER.getDriverClassName();
    }

    @Override
    public DatabaseDialect getType() {
        return DatabaseDialect.SQL_SERVER;
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

    public SqlServerAdapter(JdbcSqlSessionFactory executorFactory) {
        super(executorFactory);
    }
}
