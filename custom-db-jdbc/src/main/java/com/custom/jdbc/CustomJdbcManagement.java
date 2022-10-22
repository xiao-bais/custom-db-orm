package com.custom.jdbc;

import com.custom.comm.utils.Constants;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.jdbc.configuration.DbConnection;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.transaction.DbConnGlobal;

import java.sql.*;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/18 1:37
 * @Desc jdbc对象管理
 */
@SuppressWarnings("unchecked")
public class CustomJdbcManagement extends DbConnection {

    private Connection conn = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private final DbCustomStrategy dbCustomStrategy;

    public CustomJdbcManagement(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        super(dbDataSource);
        this.dbCustomStrategy = dbCustomStrategy;
        DbConnGlobal.addDataSource(new CustomConfigHelper(dbDataSource, dbCustomStrategy));
    }

    protected PreparedStatement getStatement() {
        return statement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    protected ResultSet handleQueryStatement() throws SQLException {
        this.resultSet = this.statement.executeQuery();
        return resultSet;
    }

    protected int handleUpdateStatement() throws SQLException {
        return this.statement.executeUpdate();
    }

    protected ResultSet handleGenerateKeysStatement() throws Exception {
        return this.statement.getGeneratedKeys();
    }

    protected void handleExecTableInfo(String execSql) throws Exception {
        this.statement = this.conn.prepareStatement(execSql);
        this.statement.execute();
    }



    /**
     * 预编译-更新
     */
    protected void statementUpdate(boolean isSave, String sql, Object... params) throws Exception {
        this.conn = super.createConnection();
        statement = isSave ? conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
        if (dbCustomStrategy.isSqlOutPrinting()) {
            SqlOutPrintBuilder
                    .build(sql, params, dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlInfoUpdatePrint();
        }
    }

    /**
     * 预编译-查询1
     */
    protected void statementQuery(String sql, boolean sqlPrintSupport, Object... params) throws Exception {
        this.conn = super.createConnection();
        this.statement = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            this.statement.setObject((i + 1), params[i]);
        }
        if (dbCustomStrategy.isSqlOutPrinting() && sqlPrintSupport) {
            SqlOutPrintBuilder
                    .build(sql, params, dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlInfoQueryPrint();
        }
    }

    /**
     * 预编译-查询2（可预先获取结果集行数）
     */
    protected void statementQueryReturnRows(String sql, boolean sqlPrintSupport, Object... params) throws Exception {
        this.conn = super.createConnection();
        statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
        if (dbCustomStrategy.isSqlOutPrinting() && sqlPrintSupport) {
            SqlOutPrintBuilder
                    .build(sql, params, dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlInfoQueryPrint();
        }
    }

    /**
     * 处理结果集对象映射
     */
    protected <T> void handleResultMapper(Map<String, T> map, ResultSetMetaData metaData) throws SQLException {
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i + 1);
            T object = (T) resultSet.getObject(i + 1);
            map.put(columnName, object);
        }
    }

    /**
     * 检查是否多结果
     */
    protected void checkMoreResult(ResultSet resultSet) throws SQLException {
        int rowsCount = this.getRowsCount(resultSet);
        if (rowsCount > Constants.DEFAULT_ONE) {
            ExThrowsUtil.toCustom("只查一条，但查询到%s条结果", rowsCount);
        }
    }

    /**
     * 获取结果集行数
     */
    protected int getRowsCount(ResultSet resultSet) throws SQLException {
        resultSet.last();
        final int rowsCount = resultSet.getRow();
        resultSet.beforeFirst();
        return rowsCount;
    }

    /**
     * 关闭资源
     */
    protected void closeResources() {
        try {
            if (this.conn != null) {
                Boolean connCanClose = (Boolean) GlobalDataHandler.readGlobalObject(Constants.TRANS_CURSOR);
                if (connCanClose == null || !connCanClose) {
                    this.conn.close();
                }
            }
            if (this.resultSet != null) {
                this.resultSet.close();
            }
            if (this.statement != null) {
                this.statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
