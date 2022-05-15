package com.custom.action;

/**
 * @author Xiao-Bai
 * @date 2022/5/15 23:40
 * @desc:
 */
public class SqlUtil {

    public static String ifNull(String column) {
        return String.format("ifnull(%s, '')", column);
    }

    public static String ifNull(String column, String fieldName) {
        return String.format("ifnull(%s, '') %s", column, fieldName);
    }


}
