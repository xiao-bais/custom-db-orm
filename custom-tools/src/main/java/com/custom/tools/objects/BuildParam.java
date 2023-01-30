package com.custom.tools.objects;

/**
 * @author Xiao-Bai
 * @date 2023/1/28 11:02
 */
public interface BuildParam<T, P> {

    void accept(T t, P p);

}
