package com.custom.action.core.methods.other;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

import java.util.Objects;

/**
 * @author Xiao-Bai
 * @since 2023/3/14 13:07
 */
@SuppressWarnings("unchecked")
public class Save extends AbstractMethod {
    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        return null;
    }

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        T entity = (T) params[0];
        AbstractSqlBuilder<T> sqlBuilder = this.getEmptySqlBuilder(executorFactory, target);
        boolean primaryKeyIsNotNull = Objects.nonNull(sqlBuilder.primaryKeyVal(entity));
        return primaryKeyIsNotNull ? MethodKind.INSERT : MethodKind.UPDATE_BY_KEY;
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SAVE;
    }
}
