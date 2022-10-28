package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseType;
import com.custom.comm.utils.Constants;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.interfaces.DatabaseAdapter;

/**
 * @author Xiao-Bai
 * @date 2022/10/27 18:33
 * @desc
 */
public class Mysql8Adapter implements DatabaseAdapter {

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
        return DatabaseType.MYSQL8.getDriverClassName();
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.MYSQL8;
    }

    @Override
    public String hostName() {
        return null;
    }

    public Mysql8Adapter(DbDataSource dbDataSource) {
        this.dbDataSource = dbDataSource;
    }
}
