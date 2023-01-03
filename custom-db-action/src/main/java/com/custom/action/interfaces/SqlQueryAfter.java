package com.custom.action.interfaces;

import com.custom.jdbc.executor.JdbcExecutorFactory;

import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2023/1/2 15:38
 * 查询结果后，做一些数据的补充
 */
public interface SqlQueryAfter {

    <T> void handle(Class<T> target, List<T> result) throws Exception;

}
