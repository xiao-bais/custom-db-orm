package com.custom.jdbc.exceptions;

import com.custom.comm.exceptions.CustomCheckException;

/**
 * 查询多结果异常
 * @author   Xiao-Bai
 * @since  2022/12/19 0019 10:37
 */
public class QueryMultiException extends CustomCheckException {

    public QueryMultiException(String message) {
        super(message);
    }

}
