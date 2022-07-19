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
    DbPageRows<T> selectPageRows(ConditionWrapper<T> wrapper);
    List<T> selectList(ConditionWrapper<T> wrapper);
    T selectOneByCondition(ConditionWrapper<T> wrapper);
    long selectCount(ConditionWrapper<T> wrapper);
    Object selectObj(ConditionWrapper<T> wrapper);
    List<Object> selectObjs(ConditionWrapper<T> wrapper);
    Map<String, Object> selectMap(ConditionWrapper<T> wrapper);
    List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper);
    DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper);

    /*------------------------------------ delete ---------------------------------------*/
    int deleteByKey(P key);
    int deleteBatchKeys(Collection<P> keys);
    int deleteByCondition(ConditionWrapper<T> wrapper);

    /*------------------------------------ insert ---------------------------------------*/
    int insert(T t);
    int insert(List<T> tList);

    /*------------------------------------ update ---------------------------------------*/
    int updateByKey(T t);
    int updateByKey(T t, SFunction<T, ?>... updateColumns);
    int updateByCondition(T t, ConditionWrapper<T> wrapper);

    /*------------------------------------ comm ---------------------------------------*/
    int save(T t);
    P primaryKeyValue(T entity);


}
