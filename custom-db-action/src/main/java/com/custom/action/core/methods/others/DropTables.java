package com.custom.action.core.methods.others;

import com.custom.action.core.TableInfoCache;
import com.custom.action.core.TableParseModel;
import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.session.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;

/**
 * @author Xiao-Bai
 * @since 2023/3/14 13:00
 */
public class DropTables extends AbstractMethod {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        TableParseModel<?> tableSqlBuilder = TableInfoCache.getTableModel(target);
        String dropTableSql = tableSqlBuilder.dropTableSql();
        logger.warn("drop table '{}' completed\n", tableSqlBuilder.getTable());
        BaseExecutorBody executorBody = new BaseExecutorBody(dropTableSql, false, new Object[]{});
        return sqlSessionFactory.createSqlSession(executorBody);
    }

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        Class<?>[] dropTables = (Class<?>[]) params[0];
        for (int i = dropTables.length - 1; i >= 0; i--) {
            CustomSqlSession sqlSession = this.createSqlSession(sqlSessionFactory, dropTables[i], params);
            sqlSessionFactory.getJdbcExecutor().execTableInfo(sqlSession);
        }
        return null;
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.DROP_TABLES;
    }
}
