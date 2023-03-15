package com.custom.action.core.methods.update;

import com.custom.action.condition.AbstractUpdateSet;
import com.custom.action.condition.ConditionWrapper;
import com.custom.action.condition.UpdateSetWrapper;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.comm.enums.SqlExecTemplate;
import com.custom.comm.utils.CustomUtil;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.executebody.SaveExecutorBody;
import com.custom.jdbc.executor.JdbcExecutorFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

import java.util.List;

/**
 * @author Xiao-Bai
 * @since 2023/3/14 12:23
 */
@SuppressWarnings("unchecked")
public class UpdateSelectiveBySqlSet extends UpdateByCondition {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcExecutorFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        AbstractUpdateSet<T> updateSet = (AbstractUpdateSet<T>) params[0];
        UpdateSetWrapper<T> updateSetWrapper = updateSet.getUpdateSetWrapper();
        ConditionWrapper<T> conditionWrapper = updateSet.getConditionWrapper();

        AbstractSqlBuilder<T> sqlBuilder = super.getUpdateSqlBuilder(executorFactory, target);
        FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(conditionWrapper.getFinalConditional());
        String finalConditional = executor.execute();
        List<Object> sqlParams = updateSetWrapper.getSetParams();
        CustomUtil.addParams(sqlParams, conditionWrapper.getParamValues());

        // 创建SQL
        String updateSql = SqlExecTemplate.format(SqlExecTemplate.UPDATE_DATA, sqlBuilder.getTable(), sqlBuilder.getAlias(),
                updateSetWrapper.getSqlSetter(), finalConditional);
        BaseExecutorBody executorBody = ExecuteBodyHelper.createExecUpdate(updateSql, sqlPrintSupport, sqlParams.toArray());
        return executorFactory.createSqlSession(executorBody);
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.UPDATE_SELECTIVE_BY_SQL_SET;
    }
}
