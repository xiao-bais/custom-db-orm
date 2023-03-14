package com.custom.action.core.methods.select;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/10 22:15
 */
public class SelectList extends AbstractMethod {

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(executorFactory, target, params);
        return executorFactory.getJdbcExecutor().selectList(sqlSession);
    }

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = super.getSelectSqlBuilder(executorFactory, target);
        FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(String.valueOf(params[1]));
        String selectSql = sqlBuilder.createTargetSql() + executor.execute();
        SelectExecutorBody<T> executorBody = new SelectExecutorBody<>(target, selectSql, sqlPrintSupport, (Object[]) params[2]);
        return executorFactory.createSqlSession(executorBody);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_LIST;
    }
}
