package com.custom.jdbc.session;

import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.CustomApp;
import com.custom.comm.utils.ReflectUtil;
import com.custom.jdbc.condition.BaseExecutorBody;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.jdbc.executor.CustomSqlInterceptor;
import com.custom.jdbc.interfaces.CustomSqlSession;
import com.custom.jdbc.sqlprint.SqlOutPrintBuilder;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.sql.*;

/**
 * sql 会话辅助对象
 * @author  Xiao-Bai
 * @since  2022/10/23 19:08
 */
public class CustomSqlSessionHelper {

    private final DbGlobalConfig globalConfig;
    private final CustomSqlSession sqlSession;

    public CustomSqlSessionHelper(DbGlobalConfig globalConfig, CustomSqlSession sqlSession) {
        this.globalConfig = globalConfig;
        this.sqlSession = sqlSession;
    }


    /**
     * sql执行前的拦截。可对将要执行的sql以及参数信息进行一定的辅助操作
     */
    private void handleInterceptor() throws Exception {
        CustomSqlInterceptor sqlInterceptor;

        try {
            sqlInterceptor = CustomApp.getBean(CustomSqlInterceptor.class);
        } catch (NoSuchBeanDefinitionException e) {
            sqlInterceptor = null;
        }

        if (sqlInterceptor == null) {
            Class<? extends CustomSqlInterceptor> sqlInterceptorClass = globalConfig.getSqlInterceptor();
            if (sqlInterceptorClass == null) {
                return;
            }
            sqlInterceptor = ReflectUtil.getInstance(sqlInterceptorClass);
        }
        BaseExecutorBody executorBody = sqlInterceptor.handle(sqlSession.getBody());
        Asserts.notNull(executorBody, "The execution body is missing. Please check whether the sql interceptor returns.");
        this.sqlSession.setBody(executorBody);
    }


    /**
     * 返回一个 增/删/改/查 通用的预编译(默认)
     */
    public PreparedStatement defaultPreparedStatement() throws Exception {
        this.handleInterceptor();
        Connection connection = sqlSession.getConnection();
        BaseExecutorBody executorModel = sqlSession.getBody();
        String prepareSql = executorModel.getPrepareSql();
        return connection.prepareStatement(prepareSql);
    }

    /**
     * 返回可生成新ID的预编译对象
     * <br/> 一般用于插入新数据时使用
     */
    public PreparedStatement generateKeysStatement() throws Exception {
        this.handleInterceptor();
        Connection connection = sqlSession.getConnection();
        BaseExecutorBody executorModel = sqlSession.getBody();
        String prepareSql = executorModel.getPrepareSql();
        return connection.prepareStatement(prepareSql, Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * 返回可预知查询结果行数的预编译对象
     * <br/> 用于数组查询时，实例化对应的数组长度
     */
    public PreparedStatement resultRowsStatement() throws Exception {
        this.handleInterceptor();
        Connection connection = sqlSession.getConnection();
        BaseExecutorBody executorModel = sqlSession.getBody();
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

        BaseExecutorBody executorModel = sqlSession.getBody();
        Object[] sqlParams = executorModel.getSqlParams();
        boolean sqlPrintSupport = executorModel.isSqlPrintSupport();
        String prepareSql = executorModel.getPrepareSql();

        // 设置参数
        for (int i = 0; i < sqlParams.length; i++) {
            statement.setObject((i + 1), sqlParams[i]);
        }
        DbCustomStrategy strategy = globalConfig.getStrategy();
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
