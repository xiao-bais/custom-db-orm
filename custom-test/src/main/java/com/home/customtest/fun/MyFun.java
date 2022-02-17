package com.home.customtest.fun;

/**
 * @Author Xiao-Bai
 * @Date 2022/1/25 22:06
 * @Desc
 */
@FunctionalInterface
public interface MyFun<T, S> {

    S test(T t);
}
