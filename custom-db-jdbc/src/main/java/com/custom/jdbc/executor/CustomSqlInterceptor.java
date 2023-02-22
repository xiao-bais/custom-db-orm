package com.custom.jdbc.executor;

import com.custom.jdbc.condition.BaseExecutorBody;

/**
 * sql 前置拦截器
 * @author   Xiao-Bai
 * @since  2023/2/7 22:13
 */
public interface CustomSqlInterceptor {

    /**
     * sql执行前可做一些拦截处理
     * @param body 执行体本身
     * @return body 可返回新的执行体子类
     * @see com.custom.jdbc.condition.SelectExecutorBody
     * @see com.custom.jdbc.condition.SelectMapExecutorBody
     * @see com.custom.jdbc.condition.SaveExecutorBody
     */
    BaseExecutorBody handle(BaseExecutorBody body) throws Exception;


}
