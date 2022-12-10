package com.custom.jdbc.session;

import com.custom.jdbc.sqlprint.SqlOutPrintBuilder;
import com.custom.jdbc.condition.BaseExecutorBody;
import com.custom.jdbc.configuration.DbCustomStrategy;

import java.sql.*;

/**
 * @author Xiao-Bai
 * @date 2022/10/23 19:08
 * @desc
 */
public class CustomSqlSessionHelper {

    private final DbCustomStrategy strategy;
    private final CustomSqlSession sqlSession;

    public CustomSqlSessionHelper(DbCustomStrategy strategy, CustomSqlSession sqlSession) {
        this.strategy = strategy;
        this.sqlSession = sqlSession;
    }

    /**
     * 返回一个 增/删/改/查 通用的预编译(默认)
     */
    public PreparedStatement defaultPreparedStatement() throws Exception {
        Connection connection = sqlSession.getConnection();
        BaseExecutorBody executorModel = sqlSession.getExecutorModel();
        String prepareSql = executorModel.getPrepareSql();
        return connection.prepareStatement(prepareSql);
    }

    /**
     * 返回可生成新ID的预编译对象
     * <br/> 一般用于插入新数据时使用
     */
    public PreparedStatement generateKeysStatement() throws Exception {
        Connection connection = sqlSession.getConnection();
        BaseExecutorBody executorModel = sqlSession.getExecutorModel();
        String prepareSql = executorModel.getPrepareSql();
        return connection.prepareStatement(prepareSql, Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * 返回可预知查询结果行数的预编译对象
     * <br/> 用于数组查询时，实例化对应的数组长度
     */
    public PreparedStatement resultRowsStatement() throws Exception {
        Connection connection = sqlSession.getConnection();
        BaseExecutorBody executorModel = sqlSession.getExecutorModel();
        String prepareSql = executorModel.getPrepareSql();
        return connection.prepareStatement(prepareSql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }


    /**
     * 获取结果集行数
     */
    public int getRowsCount(ResultSet resultSet) throws SQLException {
        resultSet.last();
        final int rowsCount = resultSet.getRow();
        resultSet.beforeFirst();
        return rowsCount;
    }

    public void handleExecuteBefore(PreparedStatement statement) throws SQLException {
        this.handleExecuteBefore(statement, true);
    }

    /**
     * 查询之前的处理
     * @param query 是否执行查询
     */
    public void handleExecuteBefore(PreparedStatement statement, boolean query) throws SQLException {

        BaseExecutorBody executorModel = sqlSession.getExecutorModel();
        Object[] sqlParams = executorModel.getSqlParams();
        boolean sqlPrintSupport = executorModel.isSqlPrintSupport();
        String prepareSql = executorModel.getPrepareSql();

        // 设置参数
        for (int i = 0; i < sqlParams.length; i++) {
            statement.setObject((i + 1), sqlParams[i]);
        }
        // sql打印
        if (strategy.isSqlOutPrinting() && sqlPrintSupport) {
            SqlOutPrintBuilder builder = SqlOutPrintBuilder.build(prepareSql, sqlParams, strategy.isSqlOutPrintExecute());
            if (query) {
                builder.sqlInfoQueryPrint();
            } else {
                builder.sqlInfoUpdatePrint();
            }
        }
    }

    /**
     * 关闭资源
     */
    public void closeResources(Statement statement, ResultSet resultSet) throws SQLException {

        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (sqlSession != null && sqlSession.isAutoCommit()) {
            sqlSession.closeResources();
        }
    }




}
