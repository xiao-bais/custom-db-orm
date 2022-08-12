package com.custom.action.interfaces;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.condition.SFunction;
import com.custom.comm.BasicDao;
import com.custom.comm.page.DbPageRows;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/13 2:45
 * @Desc 单个表的指定通用DAO访问即可
 * T - 实体类型
 * Primary - 主键类型
 */
public interface BaseEntityDao<T, Primary> extends BasicDao {

    /* ----------------------------------------------------------------select---------------------------------------------------------------- */

    /**
     * 根据条件查询多条记录: 例（and a.name = ?）
     */
    List<T> selectList(String condition, Object... params);

    /**
     * 根据多个主键查询多条记录
     */
    List<T> selectBatchKeys(Collection<Primary> keys);

    /**
     * 根据条件进行分页查询: 例（and a.name = ?）
     */
    DbPageRows<T> selectPage(String condition, DbPageRows<T> dbPageRows, Object... params);

    /**
     * 根据主键查询一条记录
     */
    T selectByKey(Primary key);

    /**
     * 根据条件查询一条记录
     * @param condition and a.name = ?
     * @param params "zhangsan"
     */
    T selectOne(String condition, Object... params);

    /**
     * 条件构造器查询-分页查询
     */
    DbPageRows<T> selectPageRows(ConditionWrapper<T> wrapper);

    /**
     * 条件构造器查询-查询多个
     */
    List<T> selectList(ConditionWrapper<T> wrapper);

    /**
     * 条件构造器查询-查询单个对象
     */
    T selectOne(ConditionWrapper<T> wrapper);

    /**
     * 条件构造器查询-查询数量
     */
    long selectCount(ConditionWrapper<T> wrapper);

    /**
     * 条件构造器查询单个属性值（若有多个值满足条件，默认返回第一条记录的第一个值）
     */
    Object selectObj(ConditionWrapper<T> wrapper);

    /**
     * 条件构造器查询多个单值（若有多条记录满足条件，默认返回所有记录的第一个字段）
     */
    List<Object> selectObjs(ConditionWrapper<T> wrapper);

    /**
     * 查询单条记录映射到Map
     */
    Map<String, Object> selectMap(ConditionWrapper<T> wrapper);

    /**
     * 查询多条记录映射到Map
     */
    List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper);

    /**
     * 查询多条记录映射到Map并分页
     */
    DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper);

    /**
     * 查询单条记录，!= null 的实体属性即为条件
     */
    T selectOne(T entity);

    /**
     * 查询多条记录，!= null 的实体属性即为条件
     */
    List<T> selectList(T entity);

    /**
     * 查询多条记录并分页，!= null 的实体属性即为条件
     */
    DbPageRows<T> selectPage(T entity, DbPageRows<T> pageRows);


    /* ----------------------------------------------------------------delete---------------------------------------------------------------- */

    /**
     * 根据主键删除一条记录
     */
    int deleteByKey(Primary key);

    /**
     * 根据主键删除多条记录
     */
    int deleteBatchKeys(Collection<Primary> keys);

    /**
     * 根据条件删除记录
     */
    int deleteByCondition(String condition, Object... params);

    /**
     * 根据条件删除记录
     */
    int deleteSelective(ConditionWrapper<T> wrapper);

    /* ----------------------------------------------------------------insert---------------------------------------------------------------- */

    /**
     * 插入一条记录(默认在实体中set新的主键)
     */
    long insert(T entity);

    /**
     * 插入多条记录(默认在实体中set新的主键)
     */
    int insertBatch(List<T> entityList);

    /* ----------------------------------------------------------------update---------------------------------------------------------------- */

    /**
     * 根据主键修改一条记录（updateColumns：指定要修改的表字段，否则则修改全部 !=null 的字段）
     */
    int updateColumnByKey(T entity, Consumer<List<SFunction<T, ?>>> updateColumns);

    /**
     * 根据主键修改一条记录
     */
    int updateByKey(T entity);

    /**
     * 根据条件修改一条记录(除主键不会修改)
     */
    int updateSelective(T entity, ConditionWrapper<T> wrapper);

    /**
     * 根据条件修改一条记录
     */
    int updateByCondition(T entity, String condition, Object... params);

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
    int updateSelective(AbstractUpdateSet<T> updateSet);

    /* ----------------------------------------------------------------common---------------------------------------------------------------- */

    /**
     * 保存一条记录（根据主键添加或修改）
     */
    long save(T entity);

}
