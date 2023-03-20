package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseDialect;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

/**
 * @author  Xiao-Bai
 * @since  2022/10/28 0028 17:50
 */
public class PostgresqlAdapter extends Mysql8Adapter {

    @Override
    public String driverClassName() {
        return DatabaseDialect.POSTGRESQL.getDriverClassName();
    }

    @Override
    public DatabaseDialect getType() {
        return DatabaseDialect.POSTGRESQL;
    }

    @Override
    public String hostName() {
        return null;
    }

    @Override
    public String handlePage(String originSql, long pageIndex, long pageSize) {
        if (pageIndex == 1) {
            return String.format("%s \nLIMIT %s", originSql, pageSize);
        }
        return String.format("%s \nLIMIT %s OFFSET %s", originSql, pageSize, (pageIndex - 1) * pageSize);
    }

    public PostgresqlAdapter(JdbcSqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

}
