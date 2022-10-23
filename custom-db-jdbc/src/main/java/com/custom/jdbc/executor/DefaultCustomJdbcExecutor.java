package com.custom.jdbc.executor;

import com.alibaba.fastjson.JSONObject;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.CustomUtil;
import com.custom.jdbc.CustomSqlSessionHandler;
import com.custom.jdbc.SqlOutPrintBuilder;
import com.custom.jdbc.session.CustomSqlSession;
import com.custom.jdbc.condition.SelectExecutorModel;
import com.custom.jdbc.configuration.DbCustomStrategy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2022/10/23 19:05
 * @desc
 */
@SuppressWarnings("unchecked")
public class DefaultCustomJdbcExecutor implements CustomJdbcExecutor {

    private final DbCustomStrategy strategy;

    public DefaultCustomJdbcExecutor(DbCustomStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public <T> List<T> selectList(CustomSqlSession<T> sqlSession) throws Exception {
        Map<String, Object> map;
        List<T> list = new ArrayList<>();
        SelectExecutorModel<T> executorModel = (SelectExecutorModel<T>) sqlSession.getExecutorModel();
        CustomSqlSessionHandler<T> sessionHandler = new CustomSqlSessionHandler<>(strategy, sqlSession);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            statement = sessionHandler.statementQuery();
            // 处理预编译以及sql打印
            sessionHandler.handleQueryBefore(statement);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            while (resultSet.next()) {
                T t;
                if (CustomUtil.isBasicClass(executorModel.getEntityClass())) {
                    t = (T) resultSet.getObject(Constants.DEFAULT_ONE);
                } else {
                    map = new HashMap<>();
                    sessionHandler.handleResultMapper(map, resultSet, metaData);
                    t = JSONObject.parseObject(JSONObject.toJSONString(map), executorModel.getEntityClass());
                }
                list.add(t);
            }
        } catch (
                SQLException e) {
            SqlOutPrintBuilder
                    .build(executorModel.getPrepareSql(), executorModel.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        } finally {
            sessionHandler.closeResources(statement, resultSet);
        }
        return list;
    }

    @Override
    public <T> T selectOne(CustomSqlSession<T> sqlSession) throws Exception {
        List<T> result = this.selectList(sqlSession);
        if (result.size() == 0) {
            return null;
        }
        Asserts.illegal(result.size() > 1,
                String.format("只查一条，但查询到%s条结果", result.size()));
        return result.get(0);
    }

    @Override
    public <T> Set<T> selectSet(CustomSqlSession<T> sqlSession) throws Exception {
        Set<T> resSet = new HashSet<>();
        SelectExecutorModel<T> executorModel = (SelectExecutorModel<T>) sqlSession.getExecutorModel();
        CustomSqlSessionHandler<T> sessionHandler = new CustomSqlSessionHandler<>(strategy, sqlSession);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 执行
            statement = sessionHandler.statementQuery();
            // 处理预编译以及sql打印
            sessionHandler.handleQueryBefore(statement);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                T object = (T) resultSet.getObject(Constants.DEFAULT_ONE);
                resSet.add(object);
            }

        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(executorModel.getPrepareSql(), executorModel.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }finally {
            sessionHandler.closeResources(statement, resultSet);
        }
        return resSet;
    }

    @Override
    public <T> List<T> selectObjs(CustomSqlSession<T> sqlSession) throws Exception {




        return null;
    }

    @Override
    public <T> Object selectObj(CustomSqlSession<T> sqlSession) throws Exception {
        return null;
    }
}
