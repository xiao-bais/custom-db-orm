package com.custom.action.core;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.chain.ChainWrapper;
import com.custom.action.core.methods.MethodKind;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.jdbc.interfaces.TransactionExecutor;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * jdbc操作对象
 * @author   Xiao-Bai
 * @since  2022/4/13 20:49
 */
@SuppressWarnings("unchecked")
public class JdbcAction implements SqlExecutor {

    private final CustomMappedHandler mappedHandler;

    public JdbcAction(DbDataSource dbDataSource, DbGlobalConfig globalConfig) {
        this.mappedHandler = new CustomMappedHandler(dbDataSource, globalConfig);
    }


    @Override
    public <T> List<T> selectList(Class<T> entityClass, String condition, Object... params) throws Exception {
        return (List<T>) mappedHandler.handleExecute(MethodKind.SELECT_LIST,
                entityClass, condition, params
        );
    }

    @Override
    public <T> List<T> selectListBySql(Class<T> entityClass, String sql, Object... params) throws Exception {
        return (List<T>) mappedHandler.handleExecute(MethodKind.SELECT_LIST_BY_SQL,
                entityClass, sql, params
        );
    }


    @Override
    public <T> DbPageRows<T> selectPage(Class<T> entityClass, String condition, DbPageRows<T> dbPageRows, Object... params) throws Exception {
        return (DbPageRows<T>) mappedHandler.handleExecute(MethodKind.SELECT_PAGE,
                entityClass, condition, dbPageRows, params
        );
    }

    @Override
    public <T> T selectByKey(Class<T> entityClass, Serializable key) throws Exception {
        return (T) mappedHandler.handleExecute(MethodKind.SELECT_BY_KEY,
                entityClass, key
        );
    }

