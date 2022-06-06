package com.custom.proxy;

import com.custom.comm.JudgeUtil;
import com.custom.comm.RexUtil;
import com.custom.jdbc.ExecuteSqlHandler;

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
        return getExecuteAction().executeUpdate(readyExecuteSql, sqlParams);
    }

    protected UpdateProxyHandler(ExecuteSqlHandler executeSqlHandler, Object[] methodParams,
                                 String prepareSql, Method method) {

        super.setExecuteAction(executeSqlHandler);
        super.setMethodParams(methodParams);
        super.setPrepareSql(prepareSql);
        super.setMethod(method);
    }
}
