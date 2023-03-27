package com.custom.action.core.methods.select;

import com.custom.action.condition.ConditionWrapper;
import com.custom.action.core.HandleSelectSqlBuilder;
import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Xiao-Bai
 * @since 2023/3/11 23:05
 */
@SuppressWarnings("unchecked")
public class SelectPageMapByWrapper extends AbstractMethod {


    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        ConditionWrapper<T> conditionWrapper = (ConditionWrapper<T>) params[0];
        HandleSelectSqlBuilder<T> sqlBuilder = (HandleSelectSqlBuilder<T>) super.getSelectSqlBuilder(sqlSessionFactory, target);
        String selectSql = sqlBuilder.executeSqlBuilder(conditionWrapper);
        Object[] sqlParams = conditionWrapper.getParamValues().toArray();
        CustomSqlSession countSqlSession = createCountSqlSession(sqlSessionFactory, selectSql, sqlParams);
        long count = (long) sqlSessionFactory.getJdbcExecutor().selectObj(countSqlSession);

        List<Map<String, Object>> dataList = new ArrayList<>();
        DbPageRows<Map<String, Object>> dbPageRows = new DbPageRows<>(conditionWrapper.getPageIndex(), conditionWrapper.getPageSize());
        if (count > 0) {
            CustomSqlSession pageSqlSession = createPageSqlSession(sqlSessionFactory,
                    target,
                    selectSql,
                    conditionWrapper.getPageIndex(),
                    conditionWrapper.getPageSize(),
                    sqlParams);
            dataList = sqlSessionFactory.getJdbcExecutor().selectListMap(pageSqlSession);
        }
        dbPageRows.setTotal(count).setData(dataList);
        return dbPageRows;
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_PAGE_MAP_BY_WRAPPER;
    }

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        return null;
    }
}
