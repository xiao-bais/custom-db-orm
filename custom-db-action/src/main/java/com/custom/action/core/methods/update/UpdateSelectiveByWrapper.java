package com.custom.action.core.methods.update;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executor.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/14 12:20
 */
@SuppressWarnings("unchecked")
public class UpdateSelectiveByWrapper extends UpdateByCondition {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        ConditionWrapper<T> wrapper = (ConditionWrapper<T>) params[1];
        return super.createSqlSession(sqlSessionFactory, target,
                new Object[]{
                        wrapper.getFinalConditional(),
                        wrapper.getParamValues().toArray()
                }
        );
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.UPDATE_SELECTIVE_BY_WRAPPER;
    }
}
