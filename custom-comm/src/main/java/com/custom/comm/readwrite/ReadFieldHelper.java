package com.custom.comm.readwrite;

import com.alibaba.fastjson.JSONObject;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Xiao-Bai
 * @date 2022/8/24 1:07
 * 读取对象中指定属性的值
 */
@Slf4j
@SuppressWarnings("unchecked")
public class ReadFieldHelper<T, R> {

    /**
     * 待读取的java对象
     */
    private final T waitReadEntity;

    /**
     * 需要读取的字段属性名称
     */
    private final String fieldName;

    /**
     * 读取的值
     */
    private Object readValue;

    /**
     * 若需要转化类型，则转成的对象类型
     * <br/> 注意: 若转化的类型为List/set时，convertType为泛型的类型
     */
    private Class<R> convertType;

    public ReadFieldHelper(T waitReadEntity, String fieldName, Class<R> convertType) throws NoSuchFieldException {
        this.waitReadEntity = waitReadEntity;
        this.fieldName = fieldName;
        this.convertType = convertType;
        this.generateValue();
    }

    public ReadFieldHelper(T waitReadEntity, String fieldName) throws NoSuchFieldException {
        this.waitReadEntity = waitReadEntity;
        this.fieldName = fieldName;
        this.generateValue();
    }

    private void generateValue() throws NoSuchFieldException {
        Asserts.notNull(waitReadEntity, "The entity bean cannot be empty");
        Asserts.notNull(fieldName, "The fieldName bean cannot be empty");
        Map<String, Object> objectMap;
        try {
            if (waitReadEntity instanceof Map) {
                objectMap = (Map<String, Object>) waitReadEntity;
            } else {
                objectMap = CustomUtil.beanToMap(waitReadEntity);
            }
            if (objectMap.containsKey(fieldName)) {
                this.readValue = objectMap.get(fieldName);
            }else {
                throw new NoSuchFieldException(" Field: '" + fieldName + "' not found in object " + waitReadEntity.getClass());
            }
        }catch (IntrospectionException e) {
            log.error(e.toString(), e);
        }

    }

    /**
     * 直接获取值
     */
    public Object readObjectValue() {
        if(convertType != null) {
            return readCustomValue().orElse(null);
        }
        return readValue;
    }

    private Collection<?> convertCollection() {
        Class<?> valueType = this.readValue.getClass();
        Asserts.illegal(!Collection.class.isAssignableFrom(convertType),
                valueType + " cannot be cast to type " + this.convertType);
        Asserts.illegal(Collection.class.isAssignableFrom(valueType),
                valueType + " cannot be cast to type " + this.convertType);

        return (Collection<?>) this.readValue;
    }

    /**
     * 转成List类型的数据
     */
    public List<R> readListValue() {
        if (JudgeUtil.isEmpty(this.readValue)) {
            return new ArrayList<>();
        }
        Collection<?> collection = this.convertCollection();
        return collection.stream()
                .map(tmpValue -> JSONObject.parseObject(JSONObject.toJSONString(tmpValue), this.convertType))
                .collect(Collectors.toList());
    }

    /**
     * 转成Set类型的数据
     */
    public Set<R> readSetValue() {
        if (JudgeUtil.isEmpty(this.readValue)) {
            return new HashSet<>();
        }
        Collection<?> collection = this.convertCollection();
        return collection.stream()
                .map(tmpValue -> JSONObject.parseObject(JSONObject.toJSONString(tmpValue), this.convertType))
                .collect(Collectors.toSet());
    }

    /**
     * 转成指定类型的对象
     */
    public Optional<R> readCustomValue() {
        if (JudgeUtil.isEmpty(this.readValue)) {
            return Optional.empty();
        }
        Class<?> valueType = this.readValue.getClass();
        // 子类转父类
        if (this.convertType.isAssignableFrom(valueType)) {
            return Optional.of((R) this.readValue);
        }
        return Optional.of(JSONObject.parseObject(JSONObject.toJSONString(this.readValue), this.convertType));
    }

}
