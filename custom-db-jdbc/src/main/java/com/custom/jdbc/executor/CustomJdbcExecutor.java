package com.custom.jdbc.executor;

import com.custom.jdbc.session.CustomSqlSession;

import java.util.List;
import java.util.Set;

/**
 * @author Xiao-Bai
 * @date 2022/10/23 18:51
 * @desc
 */
public interface CustomJdbcExecutor {

    /**
     * 查询多条记录（通用型）
     */
    <T> List<T> selectList(CustomSqlSession<T> sqlSession) throws Exception;

    /**
     * 查询单条记录
     */
    <T> T selectOne(CustomSqlSession<T> sqlSession) throws Exception;

    /**
     * 查询单个字段的多结果集(Set)
     */
    <T> Set<T> selectSet(CustomSqlSession<T> sqlSession) throws Exception;

    /**
     * 查询单个字段的单个值
     */
    <T> List<T> selectObjs(CustomSqlSession<T> sqlSession) throws Exception;

    /**
     * 查询单个字段的单个值
     */
    <T> Object selectObj(CustomSqlSession<T> sqlSession) throws Exception;

}
