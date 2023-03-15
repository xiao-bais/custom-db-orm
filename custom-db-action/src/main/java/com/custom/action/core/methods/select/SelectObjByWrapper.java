package com.custom.action.core.methods.select;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.HandleSelectSqlBuilder;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executor.JdbcSqlSessionFactory;

/**
 * @author Xiao-Bai
 * @since 2023/3/11 21:33
 */
@SuppressWarnings("unchecked")
public class SelectObjByWrapper extends SelectObjBySql {


    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        ConditionWrapper<T> conditionWrapper = (ConditionWrapper<T>) params[0];
        HandleSelectSqlBuilder<T> sqlBuilder = (HandleSelectSqlBuilder<T>) super.getSelectSqlBuilder(executorFactory, target);
        String selectSql = sqlBuilder.executeSqlBuilder(conditionWrapper);
        return super.doExecute(executorFactory, target, new Object[]{selectSql, conditionWrapper.getParamValues()});
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_OBJ_BY_WRAPPER;
    }
}
