package com.custom.action.interfaces;

import com.custom.jdbc.condition.BaseExecutorBody;

/**
 * sql 拦截器
 * @author  Xiao-Bai
 * @since  2023/2/7 22:13
 */
public interface CustomSqlInterceptor {

    /**
     * sql执行前可做一些拦截处理
     * @param body 执行体本身
     */
    void handler(BaseExecutorBody body) throws Exception;


}
