package com.custom.action.core.methods.select;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.HandleSelectSqlBuilder;
import com.custom.action.core.methods.MethodKind;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/11 21:16
 */
@SuppressWarnings("unchecked")
public class SelectPageByWrapper extends SelectListByWrapper {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        return null;
    }

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        ConditionWrapper<T> conditionWrapper = (ConditionWrapper<T>) params[0];
        HandleSelectSqlBuilder<T> sqlBuilder = (HandleSelectSqlBuilder<T>) super.getSelectSqlBuilder(executorFactory, target);
        String selectSql = sqlBuilder.executeSqlBuilder(conditionWrapper);
        DbPageRows<T> pageRows = new DbPageRows<>(conditionWrapper.getPageIndex(), conditionWrapper.getPageSize());
        buildPageResult(executorFactory, target, selectSql, pageRows, conditionWrapper.getParamValues().toArray());
        return pageRows;
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_PAGE_BY_WRAPPER;
    }
}
