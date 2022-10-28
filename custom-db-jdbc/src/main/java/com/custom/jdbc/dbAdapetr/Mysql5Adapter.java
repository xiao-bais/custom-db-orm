package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseType;
import com.custom.jdbc.configuration.DbDataSource;

/**
 * @author Xiao-Bai
 * @date 2022/10/27 18:48
 * @desc
 */
public class Mysql5Adapter extends Mysql8Adapter {

    private final DbDataSource dbDataSource;


    @Override
    public String driverClassName() {
        return DatabaseType.MYSQL5.getDriverClassName();
    }


    @Override
    public DatabaseType getType() {
        return DatabaseType.MYSQL5;
    }

    @Override
    public String hostName() {
        return null;
    }

    public Mysql5Adapter(DbDataSource dbDataSource) {
        super(dbDataSource);
        this.dbDataSource = dbDataSource;
    }
}
