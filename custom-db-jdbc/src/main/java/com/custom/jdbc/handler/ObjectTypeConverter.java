package com.custom.jdbc.handler;

import com.custom.comm.exceptions.CustomCheckException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Xiao-Bai
 * @date 2022/11/13 1:50
 * 类型转换器
 */
@SuppressWarnings("unchecked")
public class ObjectTypeConverter<T> {

    /**
     * 获取值
     */
    public T getValue() {
        if (Object.class.equals(val.getClass())) {
            return (T) val;
        }
        TypeHandler<?> typeHandler = ALL_TYPE_HANDLER_CACHE.get(toTypeClass);
        return (T) typeHandler.getTypeValue(val);
    }

    /**
     * 获取不为空的值
     */
    public T getNonNullValue() {
        if (Object.class.equals(val.getClass())) {
            return (T) val;
        }
        TypeHandler<?> typeHandler = ALL_TYPE_HANDLER_CACHE.get(toTypeClass);
        return (T) typeHandler.getTypeNoNullValue(val);
    }

    /**
     * 获取结果集中的值
     */
    public T getRsValue(ResultSet rs, String column) throws SQLException {
        TypeHandler<?> typeHandler = ALL_TYPE_HANDLER_CACHE.get(toTypeClass);
        if (typeHandler == null) {
            return (T) rs.getObject(column);
        }
        return (T) typeHandler.getTypeValue(rs, column);
    }

    /**
     * 获取结果集中的值
     */
    public T getRsValue(ResultSet rs, int index) throws SQLException {
        TypeHandler<?> typeHandler = ALL_TYPE_HANDLER_CACHE.get(toTypeClass);
        if (typeHandler == null) {
            return (T) rs.getObject(index);
        }
        return (T) typeHandler.getTypeValue(rs, index);
    }


    /**
     * 需转换的类型
     */
    private Class<T> toTypeClass;

    /**
     * 待转换的值
     */
    private Object val;

    /**
     * 类型转换寄存
     */
    private final static Map<Class<?>, TypeHandler<?>> ALL_TYPE_HANDLER_CACHE = new ConcurrentHashMap<>();


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
    }


    private static void registerType(Class<?> cls, TypeHandler<?> typeHandler) {
        ALL_TYPE_HANDLER_CACHE.put(cls, typeHandler);
    }

    public ObjectTypeConverter(Class<T> toTypeClass, Object val) {
        this.toTypeClass = toTypeClass;
        this.val = val;
    }

    public ObjectTypeConverter(Class<T> toTypeClass) {
        this.toTypeClass = toTypeClass;
    }

    public ObjectTypeConverter() {
    }

    public TypeHandler<T> getThisTypeHandler() {
        TypeHandler<T> typeHandler = (TypeHandler<T>) ALL_TYPE_HANDLER_CACHE.get(toTypeClass);
        if (typeHandler == null) {
            throw new UnsupportedOperationException("This type of conversion processing is not supported temporarily");
        }
        return typeHandler;
    }

    public TypeHandler<?> getTargetTypeHandler(Class<?> targetCls) {
        TypeHandler<?> typeHandler = ALL_TYPE_HANDLER_CACHE.get(targetCls);
        if (typeHandler == null) {
            throw new UnsupportedOperationException("This type of conversion processing is not supported temporarily");
        }
        return typeHandler;
    }

    public static void main(String[] args) {

        ObjectTypeConverter<Integer> converter = new ObjectTypeConverter<>(Integer.class, 5.5);
        Integer value = converter.getValue();
        System.out.println(value);

    }

}
