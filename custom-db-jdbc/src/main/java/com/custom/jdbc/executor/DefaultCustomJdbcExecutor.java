package com.custom.jdbc.executor;

import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.CustomUtil;
import com.custom.jdbc.CustomSqlSessionHelper;
import com.custom.jdbc.SqlOutPrintBuilder;
import com.custom.jdbc.condition.BaseExecutorModel;
import com.custom.jdbc.condition.SaveExecutorModel;
import com.custom.jdbc.condition.SelectMapExecutorModel;
import com.custom.jdbc.session.CustomSqlSession;
import com.custom.jdbc.condition.SelectExecutorModel;
import com.custom.jdbc.configuration.DbCustomStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2022/10/23 19:05
 * @desc
 */
@SuppressWarnings("unchecked")
public class DefaultCustomJdbcExecutor implements CustomJdbcExecutor {

    private final DbCustomStrategy strategy;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DefaultCustomJdbcExecutor(DbCustomStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public <T> List<T> selectList(CustomSqlSession sqlSession) throws Exception {
        Map<String, Object> map;
        List<T> list = new ArrayList<>();
        SelectExecutorModel<T> executorModel = (SelectExecutorModel<T>) sqlSession.getExecutorModel();
        CustomSqlSessionHelper sessionHelper = new CustomSqlSessionHelper(strategy, sqlSession);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            statement = sessionHelper.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            while (resultSet.next()) {
                T t;
                if (CustomUtil.isBasicClass(executorModel.getEntityClass())) {
                    t = (T) resultSet.getObject(Constants.DEFAULT_ONE);
                } else {
                    map = new HashMap<>();
                    sessionHelper.handleResultMapper(map, resultSet, metaData);
                    t = CustomUtil.convertBean(map, executorModel.getEntityClass());
                }
                list.add(t);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(executorModel.getPrepareSql(), executorModel.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, resultSet);
        }
        return list;
    }

    @Override
    public <T> T selectOne(CustomSqlSession sqlSession) throws Exception {
        List<T> result = this.selectList(sqlSession);
        return getOne(result);
    }

    @Override
    public <T> Set<T> selectSet(CustomSqlSession sqlSession) throws Exception {
        Set<T> resSet = new HashSet<>();
        SelectExecutorModel<T> executorModel = (SelectExecutorModel<T>) sqlSession.getExecutorModel();
        CustomSqlSessionHelper sessionHelper = new CustomSqlSessionHelper(strategy, sqlSession);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 执行
            statement = sessionHelper.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
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
        } finally {
            sessionHelper.closeResources(statement, resultSet);
        }
        return resSet;
    }

