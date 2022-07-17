package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.action.dbaction.JdbcActiveWrapper;
import com.custom.action.proxy.JdbcActionProxy;
import com.custom.action.wrapper.ConditionWrapper;
import com.custom.action.wrapper.SFunction;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.comm.page.DbPageRows;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.jdbc.select.CustomSelectJdbcBasic;
import com.custom.jdbc.update.CustomUpdateJdbcBasic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/15 0015 16:11
 * @Desc
 */
public class JdbcSingleAction<T, P> implements JdbcActiveWrapper<T, P> {

    private static final Logger logger = LoggerFactory.getLogger(JdbcSingleAction.class);

    private final AbstractSqlExecutor jdbcAction;
    private final Class<T> entityClass;
    private final TableSqlBuilder<T> defaultSqlBuilder;

    public JdbcSingleAction(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.jdbcAction = new JdbcActionProxy<>(new JdbcAction(), dbDataSource, dbCustomStrategy).createProxy();
        this.defaultSqlBuilder = this.jdbcAction.defaultSqlBuilder(entityClass);
    }

    private TableSqlBuilder<T> updateSqlBuilder(List<T> tList) {
        return this.jdbcAction.updateSqlBuilder(tList);
    }

    @Override
    public DbPageRows<T> selectPageRows(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectPageRows(wrapper);
    }

    @Override
    public List<T> selectList(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectList(wrapper);
    }

    @Override
    public T selectOneByCondition(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectOneByCondition(wrapper);
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
        return jdbcAction.selectMap(wrapper);
    }

    @Override
    public List<Map<String, Object>> selectMaps(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectMaps(wrapper);
    }

    @Override
    public DbPageRows<Map<String, Object>> selectPageMaps(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.selectPageMaps(wrapper);
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
    public int deleteByCondition(ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.deleteByCondition(wrapper);
    }

    @Override
    public int insert(T t) throws Exception {
        return jdbcAction.insert(t);
    }

    @Override
    public int insert(List<T> ts) throws Exception {
        return jdbcAction.insert(ts);
    }

    @Override
    public int updateByKey(T t) throws Exception {
        return jdbcAction.updateByKey(t);
    }

    @SafeVarargs
    @Override
    public final int updateByKey(T t, SFunction<T, ?>... updateColumns) throws Exception {
        return jdbcAction.updateByKey(t, updateColumns);
    }

    @Override
    public int updateByCondition(T t, ConditionWrapper<T> wrapper) throws Exception {
        return jdbcAction.updateByCondition(t, wrapper);
    }

    @Override
    public long save(T t) throws Exception {
        return jdbcAction.save(t);
    }

    @Override
    public Object primaryKeyValue(T entity) {
        TableSqlBuilder<T> tableSqlBuilder = updateSqlBuilder(Collections.singletonList(entity));
        if (tableSqlBuilder.getKeyParserModel() == null) {
            ExThrowsUtil.toCustom("未指定主键字段");
        }
        return tableSqlBuilder.getKeyParserModel().getValue();
    }


}
