package com.custom.comm.readwrite;

import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.ReflectUtil;
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
public class ReadFieldHelper {

    /**
     * 待读取的java对象
     */
    private Object waitReadEntity;

    /**
     * 需要读取的字段属性名称
     */
    private String fieldName;

    /**
     * 读取的值
     */
    private Object readValue;

    /**
     * 若需要转化类型，则转成的对象类型
     * <br/> 注意: 若转化的类型为List/set时，convertType为泛型的类型
     */
    private Class<?> convertType;

    private final static ReadFieldHelper thisHelper = new ReadFieldHelper();


    public static ReadFieldHelper build() {
        thisHelper.clear();
        return thisHelper;
    }


    public ReadFieldHelper field(String field) {
        Asserts.notNull(field);
        thisHelper.fieldName = field;
        return thisHelper;
    }

    public <T> ReadFieldHelper objType(T objType) {
        Asserts.notNull(objType);
        thisHelper.waitReadEntity = objType;
        return thisHelper;
    }

    public <C> ReadFieldHelper convert(Class<C> type) {
        Asserts.notNull(convertType);
        thisHelper.convertType = type;
        return thisHelper;
    }

    public ReadFieldHelper readStart() throws NoSuchFieldException {
        Asserts.notNull(fieldName);
        Asserts.notNull(waitReadEntity);
        thisHelper.generateValue();
        return thisHelper;
    }


    public ReadFieldHelper() {

    }

    private void generateValue() throws NoSuchFieldException {
        Asserts.notNull(waitReadEntity, "The entity bean cannot be empty");
        Asserts.notNull(fieldName, "The fieldName bean cannot be empty");
        Map<String, Object> objectMap;
        try {
            if (waitReadEntity instanceof Map) {
                objectMap = (Map<String, Object>) waitReadEntity;
            } else {
                objectMap = ReflectUtil.beanToMap(waitReadEntity);
            }
            if (objectMap.containsKey(fieldName)) {
                this.readValue = objectMap.get(fieldName);
            } else {
                throw new NoSuchFieldException(" Field: '" + fieldName + "' not found in object " + waitReadEntity.getClass());
            }
        } catch (IntrospectionException e) {
            log.error(e.toString(), e);
        }

    }

    /**
     * 直接获取值
     */
    public Object readObjectValue() {
        if (convertType != null) {
            return readCustomValue().orElse(null);
        }
        return readValue;
    }

    private <R> Collection<R> convertCollection() {
        if (readValue == null) {
            return new ArrayList<>();
        }
        Class<?> valueType = this.readValue.getClass();
        Asserts.illegal(!Collection.class.isAssignableFrom(convertType),
                valueType + " cannot be cast to type " + this.convertType);
        Asserts.illegal(Collection.class.isAssignableFrom(valueType),
                valueType + " cannot be cast to type " + this.convertType);

        return (Collection<R>) this.readValue;
    }

    /**
     * 转成List类型的数据
     * convertType为泛型的类型
     */
    public <R> List<R> readListValue(Class<R> type) {
        if (readValue == null) {
            return new ArrayList<>();
        }
        Asserts.notNull(type);
        thisHelper.convertType = type;
        Collection<R> collection = this.convertCollection();
        return collection.stream()
                .map(tmpValue -> (R) CustomUtil.jsonParseToObject(CustomUtil.objToJsonString(tmpValue), thisHelper.convertType))
                .collect(Collectors.toList());
    }

    /**
     * 转成Set类型的数据
     */
    public <R> Set<R> readSetValue() {
        if (this.readValue == null) {
            return new HashSet<>();
        }
        Collection<?> collection = this.convertCollection();
        return collection.stream()
                .map(tmpValue -> (R) CustomUtil.jsonParseToObject(CustomUtil.objToJsonString(tmpValue), thisHelper.convertType))
                .collect(Collectors.toSet());
    }

    /**
     * 转成指定类型的对象
     */
    public <R> Optional<R> readCustomValue() {
        if (JudgeUtil.isEmpty(this.readValue)) {
            return Optional.empty();
        }
        Class<?> valueType = this.readValue.getClass();
        // 子类转父类
        if (this.convertType.isAssignableFrom(valueType)) {
            return Optional.of((R) this.readValue);
        }
        R obj = (R) CustomUtil.jsonParseToObject(CustomUtil.objToJsonString(thisHelper.readValue), thisHelper.convertType);
        return Optional.of(obj);
    }


    public void clear() {
        this.waitReadEntity = null;
        this.fieldName = null;
        this.convertType = null;
        this.readValue = null;
    }

}
