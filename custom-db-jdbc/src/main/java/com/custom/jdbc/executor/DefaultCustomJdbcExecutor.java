package com.custom.jdbc.executor;

import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.CustomUtil;
import com.custom.jdbc.CustomSqlSessionHandler;
import com.custom.jdbc.SqlOutPrintBuilder;
import com.custom.jdbc.condition.BaseExecutorModel;
import com.custom.jdbc.condition.SaveExecutorModel;
import com.custom.jdbc.condition.SelectMapExecutorModel;
import com.custom.jdbc.session.CustomSqlSession;
import com.custom.jdbc.condition.SelectExecutorModel;
import com.custom.jdbc.configuration.DbCustomStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        CustomSqlSessionHandler sessionHandler = new CustomSqlSessionHandler(strategy, sqlSession);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            statement = sessionHandler.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHandler.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            while (resultSet.next()) {
                T t;
                if (CustomUtil.isBasicClass(executorModel.getEntityClass())) {
                    t = (T) resultSet.getObject(Constants.DEFAULT_ONE);
                } else {
                    map = new HashMap<>();
                    sessionHandler.handleResultMapper(map, resultSet, metaData);
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
            sessionHandler.closeResources(statement, resultSet);
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
        CustomSqlSessionHandler sessionHandler = new CustomSqlSessionHandler(strategy, sqlSession);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 执行
            statement = sessionHandler.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHandler.handleExecuteBefore(statement);
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
            sessionHandler.closeResources(statement, resultSet);
        }
        return resSet;
    }

    @Override
    public <T> List<T> selectObjs(CustomSqlSession sqlSession) throws Exception {

        SelectExecutorModel<T> executorModel = (SelectExecutorModel<T>) sqlSession.getExecutorModel();
        CustomSqlSessionHandler sessionHandler = new CustomSqlSessionHandler(strategy, sqlSession);
        List<T> list = new ArrayList<>();

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            statement = sessionHandler.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHandler.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                T t;
                if (CustomUtil.isBasicClass(executorModel.getEntityClass())) {
                    t = (T) resultSet.getObject(Constants.DEFAULT_ONE);
                } else {
                    throw new UnsupportedOperationException("This [" + executorModel.getEntityClass() + "] of query is not supported");
                }
                list.add(t);
            }
        } catch (SQLException e) {
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
    public Object selectObj(CustomSqlSession sqlSession) throws Exception {
        List<Object> result = this.selectObjs(sqlSession);
        return getOne(result);
    }


    @Override
    public List<Map<String, Object>> selectListMap(CustomSqlSession sqlSession) throws Exception {
        Map<String, Object> map;
        List<Map<String, Object>> list = new ArrayList<>();
        SelectExecutorModel<Object> executorModel = (SelectExecutorModel<Object>) sqlSession.getExecutorModel();
        CustomSqlSessionHandler sessionHandler = new CustomSqlSessionHandler(strategy, sqlSession);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            statement = sessionHandler.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHandler.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            while (resultSet.next()) {
                map = new HashMap<>();
                sessionHandler.handleResultMapper(map, resultSet, metaData);
                list.add(map);
            }
        } catch (SQLException e) {
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
    public Map<String, Object> selectOneMap(CustomSqlSession sqlSession) throws Exception {
        List<Map<String, Object>> result = selectListMap(sqlSession);
        return getOne(result);
    }


    @Override
    public <K, V> List<Map<K, V>> selectMaps(CustomSqlSession sqlSession) throws Exception {

        List<Map<K, V>> list = new ArrayList<>();
        SelectMapExecutorModel<K, V> executorModel = (SelectMapExecutorModel<K, V>) sqlSession.getExecutorModel();
        CustomSqlSessionHandler sessionHandler = new CustomSqlSessionHandler(strategy, sqlSession);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            statement = sessionHandler.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHandler.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            // 若查询的结果列数不是2列，则抛出异常
            int columnCount = metaData.getColumnCount();
            if (columnCount != 2) {
                throw new SQLDataException("This query only supports dual column queries. Current number of query fields: (" + columnCount + ")");
            }

            Map<K, V> map = null;
//            Map<Object, Object> tempMap;
            while (resultSet.next()) {
//                tempMap = new HashMap<>();
                map = new HashMap<>();

                try {
                    K key = (K) resultSet.getObject(1);
                    V value = (V) resultSet.getObject(2);
                    map.put(key, value);
                } catch (NumberFormatException e) {
                    // 可能错误: 查询结果中出现给定泛型之外的类型
                    logger.error("The error may be: because the value in the query result has a type other than the given generic type");
                    throw e;
                }


                // 映射键值对
//                Object key = resultSet.getObject(1);
//                Object value = resultSet.getObject(2);


                // 利用反序列化生成目标map实例
//                try {
//
//                    String jsonStr = CustomUtil.mapObjToJsonString(tempMap);
//                    map = CustomUtil.jsonParseToMap(jsonStr, executorModel.getKeyType(), executorModel.getValueType());
//
//                } catch (JSONException e) {
//                    if (e.getCause() instanceof NumberFormatException) {
//                        // 可能错误: 查询结果中出现给定泛型之外的类型
//                        logger.error("The error may be: because the value in the query result has a type other than the given generic type");
//                    }
//                    throw e;
//                }

                list.add(map);
            }
        } catch (SQLException e) {
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
    public <T> T[] selectArrays(CustomSqlSession sqlSession) throws Exception {

        CustomSqlSessionHandler sessionHandler = new CustomSqlSessionHandler(strategy, sqlSession);
        SelectExecutorModel<T> executorModel = (SelectExecutorModel<T>) sqlSession.getExecutorModel();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = sessionHandler.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHandler.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();



            return null;
        } catch (Exception e) {
            SqlOutPrintBuilder
                    .build(executorModel.getPrepareSql(), executorModel.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        } finally {
            sessionHandler.closeResources(statement, null);
        }
    }


    @Override
    public <T> int executeUpdate(CustomSqlSession sqlSession) throws Exception {

        CustomSqlSessionHandler sessionHandler = new CustomSqlSessionHandler(strategy, sqlSession);
        BaseExecutorModel executorModel = sqlSession.getExecutorModel();
        PreparedStatement statement = null;
        try {
            statement = sessionHandler.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHandler.handleExecuteBefore(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(executorModel.getPrepareSql(), executorModel.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        } finally {
            sessionHandler.closeResources(statement, null);
        }

    }

    @Override
    public <T> int executeSave(CustomSqlSession sqlSession) throws Exception {

        CustomSqlSessionHandler sessionHandler = new CustomSqlSessionHandler(strategy, sqlSession);
        SaveExecutorModel<T> executorModel = (SaveExecutorModel<T>) sqlSession.getExecutorModel();

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Field keyField = executorModel.getKeyField();
        List<T> dataList = executorModel.getDataList();
        try {
            statement = sessionHandler.generateKeysStatement();
            // 处理预编译以及sql打印
            sessionHandler.handleExecuteBefore(statement);
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
            sessionHandler.closeResources(statement, resultSet);
        }

    }

    @Override
    public void execTableInfo(CustomSqlSession sqlSession) throws Exception {

        BaseExecutorModel executorModel = sqlSession.getExecutorModel();
        String prepareSql = executorModel.getPrepareSql();
        CustomSqlSessionHandler sessionHandler = new CustomSqlSessionHandler(strategy, sqlSession);
        PreparedStatement statement = null;

        try {
            Connection connection = sqlSession.getConnection();
            statement = connection.prepareStatement(prepareSql);
            statement.execute();
        } catch (Exception e) {
            SqlOutPrintBuilder
                    .build(prepareSql, new String[]{}, strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            logger.error(e.toString(), e);
        } finally {
            sessionHandler.closeResources(statement, null);
        }
    }


    /**
     * 返回单条记录或单个值
     */
    private <T> T getOne(List<T> result) {
        if (result.size() == 0) {
            return null;
        }
        Asserts.illegal(result.size() > 1,
                String.format("只查一条，但查询到%s条结果", result.size()));
        return result.get(0);
    }


}
