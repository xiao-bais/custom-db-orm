package com.custom.action.core.methods.others;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/14 13:27
 */
public class ExecuteSql extends AbstractMethod {
    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        BaseExecutorBody executorBody = new BaseExecutorBody(String.valueOf(params[0]), sqlPrintSupport, (Object[]) params[1]);
        return sqlSessionFactory.createSqlSession(executorBody);
    }

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = this.createSqlSession(sqlSessionFactory, target, params);
        return sqlSessionFactory.getJdbcExecutor().executeUpdate(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.EXECUTE_SQL;
    }
}
