package com.custom.action.core.methods.select;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.HandleSelectSqlBuilder;
import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/11 21:27
 */
@SuppressWarnings("unchecked")
public class SelectCountByWrapper extends AbstractMethod {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        ConditionWrapper<T> conditionWrapper = (ConditionWrapper<T>) params[0];
        HandleSelectSqlBuilder<T> sqlBuilder = (HandleSelectSqlBuilder<T>) super.getSelectSqlBuilder(executorFactory, target);
        String selectCountSql = sqlBuilder.createSelectCountSql(conditionWrapper);
        return super.createCountSqlSession(executorFactory, selectCountSql, conditionWrapper.getParamValues().toArray());
    }

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(executorFactory, target, params);
        return executorFactory.getJdbcExecutor().selectObj(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_COUNT_BY_WRAPPER;
    }
}
