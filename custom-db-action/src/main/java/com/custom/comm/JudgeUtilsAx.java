package com.custom.comm;

import com.custom.dbconfig.DbCustomStrategy;

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

    public static boolean isBlank(final CharSequence cs) {
        if (cs == null) {
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
        if(null == t) throw new NullPointerException();
    }

    /**
    * 是否开启了逻辑删除字段
    */
    public static boolean isLogicDeleteOpen(DbCustomStrategy dbCustomStrategy) {
        if(dbCustomStrategy == null) {
            dbCustomStrategy = new DbCustomStrategy();
        }
       return isNotEmpty(dbCustomStrategy.getDbFieldDeleteLogic());
    }



}
