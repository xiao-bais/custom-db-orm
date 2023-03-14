package com.custom.action.core.methods.select;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultConditionWrapper;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executor.JdbcExecutorFactory;

/**
 * @author Xiao-Bai
 * @since 2023/3/10 23:19
 */
@SuppressWarnings("unchecked")
public class SelectListByEntity extends SelectListByWrapper {

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        T param = (T) params[0];
        DefaultConditionWrapper<T> conditionWrapper = Conditions.allEqQuery(param);
        return super.doExecute(executorFactory, target, new Object[]{conditionWrapper});
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_LIST_BY_ENTITY;
    }


}
