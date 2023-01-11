package com.custom.jdbc.executor;

import com.custom.jdbc.interfaces.CustomSqlSession;

import java.util.List;
import java.util.Map;
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
    <T> List<T> selectList(CustomSqlSession sqlSession) throws Exception;

    /**
     * 查询单条记录
     */
    <T> T selectOne(CustomSqlSession sqlSession) throws Exception;

    /**
     * 查询单个字段的多结果集(Set)
     */
    <T> Set<T> selectSet(CustomSqlSession sqlSession) throws Exception;

    /**
     * 返回查询后的第一个字段，也就是说该方法只支持查询一个字段
     */
    <T> List<T> selectObjs(CustomSqlSession sqlSession) throws Exception;

    /**
     * 返回查询后的第一个字段，也就是说该方法只支持查询一个字段，并只返回该结果集的第一个值
     */
     Object selectObj(CustomSqlSession sqlSession) throws Exception;

    /**
     * 查询多条记录映射到Map
     */
    <V> List<Map<String, V>> selectListMap(CustomSqlSession sqlSession) throws Exception;

    /**
     * 查询一条记录映射到Map
     */
    <V> Map<String, V> selectOneMap(CustomSqlSession sqlSession) throws Exception;

    /**
     * 查询多条单次映射关系
     * 该方法与 {@link #selectOneMap} 略有不同，该方法返回的map内只有一个键值对的映射
     * <br/> 可用于一些聚合查询后的结果映射
     * 1.例如: 查询每个名字的使用数量 结果如下
     * <tr>
     *     <th>name</th>
     *     <th>count</th>
     * </tr>
     * <tr>
     *     <td>zhangsan</td>
     *     <td>999</td>
     * </tr>
     * <tr>
     *     <td>lisi</td>
     *     <td>888</td>
     * </tr>
     * <br/> key即为<b>[zhangsan]</b>, value为<b>[999]</b>
     *
     * <br/><p></p>
     * 2.例如: 查询每个班的平均分
     * <tr>
     *     <th>class</th>
     *     <th>avgScore</th>
     * </tr>
     * <tr>
     *     <td>c01</td>
     *     <td>78.5</td>
     * </tr>
     * <tr>
     *     <td>c02</td>
     *     <td>86.5</td>
     * </tr>
     * <br/> key即为<b>[c01]</b>, value为<b>[78.5]</b>
     */
    <K, V> Map<K, V> selectMap(CustomSqlSession sqlSession) throws Exception;

    /**
     * 查询数组(T类型不可以是基础类型，只能是基础类型对应的包装类)
     */
    <T> T[] selectArrays(CustomSqlSession sqlSession) throws Exception;


    /**
     * 通用添加、修改、删除
     */
    <T> int executeUpdate(CustomSqlSession sqlSession) throws Exception;

    /**
     * 插入记录，并为参数中的dataList自动生成主键值
     */
    <T> int executeSave(CustomSqlSession sqlSession) throws Exception;

    /**
     * 表结构相关操作，
     * 执行表(字段)结构创建或删除
     */
    void execTableInfo(CustomSqlSession sqlSession) throws Exception;




}
