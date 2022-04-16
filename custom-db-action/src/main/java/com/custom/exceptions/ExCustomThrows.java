package com.custom.exceptions;

/**
 * @author Xiao-Bai
 * @date 2022/4/16 12:44
 * @desc:异常处理
 */
public class ExCustomThrows {

    /**
     * 抛出自定义异常
     */
    public static void goCustom(String msg) {
        throw new CustomCheckException(msg);
    }

    /**
     * 抛出空指针异常
     */
    public static void goNull(String msg) {
        throw new NullPointerException(msg);
    }

}
