package com.custom.action.proxy;

import com.custom.action.sqlparser.JdbcAction;
import com.custom.comm.annotations.check.CheckExecute;
import com.custom.comm.enums.ActionMethod;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/20 1:27
 * @Desc 自定义执行方法分析
 */
public class CustomTypeAction {

    private final static List<ActionMethod> actionMethods;
    private final static List<Method> JdbcActionMethods;
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
        actionMethods = Stream.of(ActionMethod.values()).collect(Collectors.toList());
        JdbcActionMethods = Stream.of(JdbcAction.class.getMethods())
                .filter(op -> op.isAnnotationPresent(CheckExecute.class))
                .collect(Collectors.toList());
    }

    public CustomTypeAction(String execMethodName, Integer paramNumber, Object[] parameters) {
        this.execMethodName = execMethodName;
        this.paramNumber = paramNumber;
        this.parameters = parameters;
    }


    /**
     * 分析具体执行的方法，并返回
     */
    public Method action() {

        ActionMethod actionMethod = actionMethods.stream().filter(op -> execMethodName.equals(op.getMethodName()) && parameters.length == op.getParamNumber())
                .findFirst().orElseThrow(() -> new CustomCheckException("Unknown execution method : " + execMethodName));



        return null;
    }


}
