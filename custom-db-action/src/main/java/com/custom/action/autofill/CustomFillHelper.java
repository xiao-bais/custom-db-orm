package com.custom.action.autofill;

import com.custom.comm.enums.FillStrategy;
import com.custom.comm.exceptions.CustomCheckException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Xiao-Bai
 * @since 2023/2/23 12:53
 */
public class CustomFillHelper<T> {

    private final Class<T> target;
    private final CustomTableFill tableFill;


    public CustomFillHelper(Class<T> target, CustomTableFill tableFill) {
        this.target = target;
       this.tableFill = tableFill;
    }

    /**
     * 获取需要填充的值
     * @param fieldName java属性名称
     * @param fieldType java属性类型
     * @param strategy 填充的策略
     */
    public Object getFillValue(String fieldName, Class<?> fieldType, FillStrategy strategy) {
        if (tableFill.isFillEmpty() || strategy == FillStrategy.DEFAULT) {
            return null;
        }
        FillObject fillObject = this.getFillObject(fieldName, fieldType);
        // 若填充对象为null 或填充值为null
        if (fillObject == null || fillObject.getTargetVal() == null) {
            return null;
        }
        // 若填充策略不匹配
        if (!strategy.name().contains(fillObject.getFillStrategy().name())) {
             return null;
        }
        return fillObject.getTargetVal().get();
    }


    /**
     * 获取该字段的填充对象
     */
    private FillObject getFillObject(String fieldName, Class<?> fieldType) {
        FillObject fillObject;
        List<FillObject> fillObjects = tableFill.getFillObjects().stream()
                .filter(e -> e.getFieldName().equals(fieldName))
                .collect(Collectors.toList());
        if (fillObjects.isEmpty()) {
            return null;
        }
        // 查询到可匹配该字段的多个类型填充对象
        else if (fillObjects.size() > 1) {
            if (fieldType == null) {
                String multiTypes = fillObjects.stream()
                        .map(e -> e.getFieldType().getName())
                        .collect(Collectors.joining(","));
                throw new CustomCheckException("无法匹配，查询到 [" + target.getName() + "." + fieldName + "]存在多个配置类型:(" + multiTypes + ")");
            }
            // 若字段名称一致，则根据字段类型判定目标填充对象
            fillObject = fillObjects.stream()
                    .filter(e -> e.getFieldType().isAssignableFrom(fieldType))
                    .findFirst().orElse(null);
        } else {
            fillObject = fillObjects.get(0);
        }
        return fillObject;
    }



}
