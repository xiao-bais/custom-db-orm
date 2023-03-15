package com.custom.action.core.methods.select;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.HandleSelectSqlBuilder;
import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/10 23:30
 */
@SuppressWarnings("unchecked")
public class SelectListByWrapper extends AbstractMethod {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        ConditionWrapper<T> conditionWrapper = (ConditionWrapper<T>) params[0];
        HandleSelectSqlBuilder<T> sqlBuilder = (HandleSelectSqlBuilder<T>) super.getSelectSqlBuilder(executorFactory, target);
        String selectSql = sqlBuilder.executeSqlBuilder(conditionWrapper);
        SelectExecutorBody<T> executorBody = ExecuteBodyHelper.createSelect(
                target, selectSql, sqlPrintSupport,
                conditionWrapper.getParamValues().toArray()
        );
        return executorFactory.createSqlSession(executorBody);
    }

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(executorFactory, target, params);
        return executorFactory.getJdbcExecutor().selectList(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_LIST_BY_WRAPPER;
    }
}
