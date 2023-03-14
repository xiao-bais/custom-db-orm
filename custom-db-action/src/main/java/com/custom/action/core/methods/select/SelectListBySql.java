package com.custom.action.core.methods.select;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/10 23:07
 */
public class SelectListBySql extends AbstractMethod {

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(executorFactory, target, params);
        return executorFactory.getJdbcExecutor().selectList(sqlSession);
    }


    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) {
        String selectSql = String.valueOf(params[1]);
        SelectExecutorBody<T> executorBody = new SelectExecutorBody<>(target, selectSql, sqlPrintSupport, (Object[]) params[2]);
        return executorFactory.createSqlSession(executorBody);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_LIST_BY_SQL;
    }
}
