package com.custom.jdbc.dbAdapetr;

import com.custom.comm.enums.DatabaseType;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.Constants;
import com.custom.jdbc.condition.SelectExecutorModel;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.executor.CustomJdbcExecutor;

/**
 * @author Xiao-Bai
 * @date 2022/10/27 18:33
 * @desc
 */
public class Mysql8Adapter extends AbstractDbAdapter {


    // 查询该表所有字段
    private final static String SELECT_COLUMN_SQL = "SELECT COLUMN_NAME FROM `information_schema`.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s'";
    // 查询该表是否存在
    private final static String TABLE_EXISTS_SQL = "SELECT COUNT(1) COUNT FROM `information_schema`.`TABLES` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s'";
    // 查询字段是否存在
    private final static String COLUMN_EXIST_SQL = "SELECT COUNT(1) COUNT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '%S' AND COLUMN_NAME = '%S'";

    @Override
    public String databaseName() {
        String url = getDbDataSource().getUrl();
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

    @Override
    public String pageHandle(String originSql, long pageIndex, long pageSize) {
        return originSql + " LIMIT " + Constants.QUEST + Constants.SEPARATOR_COMMA_2 + Constants.QUEST;
    }

    @Override
    public boolean existTable(String table) {

        Asserts.npe(table);
        String targetSql = String.format(TABLE_EXISTS_SQL, table, this.databaseName());
        SelectExecutorModel<Long> selectExecutorModel = new SelectExecutorModel<>(Long.class, targetSql, false);
        return queryBoolean(selectExecutorModel);
    }


    @Override
    public boolean existColumn(String table, String column) {

        Asserts.npe(table);
        Asserts.npe(column);
        String targetSql = String.format(COLUMN_EXIST_SQL, table, this.databaseName());
        SelectExecutorModel<Long> selectExecutorModel = new SelectExecutorModel<>(Long.class, targetSql, false);
        return queryBoolean(selectExecutorModel);
    }

    public Mysql8Adapter(DbDataSource dbDataSource, CustomJdbcExecutor jdbcExecutor) {
        super(dbDataSource, jdbcExecutor);
    }
}
