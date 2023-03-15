package com.custom.action.core;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.chain.ChainWrapper;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.executor.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.TransactionExecutor;

import java.io.Serializable;
import java.util.*;

/**
 * sql执行器
 * @author   Xiao-Bai
 * @since  2021/12/8 14:49
 **/
public interface SqlExecutor {

    /*--------------------------------------- select ---------------------------------------*/
    <T> List<T> selectList(Class<T> entityClass, String condition, Object... params) throws Exception;
    <T> List<T> selectListBySql(Class<T> entityClass, String sql, Object... params) throws Exception;
    <T> DbPageRows<T> selectPage(Class<T> entityClass, String condition, DbPageRows<T> dbPageRows, Object... params) throws Exception;
    <T> T selectByKey(Class<T> entityClass, Serializable key) throws Exception;
    <T> List<T> selectBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys) throws Exception;
    <T> T selectOne(Class<T> entityClass, String condition, Object... params) throws Exception;
    <T> T selectOneBySql(Class<T> entityClass, String sql, Object... params) throws Exception;
    <T> T selectOne(T entity) throws Exception;
    <T> List<T> selectList(T entity) throws Exception;
    <T> DbPageRows<T> selectPage(T entity, DbPageRows<T> pageRows) throws Exception;
    <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception;
    Object selectObjBySql(String sql, Object... params) throws Exception;
    <K, V> Map<K, V> selectMap(Class<K> kClass, Class<V> vClass, String sql, Object... params) throws Exception;


    /**
     * ConditionWrapper(条件构造器)
     */
    <T> DbPageRows<T> selectPage(ConditionWrapper<T> wrapper) throws Exception;
    <T> List<T> selectList(ConditionWrapper<T> wrapper) throws Exception;
    <T> T selectOne(ConditionWrapper<T> wrapper) throws Exception;
    <T> long selectCount(ConditionWrapper<T> wrapper) throws Exception;
    <T> Object selectObj(ConditionWrapper<T> wrapper) throws Exception;
    <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception;
    <T> Map<String, Object> selectOneMap(ConditionWrapper<T> wrapper) throws Exception;
    <T> List<Map<String, Object>> selectListMap(ConditionWrapper<T> wrapper) throws Exception;
    <T> DbPageRows<Map<String, Object>> selectPageMap(ConditionWrapper<T> wrapper) throws Exception;
    <T, K, V> Map<K, V> selectMap(ConditionWrapper<T> wrapper, Class<K> kClass, Class<V> vClass) throws Exception;


    /*--------------------------------------- delete ---------------------------------------*/
    <T> int deleteByKey(Class<T> entityClass, Serializable key) throws Exception;
    <T> int deleteBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys) throws Exception;
    <T> int deleteByCondition(Class<T> entityClass, String condition, Object... params) throws Exception;
    <T> int deleteSelective(ConditionWrapper<T> wrapper) throws Exception;

    /*--------------------------------------- insert ---------------------------------------*/
    <T> int insert(T entity) throws Exception;
    <T> int insertBatch(List<T> tList) throws Exception;

    /*--------------------------------------- update ---------------------------------------*/
    <T> int updateByKey(T entity) throws Exception;
    <T> int updateSelective(T entity, ConditionWrapper<T> wrapper) throws Exception;
    <T> int updateByCondition(T entity, String condition, Object... params) throws Exception;

    /**
     * updateSet sql set设置器
     */
    <T> int updateSelective(AbstractUpdateSet<T> updateSet) throws Exception;

    /*--------------------------------------- comm ---------------------------------------*/
    <T> int save(T entity) throws Exception;
    int executeSql(String sql, Object... params) throws Exception;
    void createTables(Class<?>... arr) throws Exception;
    void dropTables(Class<?>... arr) throws Exception;
    DbDataSource getDbDataSource();
    JdbcSqlSessionFactory getSqlSessionFactory();
    void execTrans(TransactionExecutor wrapper) throws Exception;
    <T> ChainWrapper<T> createChain(Class<T> entityClass) throws Exception;



}
