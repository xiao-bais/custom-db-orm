package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseDialect;
import com.custom.comm.utils.AssertUtil;
import com.custom.comm.utils.Constants;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

/**
 * @author  Xiao-Bai
 * @since  2022/10/27 18:33
 * 
 */
public class Mysql8Adapter extends AbstractDbAdapter {


    // 查询该表所有字段
    private final static String SELECT_COLUMN_SQL = "SELECT COLUMN_NAME FROM `information_schema`.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s'";
    // 查询该表是否存在
    private final static String TABLE_EXISTS_SQL = "SELECT COUNT(1) COUNT FROM `information_schema`.`TABLES` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s'";
    // 查询字段是否存在
    private final static String COLUMN_EXIST_SQL = "SELECT COUNT(1) COUNT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s' AND COLUMN_NAME = '%s'";

    @Override
    public String databaseName() {
        String url = getExecutorFactory().getDbDataSource().getUrl();
        int lastIndex = url.lastIndexOf("/");
        boolean is = url.indexOf("?") > 0;
        if (is) {
            return url.substring(lastIndex + 1, url.indexOf("?"));
        }
        return url.substring(url.lastIndexOf("/") + Constants.DEFAULT_ONE);
    }

    @Override
    public String driverClassName() {
        return DatabaseDialect.MYSQL8.getDriverClassName();
    }

    @Override
    public DatabaseDialect getType() {
        return DatabaseDialect.MYSQL8;
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
        return String.format("%s \nLIMIT %s, %s", originSql, (pageIndex - 1) * pageSize, pageSize);
    }

    @Override
    public boolean existTable(String table) {
        AssertUtil.npe(table);
        String targetSql = String.format(TABLE_EXISTS_SQL, table, this.databaseName());
        return queryBoolean(targetSql);
    }


    @Override
    public boolean existColumn(String table, String column) {
        AssertUtil.npe(table);
        AssertUtil.npe(column);
        String targetSql = String.format(COLUMN_EXIST_SQL, this.databaseName(), table, column);
        return queryBoolean(targetSql);
    }

    public Mysql8Adapter(JdbcSqlSessionFactory executorFactory) {
        super(executorFactory);
    }
}
