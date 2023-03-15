package com.custom.action.core.methods.others;

import com.custom.action.core.TableInfoCache;
import com.custom.action.core.TableParseModel;
import com.custom.action.core.methods.AbstractMethod;
import com.custom.action.core.methods.MethodKind;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.executor.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;
import com.custom.jdbc.interfaces.DatabaseAdapter;

/**
 * @author Xiao-Bai
 * @since 2023/3/14 12:29
 */
public class CreateTables extends AbstractMethod {

    @Override
    protected <T> CustomSqlSession createSqlSession(JdbcSqlSessionFactory sqlSessionFactory, Class<T> target, Object[] params) throws Exception {
        TableParseModel<T> tableModel = TableInfoCache.getTableModel(target);
        DatabaseAdapter databaseAdapter = sqlSessionFactory.getDatabaseAdapter();
        if (databaseAdapter.existTable(tableModel.getTable())) {
            String createTableSql = tableModel.createTableSql();
            BaseExecutorBody executorBody = new BaseExecutorBody(createTableSql, false, new Object[]{});
            return sqlSessionFactory.createSqlSession(executorBody);
        }
        return null;
    }

    @Override
    public <T> Object doExecute(JdbcSqlSessionFactory executorFactory, Class<T> target, Object[] params) throws Exception {
        Class<?>[] createTables = (Class<?>[]) params[0];
        for (int i = createTables.length - 1; i >= 0; i--) {
            CustomSqlSession sqlSession = this.createSqlSession(executorFactory, createTables[i], params);
            if (sqlSession != null) {
                String createTableSql = sqlSession.getBody().getPrepareSql();
                logger.info("createTableSql ->\n " + createTableSql);
                executorFactory.getJdbcExecutor().execTableInfo(sqlSession);
            }
        }
        return null;
    }

    @Override
    public MethodKind getKind() {
        return MethodKind.CREATE_TABLES;
    }
}
