package com.custom.jdbc;

import com.custom.dbconfig.DbDataSource;
import com.custom.page.DbPageRows;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/4
 * @Description jdbc通用操作类
 */
public class JdbcDao {

    /* ----------------------------------------------------------------select---------------------------------------------------------------- */

    /**
     * 根据条件查询多条记录: 例（and a.name = ?）
     */
    public <T> List<T> selectList(Class<T> t, String condition, Object... params) throws Exception {
        return jdbcTableDao.selectList(t, condition, null, params);
    }

    /**
     * 根据条件查询多条记录并排序：例（orderBy: id desc）
     */
    public <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception {
        return jdbcTableDao.selectList(t, condition, orderBy, params);
    }

    /**
     * 根据sql查询多条记录: 例（select * from score ）
     */
    public <T> List<T> selectListBySql(Class<T> t, String sql, Object... params) throws Exception {
        return jdbcTableDao.selectBySql(t, sql, params);
    }

    /**
     * 根据条件进行分页查询: 例（and a.name = ?）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, int pageIndex, int pageSize, Object... params) throws Exception {
        return jdbcTableDao.selectPageRows(t, condition, null, pageIndex, pageSize, params);
    }

    /**
     * 根据条件进行分页查询并排序: 例（and a.name = ? orderBy: id desc）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, int pageIndex, int pageSize, String orderBy, Object... params) throws Exception {
        return jdbcTableDao.selectPageRows(t, condition, orderBy, pageIndex, pageSize, params);
    }

    /**
     * 根据条件进行分页查询: 例（and a.name = ?）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        return jdbcTableDao.selectPageRows(t, condition, null, dbPageRows, params);
    }

    /**
     * 根据条件进行分页查询并排序: 例（and a.name = ? orderBy: id desc）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, DbPageRows<T> dbPageRows, String orderBy, Object... params) throws Exception {
        return jdbcTableDao.selectPageRows(t, condition, orderBy, dbPageRows, params);
    }

    /**
     * 根据主键查询一条记录
     */
    public <T> T selectOneByKey(Class<T> t, Object key) throws Exception {
        return jdbcTableDao.selectOneByKey(t, key);
    }

    /**
     * 纯sql查询一条记录
     */
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        return jdbcTableDao.selectOneBySql(t, sql, params);
    }

    /**
     * 根据条件查询一条记录
     */
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception {
        return jdbcTableDao.selectOneByCondition(t, condition, params);
    }

    /* ----------------------------------------------------------------delete---------------------------------------------------------------- */

    /**
     * 根据主键删除一条记录
     */
    public <T> int deleteByKey(Class<T> t, Object key) throws Exception {
        return jdbcTableDao.deleteByKey(t, key);
    }

    /**
     * 根据主键删除多条记录
     */
    public <T> int deleteBatchKeys(Class<T> t, Object[] keys) throws Exception {
        return jdbcTableDao.deleteBatchKeys(t, keys);
    }

    /**
     * 根据条件删除记录
     */
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception {
        return jdbcTableDao.deleteByCondition(t, condition, params);
    }

    /* ----------------------------------------------------------------insert---------------------------------------------------------------- */

    /**
     * 插入一条记录
     */
    public <T> long insert(T t) throws Exception {
        return jdbcTableDao.insert(t, false);
    }

    /**
     * 插入一条记录并返回新的主键（只允许自增主键类型）
     */
    public <T> int insertGenerateKey(T t) throws Exception {
        return jdbcTableDao.insert(t, true);
    }

    /**
     * 插入多条记录
     */
    public <T> int insert(List<T> tList) throws Exception {
        return jdbcTableDao.insert(tList, false);
    }

    /**
     * 插入多条记录并返回新的主键（只允许自增主键类型）
     */
    public <T> int insertGenerateKey(List<T> tList) throws Exception {
        return jdbcTableDao.insert(tList, true);
    }

    /* ----------------------------------------------------------------update---------------------------------------------------------------- */

    /**
     * 根据主键修改一条记录（updateFields：指定要修改的表字段  为空则修改全部字段）
     */
    public <T> int updateByKey(T t, String... updateDbFields) throws Exception {
        return jdbcTableDao.updateByKey(t, updateDbFields);
    }

    /**
     * 根据主键修改一条记录
     */
    public <T> int updateByKey(T t) throws Exception {
        return jdbcTableDao.updateByKey(t);
    }

    /* ----------------------------------------------------------------common---------------------------------------------------------------- */

    /**
     * 保存一条记录（根据主键添加或修改）
     */
    public <T> long save(T t) throws Exception {
        return jdbcTableDao.save(t);
    }

    /**
     * 删除表
     */
    @SafeVarargs
    public final void dropTables(Class<?>... arr) throws Exception{
        dbTableUtil.dropTables(arr);
    }

    /**
     * 创建表
     */
    @SafeVarargs
    public final void createTables(Class<?>... arr) throws Exception{
        dbTableUtil.createTables(arr);
    }


    private JdbcTableDao jdbcTableDao;
    private DbTableUtil dbTableUtil;

    public JdbcDao setDbDataSource(DbDataSource dbDataSource) {
        return new JdbcDao(dbDataSource);
    }

    public JdbcDao(DbDataSource dbDataSource){
        jdbcTableDao = new JdbcTableDao(dbDataSource);
        dbTableUtil = new DbTableUtil(dbDataSource);
    }


}
