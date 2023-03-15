package com.custom.jdbc.executebody;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Xiao-Bai
 * @since 2023/3/15 17:46
 */
public class ExecuteBodyHelper {

    public static <T> SelectExecutorBody<T> createSelect(Class<T> t, String sql, Object... params) {
        return new SelectExecutorBody<>(t, sql, true, params);
    }

    public static <T> SelectExecutorBody<T> createSelect(Class<T> t, String sql, boolean b,  Object... params) {
        return new SelectExecutorBody<>(t, sql, b, params);
    }

    public static <K, V> SelectMapExecutorBody<K, V> createSelect(Class<K> kClass, Class<V> vClass, String sql, Object... params) {
        return new SelectMapExecutorBody<>(sql, true, params, kClass, vClass);
    }

    public static BaseExecutorBody createExecUpdate(String sql, Object... params) {
        return new BaseExecutorBody(sql, true, params);
    }

    public static BaseExecutorBody createExecUpdate(String sql, boolean b, Object... params) {
        return new BaseExecutorBody(sql, b, params);
    }

    public static <T> SaveExecutorBody<T> createSave(List<T> list, Field keyField, String sql, Object... params) {
        return new SaveExecutorBody<>(list, keyField, sql, true, params);
    }



}
