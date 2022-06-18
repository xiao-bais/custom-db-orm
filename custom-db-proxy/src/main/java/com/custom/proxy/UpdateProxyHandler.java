package com.custom.proxy;

import com.custom.jdbc.condition.SaveSqlParamInfo;
import com.custom.jdbc.update.CustomUpdateJdbcBasic;

import java.lang.reflect.Method;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/6 0:29
 * @Desc 代理：执行增删改sql
 */
public class UpdateProxyHandler extends AbstractProxyHandler {

    @Override
    protected Object execute() throws Exception {
        String readyExecuteSql = sqlExecuteParamParser();
        Object[] sqlParams = getExecuteSqlParams().toArray();
        SaveSqlParamInfo<Object> sqlParamInfo = new SaveSqlParamInfo<>(readyExecuteSql, sqlParams);
        return getUpdateJdbc().executeUpdate(sqlParamInfo);
    }

    protected UpdateProxyHandler(CustomUpdateJdbcBasic updateJdbc, Object[] methodParams,
                                 String prepareSql, Method method) {

        super.setUpdateJdbc(updateJdbc);
        super.setMethodParams(methodParams);
        super.setPrepareSql(prepareSql);
        super.setMethod(method);
    }
}
