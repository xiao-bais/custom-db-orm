package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseType;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.interfaces.DatabaseAdapter;

/**
 * @author Xiao-Bai
 * @date 2022/10/28 0028 17:50
 */
public class SqlServerAdapter implements DatabaseAdapter {

    private final DbDataSource dbDataSource;

    @Override
    public String databaseName() {
        String url = dbDataSource.getUrl();
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

    public SqlServerAdapter(DbDataSource dbDataSource) {
        this.dbDataSource = dbDataSource;
    }
}
