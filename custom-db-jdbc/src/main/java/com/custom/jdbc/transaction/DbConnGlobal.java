package com.custom.jdbc.transaction;

import com.custom.comm.utils.Constants;
import com.custom.jdbc.configuretion.DbConnection;
import com.custom.jdbc.configuretion.DbDataSource;
import com.custom.jdbc.CustomConfigHelper;
import com.custom.jdbc.GlobalDataHandler;

import java.sql.Connection;

/**
 * @author Xiao-Bai
 * @date 2022/10/22 0:06
 * @desc
 */
public class DbConnGlobal {

    /**
     * 获取当前数据库连接
     */
    public static Connection getCurrentConnection() {
        CustomConfigHelper configHelper = (CustomConfigHelper) GlobalDataHandler.readGlobalObject(Constants.DATA_CONFIG);
        Connection connection = null;
        if (configHelper != null) {
            DbDataSource dbDataSource = configHelper.getDbDataSource();
            connection = getCurrentConnection(dbDataSource);
        }
        return connection;
    }

    /**
     * 获取数据库连接
     */
    public static Connection getCurrentConnection(DbDataSource dbDataSource) {
        DbConnection dbConnection = new DbConnection(dbDataSource);
        return dbConnection.createConnection();
    }

    public static String getConnKey(DbDataSource dbDataSource) {
        return String.format("%s-%s-%s-%s",
                dbDataSource.getUrl(),
                dbDataSource.getUsername(),
                dbDataSource.getPassword(),
                dbDataSource.getDatabase()
        );
    }

    public static String getDataSourceKey(DbDataSource dbDataSource) {
        return Constants.DATASOURCE +
                "@" + getConnKey(dbDataSource);
    }

    public static String getDataBaseKey(DbDataSource dbDataSource) {
        return Constants.DATA_BASE +
                "@" + getConnKey(dbDataSource);
    }

    public static String getDataConfigKey(DbDataSource dbDataSource) {
        return Constants.DATA_CONFIG +
                "@" + getConnKey(dbDataSource);
    }

    /**
     * 表是否存在
     */
    public static String exitsTableSql(String table, DbDataSource dbDataSource) {
        if (table.contains(Constants.POINT)) {
            table = table.substring(table.lastIndexOf(Constants.POINT));
        }
        return String.format("SELECT COUNT(1) COUNT ROM " +
                        "`information_schema`.`TABLES` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s';",
                table, getDataBaseKey(dbDataSource));
    }


}
