package com.custom.comm.utils;

import com.custom.comm.exceptions.CustomCheckException;

/**
 * 断言工具类
 * @author   Xiao-Bai
 * @since  2022/7/21 23:58
 */
public final class AssertUtil {


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

    public static void empty(Object obj, String message) {
        isTure(JudgeUtil.isNotEmpty(obj), message);
    }

    public static void empty(Object obj) {
        isTure(JudgeUtil.isNotEmpty(obj), "The argument must be Empty");
    }

    public static void notEmpty(Object obj, String message) {
        isTure(JudgeUtil.isEmpty(obj), message);
    }

    public static void notEmpty(Object obj) {
        isTure(JudgeUtil.isEmpty(obj), "The argument must not be Empty");
    }

    public static void npe(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }

    public static void npe(Object obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
    }

    public static void isTure(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void illegal(boolean bool, String message) {
        isTure(bool, message);
    }

    public static void unSupportOp(boolean bool, String message) {
        if (bool) {
            throw new UnsupportedOperationException(message);
        }
    }

    public static void allowed(boolean bool, String message) {
        if (!bool) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void cce(boolean bool, String message) {
        if (!bool) {
            throw new CustomCheckException(message);
        }
    }

}
