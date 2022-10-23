package com.custom.action.dbaction;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.executor.JdbcWrapperExecutor;
import com.custom.action.sqlparser.HandleSelectSqlBuilder;
import com.custom.action.sqlparser.MappingResultInjector;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.util.DbUtil;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.page.DbPageRows;

import java.io.Serializable;
import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2021/12/8 14:49
 * @desc：方法执行处理抽象入口
 **/
public abstract class AbstractSqlExecutor extends JdbcWrapperExecutor {

    /*--------------------------------------- select ---------------------------------------*/
    public abstract <T> List<T> selectList(Class<T> entityClass, String condition, Object... params);
    public abstract <T> List<T> selectListBySql(Class<T> entityClass, String sql, Object... params);
    public abstract <T> DbPageRows<T> selectPage(Class<T> entityClass, String condition, DbPageRows<T> dbPageRows, Object... params);
    public abstract <T> T selectByKey(Class<T> entityClass, Serializable key);
    public abstract <T> List<T> selectBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys);
    public abstract <T> T selectOne(Class<T> entityClass, String condition, Object... params);
    public abstract <T> T selectOneBySql(Class<T> entityClass, String sql, Object... params);
    public abstract <T> T selectOne(T entity);
    public abstract <T> List<T> selectList(T entity);
    public abstract <T> DbPageRows<T> selectPage(T entity, DbPageRows<T> pageRows);

    /**
     * ConditionWrapper(条件构造器)
     */
    public abstract <T> DbPageRows<T> selectPage(ConditionWrapper<T> wrapper);
    public abstract <T> List<T> selectList(ConditionWrapper<T> wrapper);
    public abstract <T> T selectOne(ConditionWrapper<T> wrapper);
    public abstract <T> long selectCount(ConditionWrapper<T> wrapper);
    public abstract <T> Object selectObj(ConditionWrapper<T> wrapper);
    public abstract <T> List<Object> selectObjs(ConditionWrapper<T> wrapper);
    public abstract <T> Map<String, Object> selectMap(ConditionWrapper<T> wrapper);
    public abstract <T> List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper);
    public abstract <T> DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper);


    /*--------------------------------------- delete ---------------------------------------*/
    public abstract <T> int deleteByKey(Class<T> entityClass, Serializable key);
    public abstract <T> int deleteBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys);
    public abstract <T> int deleteByCondition(Class<T> entityClass, String condition, Object... params);
    public abstract <T> int deleteSelective(ConditionWrapper<T> wrapper);

    /*--------------------------------------- insert ---------------------------------------*/
    public abstract <T> int insert(T entity);
    public abstract <T> int insertBatch(List<T> tList);

    /*--------------------------------------- update ---------------------------------------*/
    public abstract <T> int updateByKey(T entity);
    public abstract <T> int updateSelective(T entity, ConditionWrapper<T> wrapper);
    public abstract <T> int updateByCondition(T entity, String condition, Object... params);

    /**
     * updateSet sql set设置器
     */
    public abstract <T> int updateSelective(AbstractUpdateSet<T> updateSet);

    /*--------------------------------------- comm ---------------------------------------*/
    public abstract <T> int save(T entity);
    public abstract int executeSql(String sql, Object... params);
    public abstract void createTables(Class<?>... arr);
    public abstract void dropTables(Class<?>... arr);

    /**
     * 处理异常抛出, 其实该方法只是做了一个隐式的异常抛出，没有别的作用
     */
    public void throwsException(Exception e) {
        if (e instanceof CustomCheckException) {
            throw (CustomCheckException) e;
        }
        else if (e instanceof NullPointerException) {
            throw (NullPointerException) e;
        }
        else if (e instanceof UnsupportedOperationException) {
            throw (UnsupportedOperationException) e;
        }
        else if (e instanceof IllegalArgumentException) {
            throw (IllegalArgumentException) e;
        }
        throw new RuntimeException(e.toString(), e);
    }


    /**
     * 查询后一对一结果注入
     */
    protected <T> void injectOtherResult(Class<T> entityClass, HandleSelectSqlBuilder<T> sqlBuilder, T result) throws Exception {
        if (sqlBuilder.isExistNeedInjectResult() && result != null) {
            MappingResultInjector<T> resultInjector = new MappingResultInjector<>(entityClass, this);
            resultInjector.injectorValue(Collections.singletonList(result));
        }
    }

    /**
     * 查询后一对多结果注入
     */
    protected <T> void injectOtherResult(Class<T> entityClass, HandleSelectSqlBuilder<T> sqlBuilder, List<T> result) throws Exception {
        if (sqlBuilder.isExistNeedInjectResult()) {
            MappingResultInjector<T> resultInjector = new MappingResultInjector<>(entityClass, this);
            resultInjector.injectorValue(result);
        }
    }

    /**
     * 分页数据整合
     */
    protected <T> void buildPageResult(Class<T> t, String selectSql, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        List<T> dataList = new ArrayList<>();
        long count = (long) selectObjBySql(String.format(DbUtil.SELECT_COUNT_TEMPLATE, selectSql), params);
        if (count > 0) {
            selectSql = dbPageRows.getPageIndex() == 1 ?
                    String.format("%s \nLIMIT %s", selectSql, dbPageRows.getPageSize())
                    : String.format("%s \nLIMIT %s, %s", selectSql, (dbPageRows.getPageIndex() - 1) * dbPageRows.getPageSize(), dbPageRows.getPageSize());
            dataList = selectBySql(t, selectSql, params);
        }
        dbPageRows.setTotal(count).setData(dataList);
    }
}
