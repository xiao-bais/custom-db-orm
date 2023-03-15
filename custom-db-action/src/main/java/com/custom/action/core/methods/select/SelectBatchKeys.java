package com.custom.action.core.methods.select;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.executor.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Xiao-Bai
 * @since 2023/3/10 21:11
 */
@SuppressWarnings("unchecked")
public class SelectBatchKeys extends AbstractMethod {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        Collection<? extends Serializable> keys = (Collection<? extends Serializable>) params[1];
        AbstractSqlBuilder<T> sqlBuilder = super.getSelectSqlBuilder(sqlSessionFactory, target);
        String condition = sqlBuilder.createKeysCondition(keys);
        FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(condition);
        String selectSql = sqlBuilder.createTargetSql() + executor.execute();
        SelectExecutorBody<T> executorBody = ExecuteBodyHelper.createSelectIf(target, selectSql, sqlPrintSupport, keys.toArray());
        return sqlSessionFactory.createSqlSession(executorBody);
    }

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(executorFactory, target, params);
        return executorFactory.getJdbcExecutor().selectList(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_BATCH_KEYS;
    }


}
