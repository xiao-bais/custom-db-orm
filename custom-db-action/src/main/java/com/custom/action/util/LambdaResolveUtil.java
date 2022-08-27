package com.custom.action.util;

import com.custom.action.condition.SFunction;
import com.custom.comm.exceptions.ExThrowsUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author Xiao-Bai
 * @date 2022/8/26 23:58
 * @desc lambda 表达式解析
 */
@Slf4j
public final class LambdaResolveUtil {

    /**
     * 解析 Lambda 表达式, 从SFunction中获取序列化的信息
     * @param function 表达式
     * @param <T> 对象类型
     * @return {@link SerializedLambda}
     */
    public static <T> SerializedLambda resolve(SFunction<T, ?> function) {
        Method writeMethod;
        SerializedLambda serializedLambda = null;
        try {
            // 从function中取出序列化方法
            writeMethod = function.getClass().getDeclaredMethod("writeReplace");
            writeMethod.setAccessible(true);
            serializedLambda = (SerializedLambda) writeMethod.invoke(function);
            writeMethod.setAccessible(false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (Objects.isNull(serializedLambda)) {
            ExThrowsUtil.toCustom("Unable to parse：" + function);
        }

        return serializedLambda;
    }

    /**
     * 解析 Lambda 表达式
     * @param function 表达式
     * @param <T> 对象类型
     * @return 对象class类
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getImplClass(SFunction<T, ?> function) {
        SerializedLambda serializedLambda = resolve(function);
        try {
            return (Class<T>) Class.forName(serializedLambda.getImplClass().replace('/', '.'));
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取lambda表达式函数的引用方法名
     * @param function 函数表达式
     * @param <T>
     * @return 方法名称
     */
    public static <T> String getImplMethodName(SFunction<T, ?> function) {
        SerializedLambda serializedLambda = resolve(function);
        return serializedLambda.getImplMethodName();
    }

    /**
     * 获取lambda表达式函数的引用方法返回类型
     * @param function 函数表达式
     * @param <T>
     * @return 方法返回类型:?
     */
    public static <T> Class<?> getImplFuncType(SFunction<T, ?> function) {
        SerializedLambda serializedLambda = resolve(function);
        String implMethodSignature = serializedLambda.getImplMethodSignature();
        implMethodSignature = implMethodSignature.substring(3, implMethodSignature.indexOf(";"));
        try {
            return Class.forName(implMethodSignature.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }





}
