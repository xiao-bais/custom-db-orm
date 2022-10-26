package com.custom.jdbc;

import com.custom.jdbc.condition.BaseExecutorModel;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.session.CustomSqlSession;

import java.sql.*;
import java.util.Map;

/**
 * @author Xiao-Bai
 * @date 2022/10/23 19:08
 * @desc
 */
public class CustomSqlSessionHandler {

    private final DbCustomStrategy strategy;
    private final CustomSqlSession sqlSession;

    public CustomSqlSessionHandler(DbCustomStrategy strategy, CustomSqlSession sqlSession) {
        this.strategy = strategy;
        this.sqlSession = sqlSession;
    }

    public PreparedStatement statementPrepareSql() throws Exception {
        Connection connection = sqlSession.getConnection();
        BaseExecutorModel executorModel = sqlSession.getExecutorModel();
        String prepareSql = executorModel.getPrepareSql();
        return connection.prepareStatement(prepareSql);
    }

    /**
     * 查询之前的处理
     */
    public void handleExecuteBefore(PreparedStatement statement) throws SQLException {

        BaseExecutorModel executorModel = sqlSession.getExecutorModel();
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
        if (sqlSession.isAutoCommit()) {
            sqlSession.closeResources();
        }
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
    }


    /**
     * 通用一般增删改
     */
//    public <T> int executeUpdate()



}
