package com.custom.jdbc.interfaces;

/**
 * 若涉及事务操作，可在该接口中完成
 * @author  Xiao-Bai
 * @since  2023/1/7 14:55
 */
public interface TransactionExecutor {

    void doing() throws Exception;
}
