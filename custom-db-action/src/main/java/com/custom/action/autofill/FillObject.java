package com.custom.action.autofill;

import com.custom.comm.enums.FillStrategy;

import java.util.function.Supplier;

/**
 * @author Xiao-Bai
 * @since 2023/2/23 12:01
 */
public class FillObject {

    /**
     * java属性名称
     */
    private final String fieldName;

    /**
     * java属性名称对应的类型(可不填)
     */
    private Class<?> fieldType;

    /**
     * 设置值的供给函数
     */
    private final Supplier<Object> targetVal;

    /**
     * 填充策略
     */
    private final FillStrategy fillStrategy;


    private FillObject(String fieldName, Class<?> fieldType, Supplier<Object> targetVal, FillStrategy fillStrategy) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.targetVal = targetVal;
        this.fillStrategy = fillStrategy;
    }

    public static FillObject instance(String fieldName, Class<?> fieldType, Object targetVal, FillStrategy strategy) {
        return new FillObject(fieldName, fieldType, () -> targetVal, strategy);
    }

    public static FillObject instance(String fieldName, Class<?> fieldType, Supplier<Object> targetVal, FillStrategy strategy) {
        return new FillObject(fieldName, fieldType, targetVal, strategy);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public Supplier<Object> getTargetVal() {
        return targetVal;
    }

    public FillStrategy getFillStrategy() {
        return fillStrategy;
    }
}
