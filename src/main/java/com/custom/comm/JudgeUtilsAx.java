package com.custom.comm;

import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import com.custom.exceptions.ExceptionConst;
import com.custom.exceptions.CustomCheckException;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/16 0016 1:44
 * @Version 1.0
 * @Description JudgeUtilsAx
 */
public class JudgeUtilsAx {

    public static boolean isEmpty(String el) {
        return null == el || "".equals(el) || el.length() == 0;
    }

    public static boolean isNotEmpty(String el) {
        return !isEmpty(el);
    }

    public static boolean isEmpty(Object el) {
        if(el == null) return true;

        if(el instanceof String)
            return isEmpty(el.toString());

        if(el instanceof CharSequence)
            return ((CharSequence) el).length() == 0;

        if(el instanceof Collection)
            return ((Collection) el).isEmpty();

        if (el instanceof Map)
            return ((Map) el).isEmpty();

        if (el instanceof Object[]) {
            Object[] object = (Object[]) el;
            if (object.length == 0) {
                return true;
            }
            boolean empty = true;
            for (int i = 0; i < object.length; i++) {
                if (!isEmpty(object[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }

    public static boolean isNotEmpty(Object el) {
        return !isEmpty(el);
    }


    public static <T> void checkObjNotNull(Class<T> t) throws NullPointerException {
        if(null == t) throw new NullPointerException();
    }



}
