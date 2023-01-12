package com.custom.action.interfaces;

import com.custom.action.core.JdbcOpDao;

/**
 * @author Xiao-Bai
 * @date 2023/1/10 0010 14:03
 */
public interface CustomTransactionHandler {

    void doing(JdbcOpDao jdbcOpDao) throws Exception;
}
