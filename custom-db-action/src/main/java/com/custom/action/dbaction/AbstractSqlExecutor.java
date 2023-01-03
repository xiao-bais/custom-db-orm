package com.custom.action.dbaction;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.core.HandleSelectSqlBuilder;
import com.custom.action.extend.MultiResultInjector;
import com.custom.action.condition.ConditionWrapper;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.executor.JdbcExecutorFactory;

import java.io.Serializable;
import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2021/12/8 14:49
 * @desc：方法执行处理抽象入口
 **/
public abstract class AbstractSqlExecutor  {

    /*--------------------------------------- select ---------------------------------------*/
    public abstract <T> List<T> selectList(Class<T> entityClass, String condition, Object... params) throws Exception;
    public abstract <T> List<T> selectListBySql(Class<T> entityClass, String sql, Object... params) throws Exception;
    public abstract <T> DbPageRows<T> selectPage(Class<T> entityClass, String condition, DbPageRows<T> dbPageRows, Object... params) throws Exception;
    public abstract <T> T selectByKey(Class<T> entityClass, Serializable key) throws Exception;
    public abstract <T> List<T> selectBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> T selectOne(Class<T> entityClass, String condition, Object... params) throws Exception;
    public abstract <T> T selectOneBySql(Class<T> entityClass, String sql, Object... params) throws Exception;
    public abstract <T> T selectOne(T entity) throws Exception;
    public abstract <T> List<T> selectList(T entity) throws Exception;
    public abstract <T> DbPageRows<T> selectPage(T entity, DbPageRows<T> pageRows) throws Exception;
    public abstract <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception;
    public abstract Object selectObjBySql(String sql, Object... params) throws Exception;
    public abstract <K, V> Map<K, V> selectMap(Class<K> kClass, Class<V> vClass, String sql, Object... params) throws Exception;


    /**
     * ConditionWrapper(条件构造器)
     */
    public abstract <T> DbPageRows<T> selectPage(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> List<T> selectList(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> T selectOne(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> long selectCount(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> Object selectObj(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> Map<String, Object> selectOneMap(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> List<Map<String, Object>> selectListMap(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> DbPageRows<Map<String, Object>> selectPageMap(ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T, K, V> Map<K, V> selectMap(ConditionWrapper<T> wrapper, Class<K> kClass, Class<V> vClass) throws Exception;


    /*--------------------------------------- delete ---------------------------------------*/
    public abstract <T> int deleteByKey(Class<T> entityClass, Serializable key) throws Exception;
    public abstract <T> int deleteBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys) throws Exception;
    public abstract <T> int deleteByCondition(Class<T> entityClass, String condition, Object... params) throws Exception;
    public abstract <T> int deleteSelective(ConditionWrapper<T> wrapper) throws Exception;

    /*--------------------------------------- insert ---------------------------------------*/
    public abstract <T> int insert(T entity) throws Exception;
    public abstract <T> int insertBatch(List<T> tList) throws Exception;

    /*--------------------------------------- update ---------------------------------------*/
    public abstract <T> int updateByKey(T entity) throws Exception;
    public abstract <T> int updateByKeySelective(T entity) throws Exception;
    public abstract <T> int updateSelective(T entity, ConditionWrapper<T> wrapper) throws Exception;
    public abstract <T> int updateByCondition(T entity, boolean addNullField, String condition, Object... params) throws Exception;

    /**
     * updateSet sql set设置器
     */
    public abstract <T> int updateSelective(AbstractUpdateSet<T> updateSet) throws Exception;

    /*--------------------------------------- comm ---------------------------------------*/
    public abstract <T> int save(T entity) throws Exception;
    public abstract int executeSql(String sql, Object... params) throws Exception;
    public abstract void createTables(Class<?>... arr) throws Exception;
    public abstract void dropTables(Class<?>... arr) throws Exception;
    public abstract DbDataSource getDbDataSource();
    public abstract JdbcExecutorFactory getExecutorFactory();


}
