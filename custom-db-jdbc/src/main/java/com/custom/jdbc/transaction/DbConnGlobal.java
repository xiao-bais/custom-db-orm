package com.custom.jdbc.transaction;

import com.custom.comm.utils.Constants;
import com.custom.configuration.DbConnection;
import com.custom.configuration.DbDataSource;
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

    public static String getDataBaseName() {
        DbConnection.currMap.get()
    }



}
