package com.custom.tools.data;

import com.custom.comm.utils.ReflectUtil;
import com.custom.comm.utils.lambda.LambdaUtil;
import com.custom.comm.utils.lambda.SFunction;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2022/11/25 0025 16:31
 */
@SuppressWarnings("unchecked")
public class DataSumming<T> {

    /**
     * 主数据
     */
    private final T total;

    /**
     * 待求和的集合
     */
    private final List<T> waitSumList;

    /**
     *字段描述信息
     */
    private final List<PropertyDescriptor> properties;

    public DataSumming(Class<T> targetClass, T total, List<T> waitSumList) throws IntrospectionException {
        this.total = total;
        this.waitSumList = waitSumList;
        this.properties = ReflectUtil.getProperties(targetClass);
    }

    public void start(SFunction<T, ?>... fields) throws InvocationTargetException, IllegalAccessException {
        if (total == null || waitSumList == null) {
            return;
        }

        for (SFunction<T, ?> field : fields) {

            // get方法名
            String implMethodName = LambdaUtil.getImplMethodName(field);
            // 字段类型
            Class<?> implFuncType = LambdaUtil.getImplFuncType(field);
            // get方法
            Method method = properties.stream()
                    .filter(op -> op.getReadMethod().getName().equals(implMethodName))
                    .map(PropertyDescriptor::getWriteMethod)
                    .findFirst()
                    .orElse(null);

            if (method != null && implFuncType != null) {
                if (Number.class.isAssignableFrom(implFuncType)) {
                    BigDecimal thisVal = waitSumList.stream()
                            .map(op -> {
                                Object val = field.apply(op);
                                if (val != null) {
                                    return new BigDecimal(String.valueOf(val));
                                } else {
                                    return BigDecimal.ZERO;
                                }
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);

                    // do BigDecimal
                    if (BigDecimal.class.isAssignableFrom(implFuncType)) {
                        method.invoke(total, total);
                    }
                    // do int
                    else if (Integer.class.isAssignableFrom(implFuncType)) {
                        method.invoke(total, thisVal.intValue());
                    }
                    // do long
                    else if (Long.class.isAssignableFrom(implFuncType)) {
                        method.invoke(total, thisVal.longValue());
                    }
                    // do double
                    else if (Double.class.isAssignableFrom(implFuncType)) {
                        method.invoke(total, thisVal.doubleValue());
                    }

                    // do other ignore...

                }
            }






        }

        
        
    }

}
