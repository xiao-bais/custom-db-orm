package com.custom.sqlparser;

import com.custom.comm.page.DbPageRows;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.handler.BuildSqlHandler;
import com.custom.proxy.SqlParamsCheckProxy;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2021/12/10 21:20
 * @desc: 自定义jdbc通用操作类
 */
public class CustomDao {

    /* ----------------------------------------------------------------select---------------------------------------------------------------- */

    /**
     * 根据条件查询多条记录: 例（and a.name = ?）
     */
    public <T> List<T> selectList(Class<T> t, String condition, Object... params) throws Exception {
        return jdbcAction.selectList(t, condition, null, params);
    }

    /**
     * 根据条件查询多条记录并排序：例（orderBy: id desc）
     */
    public <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception {
        return jdbcAction.selectList(t, condition, orderBy, params);
    }

    /**
     * 根据多个主键查询多条记录
     */
    public <T> List<T> selectListByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        return jdbcAction.selectBatchByKeys(t, keys);
    }

    /**
     * 根据sql查询多条记录: 例（select * from table ）
     */
    public <T> List<T> selectListBySql(Class<T> t, String sql, Object... params) throws Exception {
        return jdbcAction.selectBySql(t, sql, params);
    }

    /**
     * 根据条件进行分页查询: 例（and a.name = ?）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, int pageIndex, int pageSize, Object... params) throws Exception {
        return jdbcAction.selectPageRows(t, condition, null, pageIndex, pageSize, params);
    }

    /**
     * 根据条件进行分页查询并排序: 例（and a.name = ? orderBy: id desc）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, int pageIndex, int pageSize, String orderBy, Object... params) throws Exception {
        return jdbcAction.selectPageRows(t, condition, orderBy, pageIndex, pageSize, params);
    }

    /**
     * 根据条件进行分页查询: 例（and a.name = ?）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        return jdbcAction.selectPageRows(t, condition, null, dbPageRows, params);
    }

    /**
     * 根据条件进行分页查询并排序: 例（and a.name = ? orderBy: id desc）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, DbPageRows<T> dbPageRows, String orderBy, Object... params) throws Exception {
        return jdbcAction.selectPageRows(t, condition, orderBy, dbPageRows, params);
    }

    /**
     * 根据主键查询一条记录
     */
    public <T> T selectOneByKey(Class<T> t, Object key) throws Exception {
        return jdbcAction.selectOneByKey(t, key);
    }

    /**
     * 纯sql查询一条记录
     */
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        return jdbcAction.selectOneBySql(t, sql, params);
    }

    /**
     * 纯sql查询单个值
     */
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        return jdbcAction.selectObjBySql(sql, params);
    }

    /**
     * 根据条件查询一条记录
     */
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception {
        return jdbcAction.selectOneByCondition(t, condition, params);
    }

    /* ----------------------------------------------------------------delete---------------------------------------------------------------- */

    /**
     * 根据主键删除一条记录
     */
    public <T> int deleteByKey(Class<T> t, Object key) throws Exception {
        return jdbcAction.deleteByKey(t, key);
    }

    /**
     * 根据主键删除多条记录
     */
    public <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception {
        return jdbcAction.deleteBatchKeys(t, keys);
    }

    /**
     * 根据条件删除记录
     */
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception {
        return jdbcAction.deleteByCondition(t, condition, params);
    }

    /* ----------------------------------------------------------------insert---------------------------------------------------------------- */

    /**
     * 插入一条记录
     */
    public <T> long insert(T t) throws Exception {
        return jdbcAction.insert(t, false);
    }

    /**
     * 插入一条记录并生成新的主键（只允许自增主键类型）
     */
    public <T> int insertGenerateKey(T t) throws Exception {
        return jdbcAction.insert(t, true);
    }

    /**
     * 插入多条记录
     */
    public <T> int insert(List<T> tList) throws Exception {
        return jdbcAction.insert(tList, false);
    }

    /**
     * 插入多条记录并生成新的主键（只允许自增主键类型）
     */
    public <T> int insertGenerateKey(List<T> tList) throws Exception {
        return jdbcAction.insert(tList, true);
    }

    /* ----------------------------------------------------------------update---------------------------------------------------------------- */

    /**
     * 根据主键修改一条记录（updateFields：指定要修改的表字段  为空则修改全部字段）
     */
    public <T> int updateByKey(T t, String... updateDbFields) throws Exception {
        return jdbcAction.updateByKey(t, updateDbFields);
    }

    /**
     * 根据主键修改一条记录
     */
    public <T> int updateByKey(T t) throws Exception {
        return jdbcAction.updateByKey(t);
    }

    /* ----------------------------------------------------------------common---------------------------------------------------------------- */

    /**
     * 保存一条记录（根据主键添加或修改）
     */
    public <T> long save(T t) throws Exception {
        return jdbcAction.save(t);
    }

    /**
     * 执行一条sql（增删改）
     */
    public <T> long executeSql(String sql, Object... params) throws Exception {
        return jdbcAction.executeSql(sql, params);
    }

    /**
     * 删除表
     */
    public final void dropTables(Class<?>... arr) throws Exception{
        jdbcAction.dropTables(arr);
    }

    /**
     * 创建表
     */
    public final void createTables(Class<?>... arr) throws Exception{
        jdbcAction.createTables(arr);
    }

    private AbstractSqlBuilder jdbcAction;

    public CustomDao(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        jdbcAction = new SqlParamsCheckProxy<>(new JdbcAction(), dbDataSource, dbCustomStrategy).createProxy();
    }
    
}