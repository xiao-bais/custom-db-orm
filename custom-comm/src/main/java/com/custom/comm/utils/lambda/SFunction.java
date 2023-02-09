package com.custom.comm.utils.lambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 函数式接口，从序列化中获取对象的属性
 * @author    Xiao-Bai
 * @since  2022/3/3 14:36
 **/
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
