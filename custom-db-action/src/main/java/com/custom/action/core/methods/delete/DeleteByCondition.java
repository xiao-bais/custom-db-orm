package com.custom.action.core.methods.delete;

import com.custom.action.core.TableInfoCache;
import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.comm.utils.AssertUtil;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/13 13:50
 */
public class DeleteByCondition extends AbstractMethod {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        AbstractSqlBuilder<T> sqlBuilder = TableInfoCache.getDeleteSqlBuilderCache(target, sqlSessionFactory);
        String condition = String.valueOf(params[1]);
        AssertUtil.notEmpty(condition, "delete condition cannot be empty.");
        FullSqlConditionExecutor conditionExecutor = sqlBuilder.addLogicCondition(condition);
        String deleteSql = sqlBuilder.createTargetSql() + conditionExecutor.execute();
        BaseExecutorBody executorBody = ExecuteBodyHelper.createExecUpdate(deleteSql, sqlPrintSupport, (Object[]) params[2]);
        return sqlSessionFactory.createSqlSession(executorBody);
    }

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(executorFactory, target, params);
        return executorFactory.getJdbcExecutor().executeUpdate(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.DELETE_BY_CONDITION;
    }
}
