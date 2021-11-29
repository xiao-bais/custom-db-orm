package com.custom.exceptions;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/8
 * @Description
 */
public class CustomCheckException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String message;

    public CustomCheckException(String message) {
        super(message);
        this.message = message;
    }

    public CustomCheckException(Throwable throwable) {
        super(throwable);
    }

    public CustomCheckException(String message, Throwable throwable) {
        super(message, throwable);
        this.message = message;
    }

    @Override
    public String toString() {
        return "CustomCheckException{" +
                "message='" + message + '\'' +
                '}';
    }
}