    @Override
    public <T> List<T> selectBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys) throws Exception {
        return (List<T>) mappedHandler.handleExecute(MethodKind.SELECT_BY_KEY,
                entityClass, keys
        );
    }

    @Override
    public <T> T selectOne(Class<T> entityClass, String condition, Object... params) throws Exception {
        return (T) mappedHandler.handleExecute(MethodKind.SELECT_ONE,
                entityClass, condition, params
        );
    }

    @Override
    public <T> T selectOneBySql(Class<T> entityClass, String sql, Object... params) throws Exception {
        return (T) mappedHandler.handleExecute(MethodKind.SELECT_ONE_BY_SQL,
                entityClass, sql, params
        );
    }

    @Override
    public <T> T selectOne(T entity) throws Exception {
        return (T) mappedHandler.handleExecute(MethodKind.SELECT_ONE_BY_ENTITY, entity);
    }

    @Override
    public <T> List<T> selectList(T entity) throws Exception {
        return (List<T>) mappedHandler.handleExecute(MethodKind.SELECT_LIST_BY_ENTITY, entity);
    }

    @Override
    public <T> DbPageRows<T> selectPage(T entity, DbPageRows<T> pageRows) throws Exception {
        return (DbPageRows<T>) mappedHandler.handleExecute(MethodKind.SELECT_PAGE_BY_ENTITY, entity, pageRows);
    }

    @Override
    public <T> T[] selectArrays(Class<T> t, String sql, Object... params) throws Exception {
        return (T[]) mappedHandler.handleExecute(MethodKind.SELECT_ARRAYS, t, sql, params);
    }

    @Override
    public Object selectObjBySql(String sql, Object... params) throws Exception {
        return mappedHandler.handleExecute(MethodKind.SELECT_OBJ_BY_SQL, sql, params);
    }

    @Override
    public <K, V> Map<K, V> selectMap(Class<K> kClass, Class<V> vClass, String sql, Object... params) throws Exception {
        return (Map<K, V>) mappedHandler.handleExecute(MethodKind.SELECT_MAP,
                kClass, vClass, sql, params
        );
    }

    @Override
    public <T> DbPageRows<T> selectPage(ConditionWrapper<T> wrapper) throws Exception {
        return (DbPageRows<T>) mappedHandler.handleExecute(MethodKind.SELECT_PAGE_BY_WRAPPER, wrapper);
    }

    @Override
    public <T> List<T> selectList(ConditionWrapper<T> wrapper) throws Exception {
        return (List<T>) mappedHandler.handleExecute(MethodKind.SELECT_LIST_BY_WRAPPER, wrapper);
    }

    @Override
    public <T> T selectOne(ConditionWrapper<T> wrapper) throws Exception {
        return (T) mappedHandler.handleExecute(MethodKind.SELECT_ONE_BY_WRAPPER, wrapper);
    }

    @Override
    public <T> long selectCount(ConditionWrapper<T> wrapper) throws Exception {
        return (long) mappedHandler.handleExecute(MethodKind.SELECT_COUNT_BY_WRAPPER, wrapper);
    }

    @Override
    public <T> Object selectObj(ConditionWrapper<T> wrapper) throws Exception {
        return mappedHandler.handleExecute(MethodKind.SELECT_OBJ_BY_WRAPPER, wrapper);
    }

    @Override
    public <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception {
        return (List<Object>) mappedHandler.handleExecute(MethodKind.SELECT_OBJS_BY_WRAPPER, wrapper);
    }

    @Override
    public <T> Map<String, Object> selectOneMap(ConditionWrapper<T> wrapper) throws Exception {
        return (Map<String, Object>) mappedHandler.handleExecute(MethodKind.SELECT_ONE_MAP_BY_WRAPPER, wrapper);
    }

    @Override
    public <T> List<Map<String, Object>> selectListMap(ConditionWrapper<T> wrapper) throws Exception {
        return (List<Map<String, Object>>) mappedHandler.handleExecute(MethodKind.SELECT_LIST_MAP_BY_WRAPPER, wrapper);
    }

    @Override
    public <T> DbPageRows<Map<String, Object>> selectPageMap(ConditionWrapper<T> wrapper) throws Exception {
        return (DbPageRows<Map<String, Object>>) mappedHandler.handleExecute(MethodKind.SELECT_PAGE_MAP_BY_WRAPPER, wrapper);
    }

    @Override
    public <T, K, V> Map<K, V> selectMap(ConditionWrapper<T> wrapper, Class<K> kClass, Class<V> vClass) throws Exception {
        return (Map<K, V>) mappedHandler.handleExecute(MethodKind.SELECT_MAP_BY_WRAPPER, wrapper, kClass, vClass);
    }


    @Override
    public <T> int deleteByKey(Class<T> entityClass, Serializable key) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.DELETE_BY_KEY, key);
    }

    @Override
    public <T> int deleteBatchKeys(Class<T> entityClass, Collection<? extends Serializable> keys) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.DELETE_BATCH_KEYS, keys);
    }

    @Override
    public <T> int deleteByCondition(Class<T> entityClass, String condition, Object... params) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.DELETE_BY_CONDITION,
                entityClass, condition, params
        );
    }

    @Override
    public <T> int deleteSelective(ConditionWrapper<T> wrapper) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.DELETE_SELECTIVE, wrapper);
    }

    @Override
    public <T> int insert(T entity) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.INSERT_ONE, entity);
    }

    @Override
    public <T> int insertBatch(List<T> ts) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.INSERT_BATCH, ts);
    }

    @Override
    public <T> int updateByKey(T entity) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.UPDATE_BY_KEY, entity);
    }

    @Override
    public <T> int updateSelective(T entity, ConditionWrapper<T> wrapper) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.UPDATE_SELECTIVE_BY_WRAPPER,
                entity, wrapper
        );
    }

    @Override
    public <T> int updateByCondition(T entity, String condition, Object... params) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.UPDATE_BY_CONDITION,
                entity, condition, params
        );
    }

    @Override
    public <T> int updateSelective(AbstractUpdateSet<T> updateSet) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.UPDATE_SELECTIVE_BY_SQL_SET, updateSet);
    }

    @Override
    public <T> int save(T entity) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.SAVE, entity);
    }

    @Override
    public int executeSql(String sql, Object... params) throws Exception {
        return (int) mappedHandler.handleExecute(MethodKind.EXECUTE_SQL, sql, params);
    }

    @Override
    public void createTables(Class<?>... arr) throws Exception {
        mappedHandler.handleExecute(MethodKind.CREATE_TABLES, (Object) arr);
    }

    @Override
    public void dropTables(Class<?>... arr) throws Exception {
        mappedHandler.handleExecute(MethodKind.DROP_TABLES, (Object) arr);
    }

    @Override
    public JdbcSqlSessionFactory getSqlSessionFactory() {
        return this.mappedHandler.getSqlSessionFactory();
    }

    @Override
    public void execTrans(TransactionExecutor executor) throws Exception {
        mappedHandler.handleExecute(MethodKind.EXEC_TRANS, executor);
    }

    @Override
    public <T> ChainWrapper<T> createChain(Class<T> entityClass) throws Exception {
        return (ChainWrapper<T>) mappedHandler.handleExecute(MethodKind.CREATE_CHAIN, entityClass);
    }


}
