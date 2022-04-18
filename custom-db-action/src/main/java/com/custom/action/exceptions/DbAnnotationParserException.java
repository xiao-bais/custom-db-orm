package com.custom.action.exceptions;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/8
 * @Description
 */
public class DbAnnotationParserException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public DbAnnotationParserException(String message) {
        super(message);
    }

    public DbAnnotationParserException(Throwable throwable) {
        super(throwable);
    }

    public DbAnnotationParserException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
