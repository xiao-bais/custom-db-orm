package com.custom.action.dbaction;

import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.action.wrapper.ConditionWrapper;
import com.custom.action.wrapper.SFunction;
import com.custom.comm.page.DbPageRows;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/15 0015 16:51
 * @Desc
 */
public interface JdbcActiveWrapper<T, P> {

    /*--------------------------------------- select ---------------------------------------*/
    DbPageRows<T> selectPageRows(ConditionWrapper<T> wrapper) throws Exception;
    List<T> selectList(ConditionWrapper<T> wrapper) throws Exception;
    T selectOneByCondition(ConditionWrapper<T> wrapper) throws Exception;
    long selectCount(ConditionWrapper<T> wrapper) throws Exception;
    Object selectObj(ConditionWrapper<T> wrapper) throws Exception;
    List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception;
    Map<String, Object> selectMap(ConditionWrapper<T> wrapper) throws Exception;
    List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper) throws Exception;
    DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper) throws Exception;

    /*------------------------------------ delete ---------------------------------------*/
    int deleteByKey(P key) throws Exception;
    int deleteBatchKeys(Collection<P> keys) throws Exception;
    int deleteByCondition(ConditionWrapper<T> wrapper) throws Exception;

    /*------------------------------------ insert ---------------------------------------*/
    int insert(T t) throws Exception;
    int insert(List<T> tList) throws Exception;

    /*------------------------------------ update ---------------------------------------*/
    int updateByKey(T t) throws Exception;
    int updateByKey(T t, SFunction<T, ?>... updateColumns) throws Exception;
    int updateByCondition(T t, ConditionWrapper<T> wrapper) throws Exception;

    /*------------------------------------ comm ---------------------------------------*/
    long save(T t) throws Exception;
    Object primaryKeyValue(T entity);


}
