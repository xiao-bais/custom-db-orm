package com.custom.jdbc.executor;

/**
 * sql后置拦截器 <b>查询</b>后，做一些数据的补充
 * @author   Xiao-Bai
 * @since  2023/1/2 15:38
 */
public interface CustomSqlQueryAfter {

    <T> void handle(Class<T> target, Object result) throws Exception;

}
