package com.custom.action.core.methods.select;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

import java.io.Serializable;

/**
 * @author Xiao-Bai
 * @since 2023/3/8 19:01
 */
public class SelectByKey extends AbstractMethod {

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(sqlSessionFactory, target, params);
        return sqlSessionFactory.getJdbcExecutor().selectOne(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_BY_KEY;
    }

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        Serializable key = (Serializable) params[1];
        AbstractSqlBuilder<T> sqlBuilder = super.getSelectSqlBuilder(sqlSessionFactory, target);
        String condition = sqlBuilder.createKeyCondition(key);
        FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(condition);
        String selectSql = sqlBuilder.createTargetSql() + executor.execute();
        SelectExecutorBody<T> executorBody = new SelectExecutorBody<>(target, selectSql, sqlPrintSupport, new Object[]{key});
        return sqlSessionFactory.createSqlSession(executorBody);
    }
}
