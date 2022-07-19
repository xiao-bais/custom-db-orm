package com.custom.action.proxy;

import com.custom.comm.enums.ActionMethod;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/20 1:27
 * @Desc 自定义执行方法分析
 */
public class CustomMethodAnalysis {

    private final static ActionMethod[] actionMethods;
    /**
     * 执行方法名
     */
    private final String execMethodName;
    /**
     * 参数数量
     */
    private final Integer paramNumber;
    /**
     * 参数数组
     */
    private final Object[] parameters;

    static {
        actionMethods = ActionMethod.values();
    }

    public CustomMethodAnalysis(String execMethodName, Integer paramNumber, Object[] parameters) {
        this.execMethodName = execMethodName;
        this.paramNumber = paramNumber;
        this.parameters = parameters;
    }


}
