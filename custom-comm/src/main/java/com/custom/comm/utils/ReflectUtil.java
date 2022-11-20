package com.custom.comm.utils;

import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.readwrite.ReadFieldHelper;
import com.custom.comm.readwrite.WriteFieldHelper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Xiao-Bai
 * @date 2022/11/16 0016 17:00
 * 反射工具类
 */
public class ReflectUtil {


    /**
     * 将值写入指定对象中
     * @param writeValue 待写入的值
     * @param waitWriteEntity 写入的目标对象
     * @param fieldName 写入的目标属性
     * @param fieldType 写入的目标属性类型(若类型为Collection、则传入泛型的类型即可)
     * @param <T>
     * @return true/false
     * @throws NoSuchFieldException
     */
    public static <T> boolean writeFieldValue(Object writeValue,
                                              T waitWriteEntity,
                                              String fieldName,
                                              Class<?> fieldType) throws NoSuchFieldException {
        return WriteFieldHelper.build()
                .objType(waitWriteEntity)
                .value(writeValue)
                .field(fieldName)
                .fieldType(fieldType).writeStart();
    }



    /**
     * 读取对象的属性值
     * @param entity 目标对象
     * @param fieldName 目标属性
     * @param <T>
     * @return
     * @throws NoSuchFieldException
     */
    public static <T> Object readFieldValue(T entity, String fieldName) throws NoSuchFieldException {
        return ReadFieldHelper.build()
                .objType(entity)
                .field(fieldName)
                .readStart().readObjectValue();
    }




    /**
     * 获取一个类的所有属性（包括父类）
     */
    public static <T> List<Field> loadFields(Class<T> t, boolean checkDbField) {
        Class<?> clz = t;
        List<Field> fieldList = new ArrayList<>();
        while (clz != null) {
            Arrays.stream(clz.getDeclaredFields()).forEach(field -> {
                int modifiers = field.getModifiers();
                if (Modifier.isPrivate(modifiers)
                        && !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                    fieldList.add(field);
                }
            });
            clz = clz.getSuperclass();
        }
        if (fieldList.size() == 0 && checkDbField) {
            throw new CustomCheckException("@DbField not found inD " + t);
        }
        return fieldList;
    }


    public static <T> List<Field> loadFields(Class<T> t){
        return loadFields(t, true);
    }


    /**
     * 获取该类所有属性描述对象
     */
    public static <T> List<PropertyDescriptor> getProperties(Class<T> cls) throws IntrospectionException {
        Asserts.npe(cls);
        BeanInfo beanInfo = Introspector.getBeanInfo(cls);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        return Arrays.stream(propertyDescriptors).filter(x -> !x.getName().equals("class")).collect(Collectors.toList());
    }


    /**
     * 对象转map
     */
    public static Map<String, Object> beanToMap(Object bean) throws IntrospectionException {
        Class<?> thisClass = bean.getClass();
        Map<String, Object> resMap = new HashMap<>();
        List<PropertyDescriptor> propertyDescriptors = getProperties(thisClass);
        for (PropertyDescriptor property : propertyDescriptors) {
            String propertyName = property.getName();
            Method readMethod = property.getReadMethod();
            try {
                Object proValue = readMethod.invoke(bean);
                resMap.put(propertyName, proValue);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return resMap;
    }



    /**
     * 实例化该对象
     */
    public static <T> T getInstance(Class<T> t)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> constructor = t.getConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }


    /**
     * 获取get/set方法
     */
    public static <T> Method getMethod(Class<T> t, String methodName) throws IntrospectionException {

        Asserts.npe(methodName);
        List<PropertyDescriptor> propertyDescriptors = getProperties(t);
        List<Method> methodList = new ArrayList<>();
        propertyDescriptors.forEach(op -> {
            methodList.add(op.getReadMethod());
            methodList.add(op.getWriteMethod());
        });
        return methodList.stream()
                .filter(x -> x.getName().equals(methodName)).findFirst()
                .orElseThrow(() -> {
                    throw new CustomCheckException(String.format("%s method not found in %s", methodName, t));
                });
    }








}
