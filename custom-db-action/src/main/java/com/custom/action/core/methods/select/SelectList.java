package com.custom.action.core.methods.select;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/10 22:15
 */
public class SelectList extends AbstractMethod {

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(sqlSessionFactory, target, params);
        return sqlSessionFactory.getJdbcExecutor().selectList(sqlSession);
    }

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = super.getSelectSqlBuilder(sqlSessionFactory, target);
        FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(String.valueOf(params[1]));
        String selectSql = sqlBuilder.createTargetSql() + executor.execute();
        SelectExecutorBody<T> executorBody = ExecuteBodyHelper.createSelectIf(target, selectSql, sqlPrintSupport, (Object[]) params[2]);
        return sqlSessionFactory.createSqlSession(executorBody);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_LIST;
    }
}
