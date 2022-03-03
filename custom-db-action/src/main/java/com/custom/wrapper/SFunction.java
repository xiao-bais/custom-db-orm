package com.custom.wrapper;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 14:36
 * @Desc：函数式接口，从序列化中获取对象的属性
 **/
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