    @Override
    public <T> List<T> selectObjs(CustomSqlSession sqlSession) throws Exception {

        SelectExecutorModel<T> executorModel = (SelectExecutorModel<T>) sqlSession.getExecutorModel();
        CustomSqlSessionHelper sessionHelper = new CustomSqlSessionHelper(strategy, sqlSession);

        List<T> list = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            statement = sessionHelper.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                try {
                    T t = (T) resultSet.getObject(Constants.DEFAULT_ONE);
                    list.add(t);
                }catch (ClassCastException e) {
                    if (!CustomUtil.isBasicClass(executorModel.getEntityClass())) {
                        throw new UnsupportedOperationException("This [" + executorModel.getEntityClass() + "] of query is not supported");
                    }
                    throw e;
                }
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(executorModel.getPrepareSql(), executorModel.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, resultSet);
        }
        return list;
    }

    @Override
    public Object selectObj(CustomSqlSession sqlSession) throws Exception {
        List<Object> result = this.selectObjs(sqlSession);
        return getOne(result);
    }


    @Override
    public List<Map<String, Object>> selectListMap(CustomSqlSession sqlSession) throws Exception {
        Map<String, Object> map;
        List<Map<String, Object>> list = new ArrayList<>();
        SelectExecutorModel<Object> executorModel = (SelectExecutorModel<Object>) sqlSession.getExecutorModel();
        CustomSqlSessionHelper sessionHelper = new CustomSqlSessionHelper(strategy, sqlSession);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            statement = sessionHelper.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            while (resultSet.next()) {
                map = new HashMap<>();
                sessionHelper.handleResultMapper(map, resultSet, metaData);
                list.add(map);
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(executorModel.getPrepareSql(), executorModel.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, resultSet);
        }
        return list;
    }



    @Override
    public Map<String, Object> selectOneMap(CustomSqlSession sqlSession) throws Exception {
        List<Map<String, Object>> result = selectListMap(sqlSession);
        return getOne(result);
    }


    @Override
    public <K, V> Map<K, V> selectMaps(CustomSqlSession sqlSession) throws Exception {

        SelectMapExecutorModel<K, V> executorModel = (SelectMapExecutorModel<K, V>) sqlSession.getExecutorModel();
        CustomSqlSessionHelper sessionHelper = new CustomSqlSessionHelper(strategy, sqlSession);

        Map<K, V> map = new HashMap<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            statement = sessionHelper.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            // 若查询的结果列数不是2列，则抛出异常
            int columnCount = metaData.getColumnCount();
            if (columnCount != 2) {
                throw new SQLDataException("This query only supports dual column queries. Current number of query fields: (" + columnCount + ")");
            }

            while (resultSet.next()) {

                try {
                    // 映射键值对
                    K key = (K) resultSet.getObject(1);
                    V value = (V) resultSet.getObject(2);
                    map.put(key, value);
                } catch (NumberFormatException e) {
                    // 可能错误: 查询结果中出现给定泛型之外的类型
                    logger.error("The error may be: " +
                            "because the value in the query result has a type other than the given generic type");
                    throw e;
                }
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(executorModel.getPrepareSql(), executorModel.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, resultSet);
        }
        return map;
    }

    @Override
    public <T> T[] selectArrays(CustomSqlSession sqlSession) throws Exception {

        CustomSqlSessionHelper sessionHelper = new CustomSqlSessionHelper(strategy, sqlSession);
        SelectExecutorModel<T> executorModel = (SelectExecutorModel<T>) sqlSession.getExecutorModel();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = sessionHelper.resultRowsStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();

            // 返回的数组长度
            int rowsCount = sessionHelper.getRowsCount(resultSet);
            int count = resultSet.getMetaData().getColumnCount();
            if (count == 0) {
                return null;
            } else if (count > 1) {
                ExThrowsUtil.toCustom("数组不支持返回多列结果");
            }

            Object res = Array.newInstance(executorModel.getEntityClass(), rowsCount);
            int len = 0;
            while (resultSet.next()) {
                T val = (T) resultSet.getObject(Constants.DEFAULT_ONE);
                Array.set(res, len, val);
                len++;
            }
            return (T[])res;
        } catch (Exception e) {
            SqlOutPrintBuilder
                    .build(executorModel.getPrepareSql(), executorModel.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, null);
        }
    }


    @Override
    public <T> int executeUpdate(CustomSqlSession sqlSession) throws Exception {

        CustomSqlSessionHelper sessionHelper = new CustomSqlSessionHelper(strategy, sqlSession);
        BaseExecutorModel executorModel = sqlSession.getExecutorModel();
        PreparedStatement statement = null;
        try {
            statement = sessionHelper.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(executorModel.getPrepareSql(), executorModel.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, null);
        }

    }

    @Override
    public <T> int executeSave(CustomSqlSession sqlSession) throws Exception {

        CustomSqlSessionHelper sessionHelper = new CustomSqlSessionHelper(strategy, sqlSession);
        SaveExecutorModel<T> executorModel = (SaveExecutorModel<T>) sqlSession.getExecutorModel();

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Field keyField = executorModel.getKeyField();
        List<T> dataList = executorModel.getDataList();
        try {
            statement = sessionHelper.generateKeysStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
            int res = statement.executeUpdate();
            // 若是自增，则返回生成的新ID
            resultSet = statement.getGeneratedKeys();

            int count = 0;
            while (resultSet.next()) {
                Object newKey = resultSet.getObject(1);
                T entity = dataList.get(count);
                // 新的ID写入到实体
                CustomUtil.writeFieldValue(newKey, entity, keyField.getName(), keyField.getType());
                count++;
            }
            return res;
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(executorModel.getPrepareSql(), executorModel.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, resultSet);
        }

    }

    @Override
    public void execTableInfo(CustomSqlSession sqlSession) throws Exception {

        BaseExecutorModel executorModel = sqlSession.getExecutorModel();
        String prepareSql = executorModel.getPrepareSql();
        CustomSqlSessionHelper sessionHelper = new CustomSqlSessionHelper(strategy, sqlSession);
        Statement statement = null;

        try {
            Connection connection = sqlSession.getConnection();
            statement = connection.createStatement();
            statement.execute(prepareSql);
        } catch (Exception e) {
            SqlOutPrintBuilder
                    .build(prepareSql, new String[]{}, strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            logger.error(e.toString(), e);
        } finally {
            sessionHelper.closeResources(statement, null);
        }
    }


    /**
     * 返回单条记录或单个值
     */
    private <T> T getOne(List<T> result) {
        if (result.size() == 0) {
            return null;
        }
        Asserts.unSupportOp(result.size() > 1,
                String.format("只查一条，但查询到%s条结果", result.size()));
        return result.get(0);
    }


}
