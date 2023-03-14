package com.custom.action.core.methods.other;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;
import com.custom.jdbc.interfaces.TransactionExecutor;

/**
 * @author Xiao-Bai
 * @since 2023/3/14 13:04
 */
public class ExecTrans extends AbstractMethod {
    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        return null;
    }

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        TransactionExecutor transactionExecutor = (TransactionExecutor) params[0];
        executorFactory.handleTransaction(transactionExecutor);
        return null;
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.EXEC_TRANS;
    }
}
