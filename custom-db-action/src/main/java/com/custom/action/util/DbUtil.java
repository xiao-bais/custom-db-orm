package com.custom.action.util;

import com.custom.action.sqlparser.TableInfoCache;
import com.custom.comm.CustomUtil;
import com.custom.comm.RexUtil;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbRelated;
import com.custom.comm.enums.DbType;

import java.lang.reflect.Field;

/**
 * @author Xiao-Bai
 * @date 2022/4/18 21:48
 * @desc:
 */
public class DbUtil {

    /**
     * 该类是否存在主键
     */
    public static <T> boolean isKeyTag(Class<T> clazz){
        Field[] fields = TableInfoCache.getTableModel(clazz).getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class)) return true;
        }
        return false;
    }

    /**
     * 返回自定义包装的查询字段
     */
    public static String wrapperSqlColumn(String wrapperColumn, String fieldName, boolean isNullToEmpty) {
        boolean hasIfNull = RexUtil.hasRegex(wrapperColumn, RexUtil.sql_if_null);
        if (isNullToEmpty && !hasIfNull) {
            return DbUtil.ifNull(wrapperColumn, fieldName);
        }
        return String.format("%s %s", wrapperColumn, fieldName);
    }


    public static String ifNull(String column) {
        return String.format("ifnull(%s, '')", column);
    }

    public static String ifNull(String column, String fieldName) {
        return String.format("ifnull(%s, '') %s", column, fieldName);
    }

}
