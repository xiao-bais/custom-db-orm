package com.custom.jdbc.exceptions;

import com.custom.comm.exceptions.CustomCheckException;

/**
 * @author  Xiao-Bai
 * @since  2022/10/23 23:16
 */
public class SQLSessionException extends CustomCheckException {


    public SQLSessionException(String message) {
        super(message);
    }

    public SQLSessionException(String mft, Object... params) {
        super(mft, params);
    }

    public SQLSessionException(Throwable throwable) {
        super(throwable);
    }

    public SQLSessionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
