package com.custom.jdbc.executor;

import com.custom.comm.utils.AssertUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.ReflectUtil;
import com.custom.jdbc.handler.ResultSetTypeMappedHandler;
import com.custom.jdbc.session.CustomSqlSessionHelper;
import com.custom.jdbc.sqlprint.SqlOutPrintBuilder;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.executebody.SaveExecutorBody;
import com.custom.jdbc.executebody.SelectMapExecutorBody;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.interfaces.CustomSqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * 默认的原生jdbc执行对象
 * @author  Xiao-Bai
 * @since  2022/10/23 19:05
 */
@SuppressWarnings("unchecked")
public class DefaultCustomJdbcExecutor implements CustomJdbcExecutor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public <T> List<T> selectList(CustomSqlSession sqlSession) throws Exception {

        List<T> list = new ArrayList<>();
        SelectExecutorBody<T> executorBody = (SelectExecutorBody<T>) sqlSession.getBody();
        CustomSqlSessionHelper sessionHelper = sqlSession.getHelper();
        Class<T> mappedType = executorBody.getMappedType();
        ResultSetTypeMappedHandler<T> typeMappedHandler = sqlSession.getMappedHandler(executorBody.getMappedType());

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            statement = sessionHelper.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                T t;
                if (CustomUtil.isBasicClass(mappedType)) {
                    t = typeMappedHandler.getTargetValue(resultSet, Constants.DEFAULT_ONE);
                } else {
                    t = typeMappedHandler.getTargetObject(resultSet);
                }
                list.add(t);
            }
        } catch (SQLException e) {
            sessionHelper.sqlErrorOutPrinting();
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
        List<T> selectObjs = selectObjs(sqlSession);
        return new HashSet<>(selectObjs);
    }

    @Override
    public <T> List<T> selectObjs(CustomSqlSession sqlSession) throws Exception {

        SelectExecutorBody<T> executorBody = (SelectExecutorBody<T>) sqlSession.getBody();
        CustomSqlSessionHelper sessionHelper = sqlSession.getHelper();
        Class<T> mappedType = executorBody.getMappedType();
        ResultSetTypeMappedHandler<T> typeMappedHandler = sqlSession.getMappedHandler(mappedType);

        List<T> list = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = sessionHelper.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();

            try {
                typeMappedHandler.writeForCollection(list, resultSet);
            } catch (ClassCastException e) {
                if (!CustomUtil.isBasicClass(mappedType)) {
                    throw new UnsupportedOperationException("This [" + mappedType + "] of query is not supported");
                }
                throw e;
            }
        } catch (SQLException e) {
            sessionHelper.sqlErrorOutPrinting();
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
    public <V> List<Map<String, V>> selectListMap(CustomSqlSession sqlSession) throws Exception {
        Map<String, V> map;
        List<Map<String, V>> list = new ArrayList<>();
        SelectExecutorBody<V> executorBody = (SelectExecutorBody<V>) sqlSession.getBody();
        CustomSqlSessionHelper sessionHelper = sqlSession.getHelper();
        ResultSetTypeMappedHandler<V> typeMappedHandler = sqlSession.getMappedHandler(executorBody.getMappedType());

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {

            statement = sessionHelper.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                map = new HashMap<>();
                typeMappedHandler.writeForMap(map, resultSet);
                list.add(map);
            }
        } catch (SQLException e) {
            sessionHelper.sqlErrorOutPrinting();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, resultSet);
        }
        return list;

    }


    @Override
    public <V> Map<String, V> selectOneMap(CustomSqlSession sqlSession) throws Exception {
        List<Map<String, V>> result = selectListMap(sqlSession);
        return getOne(result);
    }


    @Override
    public <K, V> Map<K, V> selectMap(CustomSqlSession sqlSession) throws Exception {

        SelectMapExecutorBody<K, V> executorBody = (SelectMapExecutorBody<K, V>) sqlSession.getBody();
        CustomSqlSessionHelper sessionHelper = sqlSession.getHelper();
        ResultSetTypeMappedHandler<K> keyTypeMappedHandler = sqlSession.getMappedHandler(executorBody.getKeyType());
        ResultSetTypeMappedHandler<V> valTypeMappedHandler = sqlSession.getMappedHandler(executorBody.getValueType());

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
                    K key = keyTypeMappedHandler.getTargetValue(resultSet, 1);
                    V value = valTypeMappedHandler.getTargetValue(resultSet, 2);
                    map.put(key, value);
                } catch (NumberFormatException e) {
                    // 可能错误: 查询结果中出现给定泛型之外的类型
                    logger.error("The error may be: " +
                            "because the value in the query result has a type other than the given generic type");
                    throw e;
                }
            }
        } catch (SQLException e) {
            sessionHelper.sqlErrorOutPrinting();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, resultSet);
        }
        return map;
    }

    @Override
    public <T> T[] selectArrays(CustomSqlSession sqlSession) throws Exception {

        CustomSqlSessionHelper sessionHelper = sqlSession.getHelper();
        SelectExecutorBody<T> executorBody = (SelectExecutorBody<T>) sqlSession.getBody();
        ResultSetTypeMappedHandler<T> typeMappedHandler = sqlSession.getMappedHandler(executorBody.getMappedType());

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = sessionHelper.resultRowsStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement);
            resultSet = statement.executeQuery();

            // 返回的数组长度
            int rowsCount = sessionHelper.getRowsCount(resultSet);
            Object res = Array.newInstance(executorBody.getMappedType(), rowsCount);

            // 写入数组
            typeMappedHandler.writeForArrays(res, resultSet);

            return (T[]) res;
        } catch (Exception e) {
            sessionHelper.sqlErrorOutPrinting();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, resultSet);
        }
    }


    @Override
    public int executeUpdate(CustomSqlSession sqlSession) throws Exception {

        CustomSqlSessionHelper sessionHelper = sqlSession.getHelper();
        PreparedStatement statement = null;
        try {
            statement = sessionHelper.defaultPreparedStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement, false);
            return statement.executeUpdate();
        } catch (SQLException e) {
            sessionHelper.sqlErrorOutPrinting();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, null);
        }

    }

    @Override
    public <T> int executeSave(CustomSqlSession sqlSession) throws Exception {

        CustomSqlSessionHelper sessionHelper = sqlSession.getHelper();
        SaveExecutorBody<T> executorBody = (SaveExecutorBody<T>) sqlSession.getBody();
        DbCustomStrategy strategy = sessionHelper.getGlobalConfig().getStrategy();

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Field keyField = executorBody.getKeyField();
        List<T> dataList = executorBody.getDataList();
        try {
            statement = sessionHelper.generateKeysStatement();
            // 处理预编译以及sql打印
            sessionHelper.handleExecuteBefore(statement, false);
            int res = statement.executeUpdate();
            // 若是自增，则返回生成的新ID
            resultSet = statement.getGeneratedKeys();

            int count = 0;
            while (resultSet.next()) {
                Object newKey = resultSet.getObject(1);
                T entity = dataList.get(count);
                // 新的ID写入到实体
                ReflectUtil.writeFieldValue(newKey, entity, keyField.getName(), keyField.getType());
                count ++;
            }
            return res;
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(executorBody.getPrepareSql(), executorBody.getSqlParams(), strategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        } finally {
            sessionHelper.closeResources(statement, resultSet);
        }

    }

    @Override
    public void execTableInfo(CustomSqlSession sqlSession) throws Exception {

        BaseExecutorBody executorBody = sqlSession.getBody();
        String prepareSql = executorBody.getPrepareSql();
        CustomSqlSessionHelper sessionHelper = sqlSession.getHelper();

        Statement statement = null;
        try {
            Connection connection = sqlSession.getConnection();
            statement = connection.createStatement();
            statement.execute(prepareSql);
        } catch (Exception e) {
            sessionHelper.sqlErrorOutPrinting();
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
        AssertUtil.cce(result.size() == 1,
                String.format("只查一条，但查询到%s条结果", result.size()));
        return result.get(0);
    }


}
