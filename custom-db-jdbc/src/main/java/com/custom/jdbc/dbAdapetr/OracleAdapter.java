package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseType;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.interfaces.DatabaseAdapter;

/**
 * @author Xiao-Bai
 * @date 2022/10/27 18:52
 * @desc
 */
public class OracleAdapter implements DatabaseAdapter {

    private final DbDataSource dbDataSource;

    @Override
    public String databaseName() {
        String url = dbDataSource.getUrl();
        return url.substring(url.lastIndexOf(":"));
    }

    @Override
    public String driverClassName() {
        return DatabaseType.ORACLE.getDriverClassName();
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.ORACLE;
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

    public OracleAdapter(DbDataSource dbDataSource) {
        this.dbDataSource = dbDataSource;
    }
}
