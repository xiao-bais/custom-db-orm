package com.custom.action.core.methods.select;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executebody.SelectMapExecutorBody;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/11 22:37
 */
public class SelectMap extends AbstractMethod {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        return null;
    }

    private <K, V> SelectMapExecutorBody<K, V> createMapBody(Class<K> keyClass, Object[] params) {
        return new SelectMapExecutorBody<>(
                String.valueOf(params[2]), sqlPrintSupport,
                (Object[]) params[3],
                keyClass, getMappedType(params, 1)
        );
    }

    @Override
    public <T> Object doExecute(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        SelectMapExecutorBody<T, Object> mapExecutorBody = createMapBody(target, params);
        CustomSqlSession sqlSession = executorFactory.createSqlSession(mapExecutorBody);
        return executorFactory.getJdbcExecutor().selectMap(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_MAP;
    }
}