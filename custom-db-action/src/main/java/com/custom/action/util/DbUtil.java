package com.custom.action.util;

import com.custom.action.annotations.DbKey;
import com.custom.action.annotations.DbRelated;
import com.custom.action.annotations.DbTable;
import com.custom.action.enums.DbMediaType;
import com.custom.comm.CustomUtil;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExceptionConst;

import java.lang.reflect.Field;

/**
 * @author Xiao-Bai
 * @date 2022/4/18 21:48
 * @desc:
 */
public class DbUtil {

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
     * 检查主键是否自增
     */
    public static boolean checkPrimaryKeyIsAutoIncrement(DbMediaType dbType){
        return "int".equals(dbType.getType())
                || "float".equals(dbType.getType())
                || "bigint".equals(dbType.getType());
    }

    /**
     * 该类是否有@DbRelation注解
     */
    public static <T> boolean isDbRelationTag(Class<T> t) {
        Field[] fields = CustomUtil.getFields(t);
        for (Field field : fields) {
            if(field.isAnnotationPresent(DbRelated.class)) return true;
        }
        return false;
    }

    /**
     * 该类是否存在主键
     */
    public static <T> boolean isKeyTag(Class<T> clazz){
        Field[] fields = CustomUtil.getFields(clazz);
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class)) return true;
        }
        return false;
    }

}
