package com.custom.comm.exceptions;

/**
 * @author Xiao-Bai
 * @since 2021/7/8
 */
public class CustomCheckException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustomCheckException(String message) {
        super(message);
    }

    public CustomCheckException(String mft, Object... params) {
        super(String.format(mft, params));
    }

    public CustomCheckException(Throwable throwable) {
        super(throwable);
    }

    public CustomCheckException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
