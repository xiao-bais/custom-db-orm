package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseType;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.executor.CustomJdbcExecutor;

/**
 * @author Xiao-Bai
 * @date 2022/10/28 0028 17:50
 */
public class SqlServerAdapter extends AbstractDbAdapter {

    @Override
    public String databaseName() {
        String url = getDbDataSource().getUrl();
        String key = "DatabaseName";
        return url.substring(url.lastIndexOf(key) + key.length() + 1);
    }

    @Override
    public String driverClassName() {
        return DatabaseType.SQL_SERVER.getDriverClassName();
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.SQL_SERVER;
    }

    @Override
    public String hostName() {
        return null;
    }

    @Override
    public String pageHandle(String originSql, long pageIndex, long pageSize) {
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

    public SqlServerAdapter(DbDataSource dbDataSource, CustomJdbcExecutor jdbcExecutor) {
        super(dbDataSource, jdbcExecutor);
    }
}
