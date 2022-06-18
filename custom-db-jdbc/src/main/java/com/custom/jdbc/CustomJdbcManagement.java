package com.custom.jdbc;

import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbConnection;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;

import java.sql.*;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/18 1:37
 * @Desc jdbc对象管理
 */
@SuppressWarnings("unchecked")
public class CustomJdbcManagement extends DbConnection {

    private final Connection conn;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private final DbCustomStrategy dbCustomStrategy;

    public CustomJdbcManagement(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        super(dbDataSource);
        this.conn = super.getConnection();
        this.dbCustomStrategy = dbCustomStrategy;
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
        statement = isSave ? conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : conn.prepareStatement(sql);
        if (dbCustomStrategy.isSqlOutPrinting()) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlInfoUpdatePrint();
        }
        if (params.length <= 0) return;
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
    }

    /**
     * 预编译-查询1
     */
    protected void statementQuery(String sql, boolean sqlPrintSupport, Object... params) throws Exception {
        statement = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
        if (dbCustomStrategy.isSqlOutPrinting() && sqlPrintSupport) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlInfoQueryPrint();
        }
    }

    /**
     * 预编译-查询2（可预先获取结果集行数）
     */
    protected void statementQueryReturnRows(String sql, boolean sqlPrintSupport, Object... params) throws Exception {
        statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
        if (dbCustomStrategy.isSqlOutPrinting() && sqlPrintSupport) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlInfoQueryPrint();
        }
    }

    /**
     * 处理结果集对象
     */
    protected <T> void handleResultMap(Map<String, T> map, ResultSetMetaData metaData) throws SQLException {
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i + 1);
            T object = (T) resultSet.getObject(i + 1);
            map.put(columnName, object);
        }
    }

    /**
     * 检查是否多结果
     */
    protected void checkMoreResult() throws SQLException {
        int rowsCount = this.getRowsCount();
        if (rowsCount > SymbolConstant.DEFAULT_ONE) {
            ExThrowsUtil.toCustom("只查一条，但查询到%s条结果", rowsCount);
        }
    }

    /**
     * 获取结果集行数
     */
    protected int getRowsCount() throws SQLException {
        resultSet.last();
        final int rowsCount = resultSet.getRow();
        resultSet.beforeFirst();
        return rowsCount;
    }
}
