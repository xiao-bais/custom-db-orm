package com.custom.action.core.methods.select;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.HandleSelectSqlBuilder;
import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.executor.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/11 22:18
 */
@SuppressWarnings("unchecked")
public class SelectObjsByWrapper extends AbstractMethod {

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(sqlSessionFactory, target, params);
        return sqlSessionFactory.getJdbcExecutor().selectObjs(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_OBJS_BY_WRAPPER;
    }

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        ConditionWrapper<T> conditionWrapper = (ConditionWrapper<T>) params[0];
        HandleSelectSqlBuilder<T> sqlBuilder = (HandleSelectSqlBuilder<T>) super.getSelectSqlBuilder(sqlSessionFactory, target);
        String selectSql = sqlBuilder.executeSqlBuilder(conditionWrapper);
        SelectExecutorBody<T> executorBody = ExecuteBodyHelper.createSelect(target, selectSql, conditionWrapper.getParamValues().toArray());
        return sqlSessionFactory.createSqlSession(executorBody);
    }
}
