package com.custom.utils;

import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import com.custom.dbconfig.ExceptionConst;
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

    /**
     * 该类是否存在DbTable注解
     */
    public static <T> void isTableTag(Class<T> clazz) {
        if(!clazz.isAnnotationPresent(DbTable.class)) throw new CustomCheckException(ExceptionConst.EX_DBTABLE__NOTFOUND + clazz.getName());
    }

    /**
     * 该类是否有多个DbKey注解
     */
    public static <T> void isMoreDbKey(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        int num = 0;
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class)) {
                num++;
            }
        }
        if(num > 1) throw new CustomCheckException(ExceptionConst.EX_PRIMARY_REPEAT + clazz.getName());
    }

    /**
     * 该类是否存在主键
     */
    public static <T> boolean isKeyTag(Class<T> clazz){
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class)) return true;
        }
        return false;
    }

}
