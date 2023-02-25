package com.custom.jdbc.handler;

import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.ReflectUtil;
import com.custom.jdbc.configuration.DbGlobalConfig;

import java.beans.PropertyDescriptor;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author  Xiao-Bai
 * @since  2022/11/13 22:08
 * 
 */
@SuppressWarnings("unchecked")
public class ResultSetTypeMappedHandler<T> {


    /**
     * 返回结果集中该行映射的对象
     * @param rs jdbc结果集对象
     * @return this type value
     */
    public T getTargetObject(ResultSet rs)
            throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        ResultSetMetaData rsMetaData = rs.getMetaData();
        Map<String, Object> resMap = new HashMap<>();
        // 循环取值
        for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
            // 查询的列名
            String label = rsMetaData.getColumnLabel(i + 1);
            // 列名对应的值
            Object value = rs.getObject(label);

            resMap.put(label, value);
        }

        return this.mappingTargetObject(resMap);
    }


    /**
     * 将map映射到目标类，并返回该类型的实例对象
     */
    private T mappingTargetObject(Map<String, Object> resultMap)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        // 实例化该对象，前提是需要存在无参构造，否则可能抛出异常
        T instance = ReflectUtil.getInstance(resClass);
        MappedTargetCache<T> mappedTargetCache = getMappedTargetCache(resClass);

        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {

            String label = entry.getKey();
            if (isUnderlineToCamel) {
                label = CustomUtil.underlineToCamel(label);
            }
            Object value = entry.getValue();
            if (value != null) {
                MappedTargetCache.FieldCache fieldCache = mappedTargetCache.findForName(label);

                if (fieldCache != null) {
                    TypeHandler<?> typeHandler = fieldCache.getTypeHandler();
                    // 映射时，最终的格式或类型转换
                    Object newValue = typeHandler.getTypeValue(value);
                    PropertyDescriptor descriptor = fieldCache.getDescriptor();
                    Method writeMethod = descriptor.getWriteMethod();
                    writeMethod.invoke(instance, newValue);
                }
            }

        }

        return instance;
    }

    private static <T> MappedTargetCache<T> getMappedTargetCache(Class<T> targetClass) {
        WeakReference<MappedTargetCache<?>> weakTargetCache = OBJECT_HANDLE_CACHE.get(targetClass);
        if (weakTargetCache == null || weakTargetCache.get() == null) {
            MappedTargetCache<T> mappedTargetCache = new MappedTargetCache<>(targetClass);
            OBJECT_HANDLE_CACHE.put(targetClass, new WeakReference<>(mappedTargetCache));
            return mappedTargetCache;
        }
        return (MappedTargetCache<T>) weakTargetCache.get();
    }


    /**
     * 将rs中的结果写入数组(泛型的类型仅限于常用的基本类型、字符串、日期类型)
     * @param res 待写入的数组
     * @param rs jdbc结果集对象
     */
    public void writeForArrays(Object res, ResultSet rs) throws SQLException {
        // 只取每一行的第一列
        int len = 0;
        while (rs.next()) {
            T value = this.getRsValue(rs, 1);
            Array.set(res, len, value);
            len ++;
        }
    }


    /**
     * 将rs中的结果写入map(泛型的类型仅限于常用的基本类型、字符串、日期)
     * @param map 待写入的map
     * @param rs jdbc结果集对象
     */
    public void writeForMap(Map<String, T> map, ResultSet rs) throws SQLException {
        ResultSetMetaData rsMetaData = rs.getMetaData();
        // 该类型的转换处理
        AbstractTypeHandler<T> thisTypeHandler = (AbstractTypeHandler<T>) this.getThisTypeHandler().getClone();
        thisTypeHandler.setUnderlineToCamel(isUnderlineToCamel);

        // 循环取值
        for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
            // 查询的列名
            String label = rsMetaData.getColumnLabel(i + 1);
            // 列名对应的值
            T value = thisTypeHandler.getTypeValue(rs.getObject(label));
            map.put(label, value);
        }
    }


    /**
     * 将rs中的结果写入集合(泛型的类型仅限于常用的基本类型、字符串、日期)
     * @param coll 待写入的list
     * @param rs jdbc结果集对象
     */
    public void writeForCollection(Collection<T> coll, ResultSet rs) throws SQLException {
        TypeHandler<T> typeHandler = (TypeHandler<T>) ALL_TYPE_HANDLER_CACHE.get(resClass);
        // 只取每一行的第一列
        while (rs.next()) {
            T val;
            if (typeHandler == null) {
                val = (T) rs.getObject(1);
            } else {
                val = typeHandler.getTypeValue(rs, 1);
            }
            coll.add(val);
        }
    }


    /**
     * 转换基础类型
     */
    public T getTargetValue(Object val) {
        TypeHandler<T> typeHandler = (TypeHandler<T>) ALL_TYPE_HANDLER_CACHE.get(resClass);
        return typeHandler.getTypeValue(val);
    }

    public T getTargetValue(ResultSet rs, int index) throws SQLException {
        TypeHandler<T> typeHandler = (TypeHandler<T>) ALL_TYPE_HANDLER_CACHE.get(resClass);
        if (typeHandler == null) {
            return (T) rs.getObject(index);
        }
        return (T) typeHandler.getTypeValue(rs, index);
    }


    private final Class<T> resClass;
    private final boolean isUnderlineToCamel;

    public ResultSetTypeMappedHandler(Class<T> resClass, DbGlobalConfig globalConfig) {
        this.resClass = resClass;
        this.isUnderlineToCamel = globalConfig.getStrategy().isUnderlineToCamel();
    }


    /**
     * 类型转换寄存
     */
    private final static Map<Class<?>, TypeHandler<?>> ALL_TYPE_HANDLER_CACHE = new ConcurrentHashMap<>();
    /**
     * 映射对象缓存
     */
    private static final Map<Class<?>, WeakReference<MappedTargetCache<?>>> OBJECT_HANDLE_CACHE = new ConcurrentHashMap<>();


    static {
        registerType(Integer.class, new IntegerTypeHandler());
        registerType(Long.class, new LongTypeHandler());
        registerType(Double.class, new DoubleTypeHandler());
        registerType(Float.class, new FloatTypeHandler());
        registerType(Character.class, new CharacterTypeHandler());
        registerType(Short.class, new ShortTypeHandler());
        registerType(Boolean.class, new BooleanTypeHandler());
        registerType(String.class, new StringTypeHandler());
        registerType(BigDecimal.class, new BigDecimalTypeHandler());
        registerType(Date.class, new DateTypeHandler());
        registerType(Object.class, new UnknownTypeHandler());
    }


    private static void registerType(Class<?> cls, TypeHandler<?> typeHandler) {
        ALL_TYPE_HANDLER_CACHE.put(cls, typeHandler);
    }

    public TypeHandler<T> getThisTypeHandler() {
        TypeHandler<T> typeHandler = (TypeHandler<T>) ALL_TYPE_HANDLER_CACHE.get(resClass);
        if (typeHandler == null) {
            typeHandler = (TypeHandler<T>) ALL_TYPE_HANDLER_CACHE.get(Object.class);
        }
        return typeHandler;
    }

    public static TypeHandler<?> getTargetTypeHandler(Class<?> targetCls) {
        TypeHandler<?> typeHandler = ALL_TYPE_HANDLER_CACHE.get(targetCls);
        if (typeHandler == null) {
            typeHandler =  ALL_TYPE_HANDLER_CACHE.get(Object.class);
        }
        return typeHandler;
    }

    /**
     * 获取结果集中的值
     */
    public T getRsValue(ResultSet rs, String column) throws SQLException {
        TypeHandler<?> typeHandler = ALL_TYPE_HANDLER_CACHE.get(resClass);
        if (typeHandler == null) {
            return (T) rs.getObject(column);
        }
        return (T) typeHandler.getTypeValue(rs, column);
    }

    /**
     * 获取结果集中的值
     */
    public T getRsValue(ResultSet rs, int index) throws SQLException {
        TypeHandler<?> typeHandler = ALL_TYPE_HANDLER_CACHE.get(resClass);
        if (typeHandler == null) {
            return (T) rs.getObject(index);
        }
        return (T) typeHandler.getTypeValue(rs, index);
    }
}
