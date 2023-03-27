package com.custom.action.core.methods.select;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.HandleSelectSqlBuilder;
import com.custom.action.core.methods.MethodKind;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/11 21:16
 */
@SuppressWarnings("unchecked")
public class SelectPageByWrapper extends SelectListByWrapper {

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        ConditionWrapper<T> conditionWrapper = (ConditionWrapper<T>) params[0];
        if (!conditionWrapper.hasPageParams()) {
            throw new CustomCheckException("Missing paging parameter：pageIndex：%s, pageSize：%s",
                    conditionWrapper.getPageIndex(), conditionWrapper.getPageSize()
            );
        }

        DbPageRows<T> pageRows;
        if (params.length > 1 && params[1] instanceof DbPageRows) {
            pageRows = (DbPageRows<T>) params[1];
        }else {
            pageRows = new DbPageRows<>(conditionWrapper.getPageIndex(), conditionWrapper.getPageSize());
        }

        HandleSelectSqlBuilder<T> sqlBuilder = (HandleSelectSqlBuilder<T>) super.getSelectSqlBuilder(sqlSessionFactory, target);
        String selectSql = sqlBuilder.executeSqlBuilder(conditionWrapper);
        super.buildPageResult(sqlSessionFactory, target, selectSql, pageRows, conditionWrapper.getParamValues().toArray());
        return pageRows;
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_PAGE_BY_WRAPPER;
    }
}
