package com.custom.jdbc.utils;

import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.Constants;
import com.custom.jdbc.configuration.CustomConfigHelper;
import com.custom.jdbc.configuration.DbConnection;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.configuration.GlobalDataHandler;

import java.sql.Connection;

/**
 * 全局连接管理
 * @author  Xiao-Bai
 * @since  2022/10/22 0:06
 */
public class DbConnGlobal {

    /**
     * 获取当前数据库连接
     */
    public static Connection getCurrentConnection() {
        CustomConfigHelper configHelper = DbConnGlobal.getConfigHelper();
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
                "-" + dbDataSource.getOrder();
    }

    /**
     * 添加全局数据源配置
     * <br/>在多个数据源的情况下，需要指定dataSource中的order属性，并且order不能存在重复值
     */
    public static void addDataSource(CustomConfigHelper configHelper) {
        DbDataSource dbDataSource = configHelper.getDbDataSource();
        String key = getDataConfigKey(dbDataSource);
        String newDataSourceKey = getDataSourceKey(dbDataSource);

        // 添加全局数据源配置
        CustomConfigHelper configHelperCache = (CustomConfigHelper) GlobalDataHandler.addGlobalHelper(key, configHelper);

        if (configHelperCache != null) {
            DbDataSource dataSourceCache = configHelperCache.getDbDataSource();
            String dataSourceCacheKey = getDataSourceKey(dataSourceCache);

            // 在多个数据源的情况下，需要指定dataSource中的order属性，并且order不能存在重复值
            if (!dataSourceCacheKey.equals(newDataSourceKey)) {
                throw new CustomCheckException("Duplicate 'order': (%d), " +
                        "In the case of multiple data sources, you need to specify the order in the data source, " +
                        "and the order cannot have duplicate values",
                        dbDataSource.getOrder()
                );
            }

        }
    }

    public static DbDataSource getDataSource(int order) {
        CustomConfigHelper configHelper = getConfigHelper(order);
        if (configHelper != null) {
            return configHelper.getDbDataSource();
        }
        return null;
    }

    public static CustomConfigHelper getConfigHelper(int order) {
        String key = Constants.DATA_CONFIG + "-" + order;
        return (CustomConfigHelper) GlobalDataHandler.readGlobalObject(key);
    }

    public static DbDataSource getDataSource() {
        return getDataSource(Constants.DEFAULT_ONE);
    }

    public static CustomConfigHelper getConfigHelper() {
        return getConfigHelper(Constants.DEFAULT_ONE);
    }


}
