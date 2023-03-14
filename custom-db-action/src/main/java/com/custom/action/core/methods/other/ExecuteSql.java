package com.custom.action.core.methods.other;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/14 13:27
 */
public class ExecuteSql extends AbstractMethod {
    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        BaseExecutorBody executorBody = new BaseExecutorBody(String.valueOf(params[0]), sqlPrintSupport, (Object[]) params[1]);
        return executorFactory.createSqlSession(executorBody);
    }

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = this.createSqlSession(executorFactory, target, params);
        return executorFactory.getJdbcExecutor().executeUpdate(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.EXECUTE_SQL;
    }
}
