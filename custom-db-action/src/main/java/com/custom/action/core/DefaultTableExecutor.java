package com.custom.action.core;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.interfaces.TableExecutor;
import com.custom.action.proxy.JdbcActionProxy;
import com.custom.action.condition.ConditionWrapper;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.configuration.DbDataSource;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 指定单表的专属DAO
 * @author   Xiao-Bai
 * @since  2022/7/15 0015 16:11
 */
public class DefaultTableExecutor<T, P extends Serializable> implements TableExecutor<T, P> {

//    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SqlExecutor jdbcAction;
    private final Class<T> entityClass;

    public DefaultTableExecutor(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.jdbcAction = new JdbcActionProxy(new JdbcAction(), dbDataSource, dbCustomStrategy).createProxy();
    }

    @Override
    public List<T> selectByKeys(Collection<P> keys) throws Exception {
        return jdbcAction.selectBatchKeys(entityClass, keys);
    }

    @Override
    public T selectByKey(P key) throws Exception {
        return jdbcAction.selectByKey(entityClass, key);
    }

    @Override
    public DbPageRows<T> selectPage(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectPage(wrapper);
    }

    @Override
    public List<T> selectList(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectList(wrapper);
    }

    @Override
    public T selectOne(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectOne(wrapper);
    }

    @Override
    public long selectCount(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectCount(wrapper);
    }

    @Override
    public Object selectObj(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectObj(wrapper);
    }

    @Override
    public List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectObjs(wrapper);
    }

    @Override
    public Map<String, Object> selectMap(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectOneMap(wrapper);
    }

    @Override
    public List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectListMap(wrapper);
    }

    @Override
    public DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectPageMap(wrapper);
    }

    @Override
    public int deleteByKey(P key) throws Exception {
        return jdbcAction.deleteByKey(entityClass, key);
    }

    @Override
    public int deleteBatchKeys(Collection<P> keys) throws Exception {
        return jdbcAction.deleteBatchKeys(entityClass, keys);
    }

    @Override
    public int deleteSelective(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.deleteSelective(wrapper);
    }

    @Override
    public int insert(T t) throws Exception {
        return jdbcAction.insert(t);
    }

    @Override
    public int insert(List<T> ts) throws Exception {
        return jdbcAction.insertBatch(ts);
    }

    @Override
    public int updateByKey(T t) throws Exception {
        return jdbcAction.updateByKey(t);
    }

    @Override
    public int updateSelective(T t, ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.updateSelective(t, wrapper);
    }

    @Override
    public int updateSelective(AbstractUpdateSet<T> updateSet) throws Exception {
        return jdbcAction.updateSelective(updateSet);
    }

    @Override
    public int save(T t) throws Exception {
        return jdbcAction.save(t);
    }

    @Override
    @SuppressWarnings("unchecked")
    public P primaryKeyValue(T entity) {
        EmptySqlBuilder<T> emptySqlBuilder = TableInfoCache.getEmptySqlBuilder(entityClass, jdbcAction.getExecutorFactory());
        if (emptySqlBuilder.getKeyParserModel() == null) {
            throw new CustomCheckException("No primary key field specified");
        }
        DbKeyParserModel<T> keyParserModel = emptySqlBuilder.getKeyParserModel();
        return (P) keyParserModel.getValue(entity);
    }


}
