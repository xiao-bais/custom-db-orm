package com.custom.jdbc.interfaces;

/**
 * @author Xiao-Bai
 * @date 2023/1/7 14:55
 * 若涉及事务操作，可在该接口中完成
 */
public interface TransactionExecutor {

    void doing() throws Exception;
}
