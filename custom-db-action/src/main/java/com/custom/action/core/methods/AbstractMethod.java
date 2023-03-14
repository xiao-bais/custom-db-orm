package com.custom.action.core.methods;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.TableInfoCache;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.ExecuteHandler;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.executor.JdbcExecutorFactory;
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

    protected abstract <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory,
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


    protected <T> AbstractSqlBuilder<T> getSelectSqlBuilder(JdbcExecutorFactory executorFactory, Class<T> mappedType) {
        return TableInfoCache.getSelectSqlBuilderCache(mappedType, executorFactory);
    }

    protected <T> AbstractSqlBuilder<T> getInsertSqlBuilder(JdbcExecutorFactory executorFactory, Class<T> mappedType) {
        return TableInfoCache.getInsertSqlBuilderCache(mappedType, executorFactory);
    }

    protected <T> AbstractSqlBuilder<T> getDeleteSqlBuilder(JdbcExecutorFactory executorFactory, Class<T> mappedType) {
        return TableInfoCache.getDeleteSqlBuilderCache(mappedType, executorFactory);
    }

    protected <T> AbstractSqlBuilder<T> getUpdateSqlBuilder(JdbcExecutorFactory executorFactory, Class<T> mappedType) {
        return TableInfoCache.getUpdateSqlBuilderCache(mappedType, executorFactory);
    }

    protected <T> AbstractSqlBuilder<T> getEmptySqlBuilder(JdbcExecutorFactory executorFactory, Class<T> mappedType) {
        return TableInfoCache.getEmptySqlBuilder(mappedType, executorFactory);
    }

    protected <T> void buildPageResult(JdbcExecutorFactory executorFactory,
                                       Class<T> target,
                                       String selectSql,
                                       DbPageRows<T> dbPageRows,
                                       Object[] params) throws Exception {

        List<T> dataList = new ArrayList<>();

        CustomSqlSession countSqlSession = this.createCountSqlSession(executorFactory, selectSql, params);
        long count = (long) executorFactory.getJdbcExecutor().selectObj(countSqlSession);

        if (count > 0) {
            CustomSqlSession selectSqlSession = this.createPageSqlSession(executorFactory, target, selectSql, dbPageRows.getPageIndex(), dbPageRows.getPageSize(), params);
            dataList = executorFactory.getJdbcExecutor().selectList(selectSqlSession);
        }
        dbPageRows.setTotal(count).setData(dataList);
    }

    protected CustomSqlSession createCountSqlSession(JdbcExecutorFactory executorFactory, String selectSql, Object[] params) {
        // 格式化并获取selectCountSQL
        String selectCountSql = SqlExecTemplate.format(SqlExecTemplate.SELECT_COUNT, selectSql);
        SelectExecutorBody<Long> executorBody = new SelectExecutorBody<>(
                Long.class,
                selectCountSql,
                sqlPrintSupport,
                (Object[]) params[3]
        );
        return executorFactory.createSqlSession(executorBody);
    }

    protected <T> CustomSqlSession createPageSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, String selectSql, int pageIndex, int pageSize, Object[] params) {
        DatabaseAdapter databaseAdapter = executorFactory.getDatabaseAdapter();
        selectSql = databaseAdapter.handlePage(selectSql, pageIndex, pageSize);
        SelectExecutorBody<T> selectExecutorBody = new SelectExecutorBody<>(
                target,
                selectSql,
                sqlPrintSupport,
                (Object[]) params[3]
        );
        return executorFactory.createSqlSession(selectExecutorBody);
    }
}
