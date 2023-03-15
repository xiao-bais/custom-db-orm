package com.custom.action.core.methods.update;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.comm.utils.AssertUtil;
import com.custom.comm.utils.CustomUtil;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiao-Bai
 * @since 2023/3/14 11:58
 */
@SuppressWarnings("unchecked")
public class UpdateByCondition extends AbstractMethod {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        String condition = String.valueOf(params[1]);
        AssertUtil.notEmpty("update condition cannot be empty.");
        T entity = (T) params[0];
        AbstractSqlBuilder<T> sqlBuilder = super.getUpdateSqlBuilder(sqlSessionFactory, target);
        // 创建update sql
        List<Object> sqlParamList = new ArrayList<>();
        String updateSql = sqlBuilder.createTargetSql(entity, sqlParamList);
        CustomUtil.addParams(sqlParamList, params);

        // 拼接sql
        FullSqlConditionExecutor conditionExecutor = sqlBuilder.addLogicCondition(condition);
        updateSql = updateSql + conditionExecutor.execute();
        BaseExecutorBody executorBody = ExecuteBodyHelper.createExecUpdate(updateSql, sqlPrintSupport, sqlParamList.toArray());
        return sqlSessionFactory.createSqlSession(executorBody);
    }

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        CustomSqlSession sqlSession = createSqlSession(executorFactory, target, params);
        return executorFactory.getJdbcExecutor().executeUpdate(sqlSession);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.UPDATE_BY_CONDITION;
    }
}
