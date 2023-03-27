package com.custom.action.core.methods.select;

import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/11 23:20
 */
public class SelectArrays extends SelectListBySql {

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = super.createSqlSession(sqlSessionFactory, target, params);
        return sqlSessionFactory.getJdbcExecutor().selectArrays(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_ARRAYS;
    }
}
