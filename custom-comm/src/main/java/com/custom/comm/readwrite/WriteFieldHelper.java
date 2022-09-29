package com.custom.comm.readwrite;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.custom.comm.Asserts;
import com.custom.comm.ConvertUtil;
import com.custom.comm.CustomUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2022/8/24 0:24
 * 将值写入java对象指定属性中
 */
@Slf4j
public class WriteFieldHelper<T> {

    /**
     * 写入的值
     */
    private Object writeValue;

    /**
     * 写入的指定对象
     */
    private final T waitWriteEntity;

    /**
     * 写入对象中的指定属性字段名称
     */
    private final String fieldName;

    /**
     * 当写入的字段类型为List/Set时, 泛型的类型
     */
    private final Class<?> fieldType;


    public WriteFieldHelper(Object writeValue, T waitWriteEntity, String fieldName, Class<?> fieldType) {
        Asserts.notNull(waitWriteEntity, "The entity bean cannot be empty");
        Asserts.notNull(fieldName, "The fieldName bean cannot be empty");
        this.writeValue = writeValue;
        this.waitWriteEntity = waitWriteEntity;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    /**
     * 写入
     */
    public boolean writeValue() throws NoSuchFieldException {

        Class<?> waitSetClass = waitWriteEntity.getClass();
        try {

            PropertyDescriptor descriptor = new PropertyDescriptor(this.fieldName, waitSetClass);
            Method writeMethod = descriptor.getWriteMethod();
            Class<?>[] parameterTypes = writeMethod.getParameterTypes();

            if (parameterTypes.length > 1) {
                log.warn("When setting the value of field '{}', the set method of field '{}' cannot be found in '{}'",
                        this.fieldName, this.fieldName, waitSetClass);
                log.warn("The set method with only one parameter is supported");
            }

            Class<?> setParamType = parameterTypes[0];
            if (Object.class.equals(setParamType)) {
                writeMethod.invoke(waitWriteEntity, writeValue);
                return true;
            }

            if (CustomUtil.isBasicClass(setParamType)) {
                this.writeValue = ConvertUtil.transToObject(setParamType, this.writeValue);

            } else if (Collection.class.isAssignableFrom(setParamType)) {
                Type[] actualTypeArguments = ((ParameterizedTypeImpl) writeMethod.getGenericParameterTypes()[0]).getActualTypeArguments();
                if (actualTypeArguments.length == 0
                        || (CustomUtil.isNotAllowedGenericType((Class<?>) actualTypeArguments[0])
                        && Object.class.equals(this.fieldType))) {
                    ExThrowsUtil.toCustom("Field is inconsistent with parameter type of set method: " + writeMethod.toGenericString());
                }

                // 如果是集合类型的话，获取到集合中的泛型类型
                Class<?> genericType = (Class<?>) actualTypeArguments[0];
                if (Object.class.equals(genericType)) {
                    Collection<?> collection = (Collection<?>) this.writeValue;
                    Collection<Object> valueList;
                    if (List.class.isAssignableFrom(setParamType)) {
                        valueList = new ArrayList<>(collection);
                    } else {
                        valueList = new HashSet<>(collection);
                    }
                    writeMethod.invoke(this.waitWriteEntity, valueList);
                    return true;
                }

                if (!List.class.isAssignableFrom(setParamType) && !Set.class.isAssignableFrom(setParamType)){
                    log.warn("Only 'java.util.List' and 'java.util.Set' settings are supported");
                    return false;
                }

                String valueStr = JSONArray.toJSONString(this.writeValue);
                this.writeValue = JSONArray.parseArray(valueStr, genericType);
                if (Set.class.isAssignableFrom(setParamType)) {
                    this.writeValue = new HashSet<>((ArrayList<?>) this.writeValue);
                }

            } else {
                this.writeValue = JSONObject.parseObject(JSONObject.toJSONString(this.writeValue), setParamType);
            }

            writeMethod.invoke(this.waitWriteEntity, this.writeValue);
            return true;

        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error(e.toString(), e);
            return false;

        } catch (IntrospectionException e) {
            log.error(e.toString(), e);
            throw new NoSuchFieldException(" Field: '" + this.fieldName + "' not found in object " + fieldType);
        }
    }
}
