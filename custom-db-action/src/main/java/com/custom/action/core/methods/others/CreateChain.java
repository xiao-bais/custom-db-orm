package com.custom.action.core.methods.others;

import com.custom.action.core.chain.ChainWrapper;
import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/14 13:33
 */
public class CreateChain extends AbstractMethod {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        return null;
    }

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        return new ChainWrapper<>(target, sqlSessionFactory);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.CREATE_CHAIN;
    }
}
