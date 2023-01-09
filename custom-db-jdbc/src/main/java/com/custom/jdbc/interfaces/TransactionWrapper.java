package com.custom.jdbc.interfaces;

/**
 * @author Xiao-Bai
 * @date 2023/1/7 14:55
 * 对于事务的执行包装
 */
public interface TransactionWrapper {

    void doing() throws Exception;
}
