package com.custom.action.core;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.proxy.JdbcActionProxy;
import com.custom.action.condition.ConditionWrapper;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Xiao-Bai
 * @date 2022/4/17 21:31
 * @desc: 最终对外的JDBC基础操作对象
 */
public class JdbcOpDao {

    /* ----------------------------------------------------------------select---------------------------------------------------------------- */

    /**
     * 根据条件查询多条记录: 例（and a.name = ?）
     */
    public <T> List<T> selectList(Class<T> t, String condition, Object... params) {
        return jdbcAction.selectList(t, condition, params);
    }

    /**
     * 根据多个主键查询多条记录
     */
    public <T> List<T> selectBatchKeys(Class<T> t, Collection<? extends Serializable> keys) {
        return jdbcAction.selectBatchKeys(t, keys);
    }

    /**
     * 根据sql查询多条记录: 例（select * from table ）
     */
    public <T> List<T> selectListBySql(Class<T> t, String sql, Object... params) {
        return jdbcAction.selectListBySql(t, sql, params);
    }

    /**
     * 根据条件进行分页查询: 例（and a.name = ?）
     */
    public <T> DbPageRows<T> selectPage(Class<T> t, String condition, DbPageRows<T> dbPageRows, Object... params) {
        return jdbcAction.selectPage(t, condition, null, dbPageRows, params);
    }

    /**
     * 根据主键查询一条记录
     */
    public <T> T selectByKey(Class<T> t, Serializable key) {
        return jdbcAction.selectByKey(t, key);
    }

