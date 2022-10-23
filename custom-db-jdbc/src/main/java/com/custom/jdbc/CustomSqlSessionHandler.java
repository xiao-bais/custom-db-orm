package com.custom.jdbc;

import com.custom.comm.utils.Constants;
import com.custom.jdbc.condition.BaseExecutorModel;
import com.custom.jdbc.session.CustomSqlSession;
import com.custom.jdbc.configuration.DbCustomStrategy;

import java.sql.*;
import java.util.Map;

/**
 * @author Xiao-Bai
 * @date 2022/10/23 19:08
 * @desc
 */
public class CustomSqlSessionHandler<T> {

    private final DbCustomStrategy strategy;
    private final CustomSqlSession<T> sqlSession;

    public CustomSqlSessionHandler(DbCustomStrategy strategy, CustomSqlSession<T> sqlSession) {
        this.strategy = strategy;
        this.sqlSession = sqlSession;
    }

    public PreparedStatement statementQuery() throws Exception {
        Connection connection = sqlSession.getConnection();
        BaseExecutorModel<T> executorModel = sqlSession.getExecutorModel();
        String prepareSql = executorModel.getPrepareSql();
        return connection.prepareStatement(prepareSql);
    }

    /**
     * 查询之前的处理
     */
    public void handleQueryBefore(PreparedStatement statement) throws SQLException {

        BaseExecutorModel<T> executorModel = sqlSession.getExecutorModel();
        Object[] sqlParams = executorModel.getSqlParams();
        boolean sqlPrintSupport = executorModel.isSqlPrintSupport();
        String prepareSql = executorModel.getPrepareSql();

        // 设置参数
        for (int i = 0; i < sqlParams.length; i++) {
            statement.setObject((i + 1), sqlParams[i]);
        }
        // sql打印
        if (strategy.isSqlOutPrinting() && sqlPrintSupport) {
            SqlOutPrintBuilder
                    .build(prepareSql, sqlParams, strategy.isSqlOutPrintExecute())
                    .sqlInfoQueryPrint();
        }
    }

    /**
     * 处理结果集对象映射
     */
    @SuppressWarnings("unchecked")
    public <V> void handleResultMapper(Map<String, V> map, ResultSet resultSet,  ResultSetMetaData metaData) throws SQLException {
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i + 1);
            V object = (V) resultSet.getObject(i + 1);
            map.put(columnName, object);
        }
    }

    /**
     * 关闭资源
     */
    public void closeResources(PreparedStatement statement, ResultSet resultSet) throws SQLException {
        sqlSession.closeResources();
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
    }


}
