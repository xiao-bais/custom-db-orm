package com.custom.jdbc.executor;

import com.custom.jdbc.session.CustomSqlSession;

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
     * 查询单个字段的单个值
     */
    <T> List<T> selectObjs(CustomSqlSession sqlSession) throws Exception;

    /**
     * 查询单个字段的单个值
     */
     Object selectObj(CustomSqlSession sqlSession) throws Exception;

    /**
     * 查询多条记录映射到Map
     */
    List<Map<String, Object>> selectListMap(CustomSqlSession sqlSession) throws Exception;

    /**
     * 查询一条记录映射到Map
     */
    Map<String, Object> selectOneMap(CustomSqlSession sqlSession) throws Exception;

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
    <K, V> List<Map<K, V>> selectMaps(CustomSqlSession sqlSession) throws Exception;




}
