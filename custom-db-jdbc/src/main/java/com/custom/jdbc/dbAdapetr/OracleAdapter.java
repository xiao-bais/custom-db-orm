package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseType;
import com.custom.comm.utils.Constants;
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
        int lastIndex = url.lastIndexOf("/");
        boolean is = url.indexOf("?") > 0;
        if (is) {
            return url.substring(lastIndex + 1, url.indexOf("?"));
        }
        return url.substring(url.lastIndexOf("/") + Constants.DEFAULT_ONE);
    }

    @Override
    public String driverClassName() {
        return DatabaseType.ORACLE.getDriverClassName();
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.ORACLE;
    }

    public OracleAdapter(DbDataSource dbDataSource) {
        this.dbDataSource = dbDataSource;
    }
}
