package com.custom.proxy;

import com.custom.jdbc.session.JdbcSqlSessionFactory;

import java.lang.reflect.Method;

/**
 * @author  Xiao-Bai
 * @since  2022/6/6 0:29
 * @Desc 代理：执行增删改sql
 */
public class UpdateProxyHandler extends AbstractProxyHandler {

    @Override
    protected Object execute() throws Exception {
        String readyExecuteSql = sqlExecuteParamParser();
        Object[] sqlParams = getExecuteSqlParams().toArray();
        return executeAnySql(readyExecuteSql, sqlParams);
    }

    protected UpdateProxyHandler(JdbcSqlSessionFactory sqlSessionFactory, Object[] methodParams,
                                 String prepareSql, Method method) {
        super.setSqlSessionFactory(sqlSessionFactory);
        super.setMethodParams(methodParams);
        super.setPrepareSql(prepareSql);
        super.setMethod(method);
    }
}
