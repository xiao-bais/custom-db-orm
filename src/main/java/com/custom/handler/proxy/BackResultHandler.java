package com.custom.handler.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Xiao-Bai
 * @date 2021/11/7 21:32
 * @desc:
 */
public class BackResultHandler implements InvocationHandler {


    private Object target = null;


    public BackResultHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object res = null;
        res = method.invoke(target, args);


        return null;
    }
}
