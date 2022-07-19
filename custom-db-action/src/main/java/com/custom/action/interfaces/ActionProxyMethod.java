package com.custom.action.interfaces;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/19 19:24
 * @Desc 分析执行的具体方法
 */
public interface ActionProxyMethod {

    Object execMethod(String execName, Object[] params);

}
