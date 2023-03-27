package com.custom.action.core.methods.select;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultConditionWrapper;
import com.custom.action.core.methods.MethodKind;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.session.JdbcSqlSessionFactory;

/**
 * @author Xiao-Bai
 * @since 2023/3/11 23:22
 */
@SuppressWarnings("unchecked")
public class SelectPageByEntity extends SelectPageByWrapper {

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        DefaultConditionWrapper<T> conditionWrapper = Conditions.allEqQuery((T) params[0]);
        if (params.length > 1 && params[1] != null && params[1] instanceof DbPageRows) {
            DbPageRows<T> dbPageRows = (DbPageRows<T>) params[1];
            conditionWrapper.pageParams(dbPageRows.getPageIndex(), dbPageRows.getPageSize());
        }
        return super.doExecute(sqlSessionFactory, target, new Object[]{conditionWrapper, params[1]});
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_PAGE_BY_ENTITY;
    }
}
