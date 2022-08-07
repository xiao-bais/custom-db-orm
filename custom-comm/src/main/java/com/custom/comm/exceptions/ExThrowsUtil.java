package com.custom.comm.exceptions;

/**
 * @author Xiao-Bai
 * @date 2022/4/16 12:44
 * @desc:异常处理
 */
public class ExThrowsUtil {

    /**
     * 抛出自定义异常
     */
    public static void toCustom(String msg) {
        throw new CustomCheckException(msg);
    }

    /**
     * 抛出自定义异常
     */
    public static void toCustom(String msgFt, Object... params) {
        throw new CustomCheckException(String.format(msgFt, params));
    }

    /**
     * 抛出空指针异常
     */
    public static void toNull(String msg) {
        throw new NullPointerException(msg);
    }

    /**
     * 抛出不合法参数异常
     */
    public static void toIllegal(String msg) {
        throw new IllegalArgumentException(msg);
    }

    /**
     * 抛出不支持操作异常
     */
    public static void toUnSupport(String msg) {
        throw new UnsupportedOperationException(msg);
    }

}
