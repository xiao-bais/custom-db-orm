package com.custom.comm.utils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author   Xiao-Bai
 * @since  2021/1/16 0016 1:44
 */
public class JudgeUtil {

    public static boolean isEmpty(String el) {
        return null == el
                || "".equals(el)
                || el.length() == 0
                || el.trim().length() == 0;
    }

    public static boolean isNotEmpty(String el) {
        return !isEmpty(el);
    }

    public static boolean isEmpty(Object el) {
        if(el == null || "".equals(el.toString())) return true;

        if(el instanceof String)
            return isEmpty(el.toString().trim());

        if(el instanceof CharSequence)
            return ((CharSequence) el).length() == 0;

        if(el instanceof Collection)
            return ((Collection<?>) el).isEmpty();

        if (el instanceof Map)
            return ((Map<?,?>) el).isEmpty();

        if (el.getClass().isArray()) {
            int len = Array.getLength(el);
            if(len == 0) return true;
            boolean empty = true;
            for (int i = 0; i < len; i++) {
                if (!isEmpty(Array.get(el, i))) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }

    public static boolean isBlank(final CharSequence cs) {
        if (Objects.isNull(cs)) {
            return true;
        }
        int l = cs.length();
        if (l > 0) {
            for (int i = 0; i < l; i++) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs){return !isBlank(cs);}

    public static boolean isNotEmpty(Object el) {
        return !isEmpty(el);
    }


    public static <T> void checkObjNotNull(Class<T> t) throws NullPointerException {
        if(Objects.isNull(t)) throw new NullPointerException();
    }

    public static void checkObjNotNull(Object val) throws NullPointerException {
        if(Objects.isNull(val)) throw new NullPointerException();
    }

    public static void checkObjNotNull(Object... vals) throws NullPointerException {
        if (Arrays.stream(vals).anyMatch(Objects::isNull)){
            throw new NullPointerException();
        }
    }

    public static boolean isValid(Integer val) {
        return val != null && val != 0;
    }

    public static boolean isInValid(Integer val) {
        return !isValid(val);
    }

    public static boolean isValid(Long val) {
        return val != null && val != 0;
    }

    public static boolean isInValid(Long val) {
        return !isValid(val);
    }

    public static boolean isValid(BigDecimal val) {
        return val != null && !val.equals(BigDecimal.ZERO);
    }

    public static boolean isInValid(BigDecimal val) {
        return !isValid(val);
    }

    public static boolean isValid(Boolean val) {
        return val != null && val;
    }

    public static boolean isInValid(Boolean val) {
        return !isValid(val);
    }



}
