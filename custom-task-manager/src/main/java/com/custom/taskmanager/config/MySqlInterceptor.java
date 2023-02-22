package com.custom.taskmanager.config;

import com.custom.jdbc.condition.BaseExecutorBody;
import com.custom.jdbc.executor.CustomSqlInterceptor;

/**
 * @author Xiao-Bai
 * @since 2023/2/21 12:15
 */
public class MySqlInterceptor implements CustomSqlInterceptor {


    @Override
    public BaseExecutorBody handle(BaseExecutorBody body) throws Exception {

        return null;
    }
}
