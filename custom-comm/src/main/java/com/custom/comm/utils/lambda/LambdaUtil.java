package com.custom.comm.utils.lambda;

import com.custom.comm.exceptions.CustomCheckException;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * lambda 表达式解析
 * @author    Xiao-Bai
 * @since  2022/8/26 23:58
 */
@Slf4j
public final class LambdaUtil {

    /**
     * FUNCTION 函数的缓存
     */
    private final static Map<String, WeakReference<SerializedLambda>> FUNCTION_CACHE = new ConcurrentHashMap<>();

    /**
     * 解析 Lambda 表达式, 从SFunction中获取序列化的信息
     * @param function 表达式 -> Model::getXXX
     * @param <T> 对象类型
     * @return {@link SerializedLambda}
     */
    public static <T> SerializedLambda resolve(SFunction<T, ?> function) {
        return getSerializedLambda(function);
    }

    /**
     * 解析 Lambda 表达式, 从SFunction中获取序列化的信息
     * @param setter 表达式 -> Model::setXXX
     * @param <T> 对象类型
     * @return {@link SerializedLambda}
     */
    public static <T> SerializedLambda resolve(TargetSetter<T, ?> setter) {
        return getSerializedLambda(setter);
    }

    /**
     * 获取lambda的序列化信息
     */
    private static SerializedLambda getSerializedLambda(Object obj) {
        Class<?> aClass = obj.getClass();
        String canonicalName = aClass.getCanonicalName();
        return Optional.ofNullable(FUNCTION_CACHE.get(canonicalName))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    SerializedLambda serializedLambda = startParse(obj);
                    FUNCTION_CACHE.put(canonicalName, new WeakReference<>(serializedLambda));
                    return serializedLambda;
                });
    }

    /**
     * 解析函数
     */
    private static SerializedLambda startParse(Object obj) {
        SerializedLambda serializedLambda = null;
        Method writeMethod;
        try {
            // 从function中取出序列化方法
            writeMethod = obj.getClass().getDeclaredMethod("writeReplace");
            writeMethod.setAccessible(true);
            serializedLambda = (SerializedLambda) writeMethod.invoke(obj);
            writeMethod.setAccessible(false);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        if (Objects.isNull(serializedLambda)) {
            throw new CustomCheckException("Unable to parse：" + obj);
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

    public static <T, P> String getImplMethodName(TargetSetter<T, P> setter) {
        SerializedLambda serializedLambda = resolve(setter);
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
        if (implMethodSignature.length() <= 5 && !implMethodSignature.contains("/")) {
            String implMethodName = serializedLambda.getImplMethodName();
            log.error(implMethodName + " ==> 该方法或方法对应的属性使用了基础类型，会造成无法解析，请将属性或方法的返回值类型(参数类型) 换成基础类型对应的包装类");
        }
        implMethodSignature = implMethodSignature.substring(3, implMethodSignature.indexOf(";"));
        try {
            return Class.forName(implMethodSignature.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }





}
