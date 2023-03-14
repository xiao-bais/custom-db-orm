package com.custom.action.core.methods.delete;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/13 14:04
 */
@SuppressWarnings("unchecked")
public class DeleteSelective extends DeleteByCondition {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        ConditionWrapper<T> wrapper = (ConditionWrapper<T>) params[0];
        return super.createSqlSession(executorFactory,
                target,
                new Object[]{
                        target, wrapper.getFinalConditional(), wrapper.getParamValues().toArray()
                }
        );
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.DELETE_SELECTIVE;
    }
}
