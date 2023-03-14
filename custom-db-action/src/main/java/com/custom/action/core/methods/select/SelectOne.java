package com.custom.action.core.methods.select;

import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/10 23:09
 */
public class SelectOne extends SelectList {

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = super.createSqlSession(executorFactory, target, params);
        return executorFactory.getJdbcExecutor().selectOne(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_ONE;
    }
}
