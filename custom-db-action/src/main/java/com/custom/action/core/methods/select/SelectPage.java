package com.custom.action.core.methods.select;

import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.action.dbaction.AbstractSqlBuilder;
import com.custom.action.interfaces.FullSqlConditionExecutor;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/10 22:21
 */
@SuppressWarnings("unchecked")
public class SelectPage extends AbstractMethod {
    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {

        DbPageRows<T> pageRows = (DbPageRows<T>) params[2];
        if(pageRows == null) {
            pageRows = new DbPageRows<>();
        }
        AbstractSqlBuilder<T> sqlBuilder = super.getSelectSqlBuilder(sqlSessionFactory, target);
        FullSqlConditionExecutor executor = sqlBuilder.addLogicCondition(String.valueOf(params[1]));

        // 封装结果
        String selectSql = sqlBuilder.createTargetSql() + executor.execute();
        this.buildPageResult(sqlSessionFactory, getMappedType(params), selectSql, pageRows, (Object[]) params[3]);
        return pageRows;
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.SELECT_PAGE;
    }

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        return null;
    }
}
