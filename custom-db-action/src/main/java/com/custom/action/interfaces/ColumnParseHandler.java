package com.custom.action.interfaces;

import com.custom.action.condition.SFunction;
import com.custom.action.sqlparser.ColumnFunctionMap;
import com.custom.comm.CustomUtil;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/11 2:07
 * @Desc 用于条件构造器中的字段解析接口定义
 */
public interface ColumnParseHandler<T> {


    /**
     * 获取当前解析的实体Class对象
     */
    Class<T> getThisClass();

    /**
     * 获取当前解析的实体class对象中的所有字段属性(包含父类的字段，所有字段仅限于private，并且未被final、static修饰的属性)
     * <p>
     *     若当前使用非custom-x，则需手动实现该方法
     * </p>
     */
   default List<Field> loadFields() {
       Class<T> thisClass = getThisClass();
       Field[] loadFields = CustomUtil.loadFields(thisClass, false);
       return Arrays.stream(loadFields).collect(Collectors.toList());
   }

    /**
     * 解析函数接口后，返回解析后的java字段属性
     * @param funcList 接口函数数组
     * @return
     */
   default List<String> parseToFields(List<SFunction<T, ?>> funcList) {
       List<String> parseFieldList = new ArrayList<>();
       for (SFunction<T, ?> func : funcList) {
           parseFieldList.add(this.parseToField(func));
       }
       return parseFieldList;
   }

    /**
     * 解析函数接口后，返回解析后的java字段属性
     * @param func
     * @return
     */
    String parseToField(SFunction<T, ?> func);

    /**
     * 解析函数接口后，返回解析后的java字段对应的sql表字段
     * @param funcList 接口函数数组
     * @return
     */
   default List<String> parseToColumns(List<SFunction<T, ?>> funcList) {
       List<String> parseColumnList = new ArrayList<>();
       for (SFunction<T, ?> func : funcList) {
           parseColumnList.add(this.parseToColumn(func));
       }
       return parseColumnList;
   }

    /**
     * 解析函数接口后，返回解析后的java字段属性
     * @param func
     * @return
     */
    String parseToColumn(SFunction<T, ?> func);


    /**
     * 从SFunction中获取序列化的信息
     */
    default SerializedLambda parseSerializedLambda(SFunction<T, ?> fun) {
        Method writeMethod;
        SerializedLambda serializedLambda = null;
        try {
            // 从function中取出序列化方法
            writeMethod = fun.getClass().getDeclaredMethod("writeReplace");
            writeMethod.setAccessible(true);
            serializedLambda = (SerializedLambda) writeMethod.invoke(fun);
            writeMethod.setAccessible(false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (Objects.isNull(serializedLambda)) {
            ExThrowsUtil.toCustom("无法解析：" + fun);
        }

        return serializedLambda;
    }

    /**
     * 函数解析缓存
     */
    ColumnFunctionMap<?>  createFunctionMapsCache(SFunction<T, ?> function, String implMethodName);

}
