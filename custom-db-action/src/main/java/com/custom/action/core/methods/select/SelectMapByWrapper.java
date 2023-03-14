package com.custom.action.core.methods.select;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.HandleSelectSqlBuilder;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executor.JdbcExecutorFactory;

/**
 * @author Xiao-Bai
 * @since 2023/3/11 23:41
 */
@SuppressWarnings("unchecked")
public class SelectMapByWrapper extends SelectMap {

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        ConditionWrapper<T> conditionWrapper = (ConditionWrapper<T>) params[0];
        HandleSelectSqlBuilder<T> sqlBuilder = (HandleSelectSqlBuilder<T>) super.getSelectSqlBuilder(executorFactory, target);
        String selectSql = sqlBuilder.executeSqlBuilder(conditionWrapper);
        return super.doExecute(executorFactory, target,
                new Object[]{params[1],
                        params[2], selectSql,
                        conditionWrapper.getParamValues().toArray()
                });
    }

    @Override
    public <T> Class<T> getMappedType(Object[] params) {
        return super.getMappedType(params, 1);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_MAP_BY_WRAPPER;
    }
}
