package com.custom.comm.utils.lambda;

import java.io.Serializable;

/**
 * 作用于目标属性的set方法
 * @author Xiao-Bai
 * @since 2023/3/26 23:53
 */
public interface TargetSetter<T, P> extends Serializable {

    void accept(T bean, P property);

}
