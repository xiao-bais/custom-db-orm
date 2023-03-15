package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseDialect;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

/**
 * @author  Xiao-Bai
 * @since  2022/10/27 18:48
 */
public class Mysql5Adapter extends Mysql8Adapter {


    @Override
    public String driverClassName() {
        return DatabaseDialect.MYSQL5.getDriverClassName();
    }


    @Override
    public DatabaseDialect getType() {
        return DatabaseDialect.MYSQL5;
    }

    @Override
    public String hostName() {
        return null;
    }

    public Mysql5Adapter(JdbcSqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }
}
