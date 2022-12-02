package com.custom.action.core;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.ConditionWrapper;
import com.custom.comm.page.DbPageRows;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/20 0020 14:43
 * @Desc 最终对外的JDBC基础操作对象
 */
public interface JdbcDao {

    /* ----------------------------------------------------------------select---------------------------------------------------------------- */

    /**
     * 根据条件查询多条记录: 例（and a.name = ?）
     */
    <T> List<T> selectList(Class<T> t, String condition, Object... params);

    /**
     * 根据多个主键查询多条记录
     */
    <T> List<T> selectBatchKeys(Class<T> t, Collection<? extends Serializable> keys);

    /**
     * 根据sql查询多条记录: 例（select * from table ）
     */
    <T> List<T> selectListBySql(Class<T> t, String sql, Object... params);

    /**
     * 根据条件进行分页查询: 例（and a.name = ?）
     */
    <T> DbPageRows<T> selectPage(Class<T> t, String condition, DbPageRows<T> dbPageRows, Object... params);

    /**
     * 根据主键查询一条记录
     */
    <T> T selectByKey(Class<T> t, Serializable key);

    /**
     * 纯sql查询一条记录
     */
    <T> T selectOneBySql(Class<T> t, String sql, Object... params);

    /**
     * 纯sql查询单个值
     */
    Object selectObjBySql(String sql, Object... params);

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
     * <p></p>
     * @param kClass key的类型
     * @param vClass value的类型
     * @param sql 执行的sql
     * @param params 参数
     */
    <K, V> Map<K, V> selectMap(Class<K> kClass, Class<V> vClass, String sql, Object... params);

    /**
     * 根据条件查询一条记录
     * @param condition and a.name = ?
     * @param params "zhangsan"
     */
    <T> T selectOne(Class<T> t, String condition, Object... params);


    /************************************************  条件构造器  *****************************************************/

    /**
     * 条件构造器查询-分页查询
     */
    <T> DbPageRows<T> selectPage(ConditionWrapper<T> wrapper);

    /**
     * 条件构造器查询-查询多个
     */
    <T> List<T> selectList(ConditionWrapper<T> wrapper);

    /**
     * 条件构造器查询-查询单个对象
     */
    <T> T selectOne(ConditionWrapper<T> wrapper);

    /**
     * 条件构造器查询-查询数量
     */
    <T> long selectCount(ConditionWrapper<T> wrapper);

    /**
     * 条件构造器查询单个属性值（若有多个值满足条件，默认返回第一条记录的第一个值）
     */
    <T> Object selectObj(ConditionWrapper<T> wrapper);

    /**
     * 条件构造器查询多个单值（若有多条记录满足条件，默认返回所有记录的第一个字段）
     */
    <T> List<Object> selectObjs(ConditionWrapper<T> wrapper);

    /**
     * 查询单条记录映射到Map
     */
    <T> Map<String, Object> selectOneMap(ConditionWrapper<T> wrapper);

    /**
     * 查询多条记录映射到Map
     */
    <T> List<Map<String, Object>> selectListMap(ConditionWrapper<T> wrapper);

    /**
     * 查询多条记录映射到Map并分页
     */
    <T> DbPageRows<Map<String, Object>> selectPageMap(ConditionWrapper<T> wrapper);

    /**
     * 查询双列结果映射到map的K与V
     * @see #selectMap(Class, Class, String, Object...)
     */
    <T, K, V> Map<K, V> selectMap(ConditionWrapper<T> wrapper, Class<K> kClass, Class<V> vClass);

    /**
     * 查询数组（t只支持基础类型对应的引用类型）
     * <p>
     *  额外支持
     *  {@link java.lang.String}
     *  {@link java.math.BigDecimal}
     *  {@link java.util.Date}
     * </p>
     */
    <T> T[] selectArrays(Class<T> t, String sql, Object... params);

    /**
     * 查询单条记录，!= null 的实体属性即为条件(全等查询)
     */
    <T> T selectOne(T entity);

    /**
     * 查询多条记录，!= null 的实体属性即为条件(全等查询)
     */
    <T> List<T> selectList(T entity);

    /**
     * 查询多条记录并分页，!= null 的实体属性即为条件(全等查询)
     */
    <T> DbPageRows<T> selectPage(T entity, DbPageRows<T> pageRows);


    /* ----------------------------------------------------------------delete---------------------------------------------------------------- */

    /**
     * 根据主键删除一条记录
     */
    <T> int deleteByKey(Class<T> t, Serializable key);

    /**
     * 根据主键删除多条记录
     */
    <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys);

    /**
     * 根据条件删除记录
     */
    <T> int deleteByCondition(Class<T> t, String condition, Object... params);

    /**
     * 根据条件删除记录
     */
    <T> int deleteSelective(ConditionWrapper<T> wrapper);

    /* ----------------------------------------------------------------insert---------------------------------------------------------------- */

    /**
     * 插入一条记录(默认在实体中set新的主键)
     */
    <T> long insert(T entity);

    /**
     * 插入多条记录(默认在实体中set新的主键)
     */
    <T> int insertBatch(List<T> entityList);

    /* ----------------------------------------------------------------update---------------------------------------------------------------- */

    /**
     * 根据主键修改一条记录
     */
    <T> int updateByKey(T entity);

    /**
     * 根据主键修改一条记录(只修改entity中属性值 !=null 的字段)
     */
    <T> int updateByKeySelective(T entity);

    /**
     * 根据条件修改一条记录(主键不会被修改)
     */
    <T> int updateSelective(T entity, ConditionWrapper<T> wrapper);

    /**
     * 根据条件修改一条记录(只修改entity中属性值 !=null 的字段，主键不会被修改)
     */
    <T> int updateByCondition(T entity, String condition, Object... params);

    /**
     * 根据条件修改一条记录(主键不会被修改)
     * @param addNullField 是否允许修改值=null的字段
     */
    <T> int updateByCondition(T entity, boolean addNullField, String condition, Object... params);

    /**
     * 根据sql set设置器修改n条记录，
     * <p></p>
     *  示例1：Conditions.update(ChildStudent.class)
     *  .setter(x -> x.set("a.phone", "158xxxxxxxx"))
     *  .where(x -> x.eq("a.name", "张三"))
     * <p></p>
     * 示例2：Conditions.lambdaUpdate(ChildStudent.class)
     * .setter(x -> x.set(ChildStudent::getPhone, "158xxxxxxxx"))
     * .where(x -> x.eq(ChildStudent::getName, "张三"))
     * <p></p>
     * 注意：在同一条链式调用中，setter方法以及where方法若存在多次调用，则以最后一个为准.
     * 因为储存的对象只存在一个，不会累加上一次的结果，只会被覆盖
     * @see com.custom.action.condition.Conditions#update(Class)
     * @see com.custom.action.condition.Conditions#lambdaUpdate(Class)
     */
    <T> int updateSelective(AbstractUpdateSet<T> updateSet);

    /* ----------------------------------------------------------------common---------------------------------------------------------------- */

    /**
     * 保存一条记录（根据主键添加或修改）
     */
    <T> int save(T entity);

    /**
     * 执行一条sql（增删改）
     */
    <T> int executeSql(String sql, Object... params);

    /**
     * 删除表
     */
    void dropTables(Class<?>... arr);

    /**
     * 创建表
     */
    void createTables(Class<?>... arr);


}
