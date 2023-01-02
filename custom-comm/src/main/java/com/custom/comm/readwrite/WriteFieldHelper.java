package com.custom.comm.readwrite;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.ConvertUtil;
import com.custom.comm.utils.CustomUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class WriteFieldHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 写入的值
     */
    private Object writeValue;

    /**
     * 写入的指定对象
     */
    private Object waitWriteEntity;

    /**
     * 写入对象中的指定属性字段名称
     */
    private String fieldName;

    /**
     * 当写入的字段类型为List/Set时, 泛型的类型
     */
    private Class<?> fieldType;

    private final static WriteFieldHelper thisHelper = new WriteFieldHelper();


    private WriteFieldHelper() {

    }

    public static WriteFieldHelper build() {
        thisHelper.clear();
        return thisHelper;
    }


    /**
     * 要写入的目标实体对象
     */
    public <T> WriteFieldHelper objType(T type) {
        Asserts.notNull(type, "The entity bean cannot be empty");
        thisHelper.waitWriteEntity = type;
        return thisHelper;
    }

    /**
     * 要写入的值
     */
    public WriteFieldHelper value(Object val) {
        if (val == null) {
            logger.warn("write val is null");
        }
        thisHelper.writeValue = val;
        return thisHelper;
    }

    /**
     * 要写入到对应的字段属性
     */
    public WriteFieldHelper field(String fieldName) {
        thisHelper.fieldName = fieldName;
        return thisHelper;
    }

    public WriteFieldHelper fieldType(Class<?> fieldType) {
        thisHelper.fieldType = fieldType;
        return thisHelper;
    }

    public boolean writeStart() throws NoSuchFieldException {
        Asserts.notNull(thisHelper.fieldName);
        Asserts.notNull(thisHelper.waitWriteEntity);
        boolean success = thisHelper.writeValue();
        thisHelper.clear();
        return success;
    }


    /**
     * 写入
     */
    private boolean writeValue() throws NoSuchFieldException {

        Class<?> waitSetClass = waitWriteEntity.getClass();
        try {

            PropertyDescriptor descriptor = new PropertyDescriptor(this.fieldName, waitSetClass);
            Method writeMethod = descriptor.getWriteMethod();
            Class<?>[] parameterTypes = writeMethod.getParameterTypes();

            if (parameterTypes.length > 1) {
                log.warn("When setting the value of field '{}', the set method of field '{}' cannot be found in '{}'",
                        this.fieldName, this.fieldName, waitSetClass.getName());
                log.warn("The set method with only one parameter is supported");
            }

            Class<?> setParamType = parameterTypes[0];

            if (Object.class.equals(setParamType) || setParamType.equals(this.writeValue.getClass())) {
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
                    throw new CustomCheckException("Field is inconsistent with parameter type of set method: " + writeMethod.toGenericString());
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

                if (!List.class.isAssignableFrom(setParamType) && !Set.class.isAssignableFrom(setParamType)) {
                    log.warn("Only 'java.util.List' and 'java.util.Set' settings are supported");
                    return false;
                }

                String valueStr = JSONArray.toJSONString(this.writeValue);
                JSONArray jsonArray = JSON.parseArray(valueStr);
                this.writeValue = jsonArray.toList(genericType);

                if (Set.class.isAssignableFrom(setParamType)) {
                    this.writeValue = new HashSet<>((ArrayList<?>) this.writeValue);
                }

            } else {
                this.writeValue = CustomUtil.jsonParseToObject(CustomUtil.objToJsonString(this.writeValue), setParamType);
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


    private void clear() {
        this.fieldName = null;
        this.fieldType = null;
        this.waitWriteEntity = null;
        this.writeValue = null;
    }
}
