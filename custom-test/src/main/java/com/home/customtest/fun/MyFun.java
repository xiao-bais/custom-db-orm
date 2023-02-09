package com.home.customtest.fun;

/**
 * @author  Xiao-Bai
 * @since  2022/1/25 22:06
 * @Desc
 */
@FunctionalInterface
public interface MyFun<T, S> {

    S test(T t);
}
