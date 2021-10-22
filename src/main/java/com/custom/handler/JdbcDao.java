package com.custom.handler;

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
        return buildSqlHandler.selectList(t, condition, null, params);
    }

    /**
     * 根据条件查询多条记录并排序：例（orderBy: id desc）
     */
    public <T> List<T> selectList(Class<T> t, String condition, String orderBy, Object... params) throws Exception {
        return buildSqlHandler.selectList(t, condition, orderBy, params);
    }

    /**
     * 根据sql查询多条记录: 例（select * from table ）
     */
    public <T> List<T> selectListBySql(Class<T> t, String sql, Object... params) throws Exception {
        return buildSqlHandler.selectBySql(t, sql, params);
    }

    /**
     * 根据条件进行分页查询: 例（and a.name = ?）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, int pageIndex, int pageSize, Object... params) throws Exception {
        return buildSqlHandler.selectPageRows(t, condition, null, pageIndex, pageSize, params);
    }

    /**
     * 根据条件进行分页查询并排序: 例（and a.name = ? orderBy: id desc）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, int pageIndex, int pageSize, String orderBy, Object... params) throws Exception {
        return buildSqlHandler.selectPageRows(t, condition, orderBy, pageIndex, pageSize, params);
    }

    /**
     * 根据条件进行分页查询: 例（and a.name = ?）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        return buildSqlHandler.selectPageRows(t, condition, null, dbPageRows, params);
    }

    /**
     * 根据条件进行分页查询并排序: 例（and a.name = ? orderBy: id desc）
     */
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, DbPageRows<T> dbPageRows, String orderBy, Object... params) throws Exception {
        return buildSqlHandler.selectPageRows(t, condition, orderBy, dbPageRows, params);
    }

    /**
     * 根据主键查询一条记录
     */
    public <T> T selectOneByKey(Class<T> t, Object key) throws Exception {
        return buildSqlHandler.selectOneByKey(t, key);
    }

    /**
     * 纯sql查询一条记录
     */
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception {
        return buildSqlHandler.selectOneBySql(t, sql, params);
    }

    /**
     * 根据条件查询一条记录
     */
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception {
        return buildSqlHandler.selectOneByCondition(t, condition, params);
    }

    /* ----------------------------------------------------------------delete---------------------------------------------------------------- */

    /**
     * 根据主键删除一条记录
     */
    public <T> int deleteByKey(Class<T> t, Object key) throws Exception {
        return buildSqlHandler.deleteByKey(t, key);
    }

    /**
     * 根据主键删除多条记录
     */
    public <T> int deleteBatchKeys(Class<T> t, Object[] keys) throws Exception {
        return buildSqlHandler.deleteBatchKeys(t, keys);
    }

    /**
     * 根据条件删除记录
     */
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception {
        return buildSqlHandler.deleteByCondition(t, condition, params);
    }

    /* ----------------------------------------------------------------insert---------------------------------------------------------------- */

    /**
     * 插入一条记录
     */
    public <T> long insert(T t) throws Exception {
        return buildSqlHandler.insert(t, false);
    }

    /**
     * 插入一条记录并返回新的主键（只允许自增主键类型）
     */
    public <T> int insertGenerateKey(T t) throws Exception {
        return buildSqlHandler.insert(t, true);
    }

    /**
     * 插入多条记录
     */
    public <T> int insert(List<T> tList) throws Exception {
        return buildSqlHandler.insert(tList, false);
    }

    /**
     * 插入多条记录并返回新的主键（只允许自增主键类型）
     */
    public <T> int insertGenerateKey(List<T> tList) throws Exception {
        return buildSqlHandler.insert(tList, true);
    }

    /* ----------------------------------------------------------------update---------------------------------------------------------------- */

    /**
     * 根据主键修改一条记录（updateFields：指定要修改的表字段  为空则修改全部字段）
     */
    public <T> int updateByKey(T t, String... updateDbFields) throws Exception {
        return buildSqlHandler.updateByKey(t, updateDbFields);
    }

    /**
     * 根据主键修改一条记录
     */
    public <T> int updateByKey(T t) throws Exception {
        return buildSqlHandler.updateByKey(t);
    }

    /* ----------------------------------------------------------------common---------------------------------------------------------------- */

    /**
     * 保存一条记录（根据主键添加或修改）
     */
    public <T> long save(T t) throws Exception {
        return buildSqlHandler.save(t);
    }

    /**
     * 删除表
     */
    public final void dropTables(Class<?>... arr) throws Exception{
        buildTableHandler.dropTables(arr);
    }

    /**
     * 创建表
     */
    public final void createTables(Class<?>... arr) throws Exception{
        buildTableHandler.createTables(arr);
    }


    private BuildSqlHandler buildSqlHandler;
    private BuildTableHandler buildTableHandler;

    public JdbcDao setDbDataSource(DbDataSource dbDataSource) {
        return new JdbcDao(dbDataSource);
    }

    public JdbcDao(DbDataSource dbDataSource){
        buildSqlHandler = new BuildSqlHandler(dbDataSource);
        buildTableHandler = new BuildTableHandler(dbDataSource);
    }


}
