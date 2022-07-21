package com.custom.comm;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/21 23:58
 * @Desc 断言工具类
 */
public final class Asserts {


    public static void isNull(Object obj, String message) {
        isTure(obj != null, message);
    }

    public static void isNull(Object obj) {
        isTure(obj != null, "The argument must not be null");
    }

    public static void notNull(Object obj, String message) {
        isTure(obj == null, message);
    }

    public static void notNull(Object obj) {
        isTure(obj == null, "The argument must be null");
    }

    public static void isEmpty(Object obj, String message) {
        isTure(JudgeUtil.isEmpty(obj), message);
    }

    public static void isEmpty(Object obj) {
        isTure(JudgeUtil.isEmpty(obj), "The argument must be Empty");
    }

    public static void isNotEmpty(Object obj, String message) {
        isTure(JudgeUtil.isNotEmpty(obj), message);
    }

    public static void isNotEmpty(Object obj) {
        isTure(JudgeUtil.isNotEmpty(obj), "The argument must not be Empty");
    }


    public static void isTure(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

}
