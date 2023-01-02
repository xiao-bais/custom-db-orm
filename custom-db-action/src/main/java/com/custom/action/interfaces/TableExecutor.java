package com.custom.action.interfaces;

import com.custom.action.condition.ConditionWrapper;
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
public interface TableExecutor<T, P extends Serializable> {

    /*--------------------------------------- select ---------------------------------------*/
    DbPageRows<T> selectPage(ConditionWrapper<T> wrapper) throws Exception;
    List<T> selectList(ConditionWrapper<T> wrapper) throws Exception;
    T selectOne(ConditionWrapper<T> wrapper) throws Exception;
    long selectCount(ConditionWrapper<T> wrapper) throws Exception;
    Object selectObj(ConditionWrapper<T> wrapper) throws Exception;
    List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception;
    Map<String, Object> selectMap(ConditionWrapper<T> wrapper) throws Exception;
    List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper) throws Exception;
    DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper) throws Exception;

    /*------------------------------------ delete ---------------------------------------*/
    int deleteByKey(P key) throws Exception;
    int deleteBatchKeys(Collection<P> keys) throws Exception;
    int deleteSelective(ConditionWrapper<T> wrapper) throws Exception;

    /*------------------------------------ insert ---------------------------------------*/
    int insert(T t) throws Exception;
    int insert(List<T> tList) throws Exception;

    /*------------------------------------ update ---------------------------------------*/
    int updateByKey(T t) throws Exception;
    int updateSelective(T t, ConditionWrapper<T> wrapper) throws Exception;

    /*------------------------------------ comm ---------------------------------------*/
    int save(T t) throws Exception;
    P primaryKeyValue(T entity);


}