    /**
     * 纯sql查询一条记录
     */
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) {
        return jdbcAction.selectOneBySql(t, sql, params);
    }

    /**
     * 纯sql查询单个值
     */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        return jdbcAction.selectObjBySql(sql, params);
    }

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
    public <K, V> Map<K, V> selectMap(Class<K> kClass, Class<V> vClass, String sql, Object... params) {
        return jdbcAction.selectMap(kClass, vClass, sql, params);
    }


    /**
     * 根据条件查询一条记录
     * @param condition and a.name = ?
     * @param params "zhangsan"
     */
    public <T> T selectOne(Class<T> t, String condition, Object... params) {
        return jdbcAction.selectOne(t, condition, params);
    }

    /**
     * 条件构造器查询-分页查询
     */
    public <T> DbPageRows<T> selectPage(ConditionWrapper<T> wrapper) {
        return jdbcAction.selectPage(wrapper);
    }

    /**
     * 条件构造器查询-查询多个
     */
    public <T> List<T> selectList(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectList(wrapper);
    }

    /**
     * 条件构造器查询-查询单个对象
     */
    public <T> T selectOne(ConditionWrapper<T> wrapper) {
        return jdbcAction.selectOne(wrapper);
    }

    /**
     * 条件构造器查询-查询数量
     */
    public <T> long selectCount(ConditionWrapper<T> wrapper) {
        return jdbcAction.selectCount(wrapper);
    }

    /**
     * 条件构造器查询单个属性值（若有多个值满足条件，默认返回第一条记录的第一个值）
     */
    public <T> Object selectObj(ConditionWrapper<T> wrapper) {
        return jdbcAction.selectObj(wrapper);
    }
    /**
     * 条件构造器查询多个单值（若有多条记录满足条件，默认返回所有记录的第一个字段）
     */
    public <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) {
        return jdbcAction.selectObjs(wrapper);
    }

    /**
     * 查询单条记录映射到Map
     */
    public <T> Map<String, Object> selectOneMap(ConditionWrapper<T> wrapper) {
        return jdbcAction.selectOneMap(wrapper);
    }

    /**
     * 查询多条记录映射到Map
     */
    public <T> List<Map<String, Object>> selectListMap(ConditionWrapper<T> wrapper) {
        return jdbcAction.selectListMap(wrapper);
    }

    /**
     * 查询多条记录映射到Map
     */
    public <T> DbPageRows<Map<String, Object>> selectPageMap(ConditionWrapper<T> wrapper) {
        return jdbcAction.selectPageMap(wrapper);
    }

    /**
     * 查询双列结果映射到map的K与V
     * @see #selectMap(Class, Class, String, Object...)
     */
    public <T, K, V> Map<K, V> selectMap(ConditionWrapper<T> wrapper, Class<K> kClass, Class<V> vClass) {
        return jdbcAction.selectMap(wrapper, kClass, vClass);
    }

    /**
     * 查询数组（t只支持基础类型对应的引用类型）
     * <p>
     *  额外支持
     *  {@link java.lang.String}
     *  {@link java.math.BigDecimal}
     *  {@link java.util.Date}
     * </p>
     */
    public <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception {
        return jdbcAction.selectArrays(t, sql, params);
    }

    /**
     * 查询单条记录，!= null 的实体属性即为条件(全等查询)
     */
    public <T> T selectOne(T entity) {
        return jdbcAction.selectOne(entity);
    }

    /**
     * 查询多条记录，!= null 的实体属性即为条件(全等查询)
     */
    public <T> List<T> selectList(T entity) {
        return jdbcAction.selectList(entity);
    }

    /**
     * 查询多条记录并分页，!= null 的实体属性即为条件(全等查询)
     */
    public <T> DbPageRows<T> selectPage(T entity, DbPageRows<T> pageRows) {
        return jdbcAction.selectPage(entity, pageRows);
    }

    /* ----------------------------------------------------------------delete---------------------------------------------------------------- */

    /**
     * 根据主键删除一条记录
     */
    public <T> int deleteByKey(Class<T> t, Serializable key) {
        return jdbcAction.deleteByKey(t, key);
    }

    /**
     * 根据主键删除多条记录
     */
    public <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) {
        return jdbcAction.deleteBatchKeys(t, keys);
    }

    /**
     * 根据条件删除记录
     */
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) {
        return jdbcAction.deleteByCondition(t, condition, params);
    }

    /**
     * 根据条件删除记录
     */
    public <T> int deleteSelective(ConditionWrapper<T> wrapper) {
        return jdbcAction.deleteSelective(wrapper);
    }

    /* ----------------------------------------------------------------insert---------------------------------------------------------------- */

    /**
     * 插入一条记录(默认在实体中set新的主键)
     */
    public <T> long insert(T entity) {
        return jdbcAction.insert(entity);
    }

    /**
     * 插入多条记录(默认在实体中set新的主键)
     */
    public <T> int insertBatch(List<T> entityList) {
        return jdbcAction.insertBatch(entityList);
    }


    /* ----------------------------------------------------------------update---------------------------------------------------------------- */

    /**
     * 根据主键修改一条记录
     */
    public <T> int updateByKey(T entity) {
        return jdbcAction.updateByKey(entity);
    }

    /**
     * 根据主键修改一条记录(只修改entity中属性值 !=null 的字段)
     */
    public <T> int updateByKeySelective(T entity) {
        return jdbcAction.updateByKeySelective(entity);
    }

    /**
     * 根据条件修改一条记录
     */
    public <T> int updateSelective(T entity, ConditionWrapper<T> wrapper) {
        return jdbcAction.updateSelective(entity, wrapper);
    }

    /**
     * 根据条件修改一条记录
     */
    public <T> int updateByCondition(T entity, String condition, Object... params) {
        return jdbcAction.updateByCondition(entity, false, condition, params);
    }

    public <T> int updateByCondition(T entity, boolean addNullField, String condition, Object... params) {
        return jdbcAction.updateByCondition(entity, addNullField, condition, params);
    } 

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
     */
    public <T> int updateSelective(AbstractUpdateSet<T> updateSet) {
        return jdbcAction.updateSelective(updateSet);
    }

    /* ----------------------------------------------------------------common---------------------------------------------------------------- */

    /**
     * 保存一条记录（根据主键添加或修改）
     */
    public <T> int save(T entity) {
        return jdbcAction.save(entity);
    }

    /**
     * 执行一条sql（增删改）
     */
    public <T> int executeSql(String sql, Object... params) {
        return jdbcAction.executeSql(sql, params);
    }

    /**
     * 删除表
     */
    public final void dropTables(Class<?>... arr) throws Exception {
        jdbcAction.dropTables(arr);
    }

    /**
     * 创建表
     */
    public final void createTables(Class<?>... arr) {
        jdbcAction.createTables(arr);
    }

    private final AbstractSqlExecutor jdbcAction;
    private final DbDataSource dbDataSource;
    private final DbCustomStrategy dbCustomStrategy;

    public JdbcOpDao(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        this.dbDataSource = dbDataSource;
        this.dbCustomStrategy = dbCustomStrategy;
        jdbcAction = new JdbcActionProxy(new JdbcAction(), dbDataSource, dbCustomStrategy).createProxy();
    }

    public DbDataSource getDbDataSource() {
        return dbDataSource;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }
}