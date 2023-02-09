package com.custom.action.exceptions;

import com.custom.comm.enums.MultiStrategy;
import com.custom.comm.exceptions.CustomCheckException;

/**
 * 查询多结果异常
 * @author   Xiao-Bai
 * @since  2022/12/19 0019 10:37
 */
public class QueryMultiException extends CustomCheckException {

    private MultiStrategy strategy;

    private QueryMultiException(String message) {
        super(message);
    }

    public QueryMultiException(MultiStrategy strategy, String message) {
        this(message);
        this.strategy = strategy;
    }

    public MultiStrategy getStrategy() {
        return strategy;
    }
}
