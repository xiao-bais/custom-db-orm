package com.custom.action.core.methods;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.HandleSelectSqlBuilder;
import com.custom.action.core.TableInfoCache;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.extend.MultiResultInjector;
import com.custom.action.interfaces.ExecuteHandler;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;
import com.custom.jdbc.interfaces.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiao-Bai
 * @since 2023/3/8 19:02
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMethod implements ExecuteHandler {

    protected static boolean sqlPrintSupport = true;

    protected abstract <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory,
                                                             Class<T> target,
                                                             Object[] params) throws Exception;


    public <T> Class<T> getMappedType(Object[] params) {
        return getMappedType(params, 0);
    }


    public <T> Class<T> getMappedType(Object[] params, int index) {
        if (params.length == 0 || params[index] == null) {
            throw new NullPointerException();
        }
        Object param = params[index];
        if (param instanceof CharSequence) {
            return (Class<T>) String.class;
        }
        if (param instanceof ConditionWrapper) {
            ConditionWrapper<T> conditionWrapper = (ConditionWrapper<T>) param;
            return conditionWrapper.getEntityClass();
        }
        if (param instanceof AbstractUpdateSet) {
            AbstractUpdateSet<T> updateSet = (AbstractUpdateSet<T>) param;
            return updateSet.thisEntityClass();
        }
        return param instanceof Class ?
                (Class<T>) param : (Class<T>) param.getClass();
    }


    protected <T> AbstractSqlBuilder<T> getSelectSqlBuilder(JdbcSqlSessionFactory sqlSessionFactory, Class<T> mappedType) {
        return TableInfoCache.getSelectSqlBuilderCache(mappedType, sqlSessionFactory);
    }

    protected <T> AbstractSqlBuilder<T> getInsertSqlBuilder(JdbcSqlSessionFactory sqlSessionFactory, Class<T> mappedType) {
        return TableInfoCache.getInsertSqlBuilderCache(mappedType, sqlSessionFactory);
    }

    protected <T> AbstractSqlBuilder<T> getDeleteSqlBuilder(JdbcSqlSessionFactory sqlSessionFactory, Class<T> mappedType) {
        return TableInfoCache.getDeleteSqlBuilderCache(mappedType, sqlSessionFactory);
    }

    protected <T> AbstractSqlBuilder<T> getUpdateSqlBuilder(JdbcSqlSessionFactory sqlSessionFactory, Class<T> mappedType) {
        return TableInfoCache.getUpdateSqlBuilderCache(mappedType, sqlSessionFactory);
    }

    protected <T> AbstractSqlBuilder<T> getEmptySqlBuilder(JdbcSqlSessionFactory sqlSessionFactory, Class<T> mappedType) {
        return TableInfoCache.getEmptySqlBuilder(mappedType, sqlSessionFactory);
    }

    /**
     * 分页数据整合
     */
    protected <T> void buildPageResult(JdbcSqlSessionFactory sqlSessionFactory,
                                       Class<T> target,
                                       String selectSql,
                                       DbPageRows<T> dbPageRows,
                                       Object[] params) throws Exception {

        List<T> dataList = new ArrayList<>();

        CustomSqlSession countSqlSession = this.createCountSqlSession(sqlSessionFactory, selectSql, params);
        long count = (long) sqlSessionFactory.getJdbcExecutor().selectObj(countSqlSession);

        if (count > 0) {
            CustomSqlSession selectSqlSession = this.createPageSqlSession(sqlSessionFactory, target, selectSql, dbPageRows.getPageIndex(), dbPageRows.getPageSize(), params);
            dataList = sqlSessionFactory.getJdbcExecutor().selectList(selectSqlSession);
        }
        dbPageRows.setTotal(count).setData(dataList);
    }

    protected CustomSqlSession createCountSqlSession(JdbcSqlSessionFactory sqlSessionFactory, String selectSql, Object[] params) {
        // 格式化并获取selectCountSQL
        String selectCountSql = SqlExecTemplate.format(SqlExecTemplate.SELECT_COUNT, selectSql);
        SelectExecutorBody<Long> executorBody = ExecuteBodyHelper.createSelectIf(
                Long.class,
                selectCountSql,
                sqlPrintSupport,
                params
        );
        return sqlSessionFactory.createSqlSession(executorBody);
    }

    protected <T> CustomSqlSession createPageSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, String selectSql, int pageIndex, int pageSize, Object[] params) {
        DatabaseAdapter databaseAdapter = sqlSessionFactory.getDatabaseAdapter();
        selectSql = databaseAdapter.handlePage(selectSql, pageIndex, pageSize);
        SelectExecutorBody<T> selectExecutorBody = ExecuteBodyHelper.createSelectIf(
                target,
                selectSql,
                sqlPrintSupport,
                params
        );
        return sqlSessionFactory.createSqlSession(selectExecutorBody);
    }

    /**
     * 对于返回对象的一对一，一对多处理
     */
    public <T> void otherResultInject(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, List<T> result) throws Exception {
        HandleSelectSqlBuilder<T> selectSqlBuilder = TableInfoCache.getSelectSqlBuilderCache(target, sqlSessionFactory);
        if (selectSqlBuilder.isExistNeedInjectResult() && result != null) {
            MultiResultInjector<T> resultInjector = new MultiResultInjector<>(target, sqlSessionFactory, target);
            resultInjector.injectorValue(result);
        }
    }
}
