package com.custom.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.custom.comm.CustomUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbConnection;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.jdbc.param.SelectSqlParamInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/17 1:15
 * @Desc
 */
@SuppressWarnings("unchecked")
public class CustomJdbcBasicImpl extends DbConnection implements CustomJdbcBasic  {

    private static final Logger logger = LoggerFactory.getLogger(JdbcExecutorImpl.class);

    private final Connection conn;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private final DbCustomStrategy dbCustomStrategy;

//    private static ThreadLocal<Boolean> AUTO_COMMENT = new ThreadLocal<>();


    public CustomJdbcBasicImpl(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        super(dbDataSource);
        this.conn = super.getConnection();
        this.dbCustomStrategy = dbCustomStrategy;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    /**
     * 预编译-更新
     */
    private void statementUpdate(boolean isSave, String sql, Object... params) throws Exception {
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
    private void statementQuery(String sql, boolean sqlPrintSupport, Object... params) throws Exception {
        statement = conn.prepareStatement(sql);
        if (dbCustomStrategy.isSqlOutPrinting() && sqlPrintSupport) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlInfoQueryPrint();
        }
        if (params.length <= 0) return;
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
    }

    /**
     * 预编译-查询2（可预先获取结果集行数）
     */
    private void statementQueryReturnRows(String sql, Object... params) throws Exception {
        statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < params.length; i++) {
            statement.setObject((i + 1), params[i]);
        }
        if (dbCustomStrategy.isSqlOutPrinting()) {
            SqlOutPrintBuilder.build(sql, params, dbCustomStrategy.isSqlOutPrintExecute()).sqlInfoQueryPrint();
        }
    }

    /**
     * 获取结果集对象
     */
    private <T> void getResultMap(Map<String, T> map, ResultSetMetaData metaData) throws SQLException {
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i + 1);
            T object = (T) resultSet.getObject(i + 1);
            map.put(columnName, object);
        }
    }


    @Override
    public <T> List<T> selectList(SelectSqlParamInfo<T> params) throws Exception {
        Map<String, Object> map;
        List<T> list = new ArrayList<>();
        try {
            statementQuery(params.getSelectSql(), params.isSqlPrintSupport(), params);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                T t;
                if(CustomUtil.isBasicClass(params.getEntityClass())) {
                    t = (T) resultSet.getObject(SymbolConstant.DEFAULT_ONE);
                }else {
                    map = new HashMap<>();
                    getResultMap(map, metaData);
                    t = JSONObject.parseObject(JSONObject.toJSONString(map), params.getEntityClass());
                }
                list.add(t);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(params.getSelectSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
        return list;
    }

    @Override
    public <T> Set<T> selectSet(SelectSqlParamInfo<T> params) throws Exception {
        Set<T> resSet = new HashSet<>();
        try {
            statementQuery(params.getSelectSql(), params.isSqlPrintSupport(), params);
            resultSet = statement.executeQuery();
            if (resultSet.getMetaData().getColumnCount() > 1) {
                ExThrowsUtil.toCustom("Set不支持返回多列字段");
            }
            while (resultSet.next()) {
                T object = (T) resultSet.getObject(1);
                resSet.add(object);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder.build(params.getSelectSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute()).sqlErrPrint();
            throw e;
        }
        return resSet;
    }

    @Override
    public <T> Map<String, T> selectMap(SelectSqlParamInfo<T> params) throws Exception {
        return null;
    }

    @Override
    public <T> T[] selectArray(SelectSqlParamInfo<T> params) throws Exception {
        return null;
    }

    @Override
    public Object selectObjBySql(SelectSqlParamInfo<Object> params) throws Exception {
        return null;
    }

    @Override
    public List<Object> selectObjsSql(SelectSqlParamInfo<Object> params) throws Exception {
        return null;
    }

    @Override
    public <T> T selectBasicObjBySql(SelectSqlParamInfo<T> params) throws Exception {
        return null;
    }

    @Override
    public <T> T selectGenericObjSql(SelectSqlParamInfo<T> params) throws Exception {
        return null;
    }

    @Override
    public List<Map<String, Object>> selectMapsBySql(SelectSqlParamInfo<Map<String, Object>> params) throws Exception {
        return null;
    }
}
