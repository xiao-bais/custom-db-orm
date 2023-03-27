package com.custom.action.core.methods.select;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/10 23:07
 */
public class SelectListBySql extends AbstractMethod {

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(sqlSessionFactory, target, params);
        return sqlSessionFactory.getJdbcExecutor().selectList(sqlSession);
    }


    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) {
        String selectSql = String.valueOf(params[1]);
        SelectExecutorBody<T> executorBody = ExecuteBodyHelper.createSelectIf(target, selectSql, sqlPrintSupport, (Object[]) params[2]);
        return sqlSessionFactory.createSqlSession(executorBody);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_LIST_BY_SQL;
    }
}
