package com.custom.fieldfill;

import com.custom.dbconfig.CustomApplicationUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/29 15:29
 * @Desc：对于字段自动填充的工具处理
 **/
public class FieldAutoFillHandleUtils {

    /**
     * 通过java属性获取需要填充的字段值
     * @param t 填充class对象
     * @param proName 实体java属性名称
     * @return 需要填充的指定值
     */
    public static <T> Object getFillValue(Class<T> t, String proName) {
        AutoFillColumnHandler columnHandler = CustomApplicationUtils.getBean(AutoFillColumnHandler.class);
        if(ObjectUtils.isEmpty(columnHandler)) {
            return null;
        }
        List<TableFillObject> tableFillObjects = columnHandler.fillStrategy();
        if(ObjectUtils.isEmpty(tableFillObjects)) {
            return null;
        }
        Optional<TableFillObject> first = tableFillObjects.stream().filter(x -> x.getEntityClass().equals(t)).findFirst();
        if (first.isPresent()) {
            TableFillObject tableFillObject = first.get();
            Object propertyValue = tableFillObject.getTableFillMapper().get(proName);
            if(Objects.nonNull(propertyValue)) {
                return propertyValue;
            }
        }
        return null;
    }

    /**
     * 判断是否存在该实体的填充对象配置
     */
    public static <T> boolean exists(Class<T> t) {
        AutoFillColumnHandler columnHandler = CustomApplicationUtils.getBean(AutoFillColumnHandler.class);
        if(ObjectUtils.isEmpty(columnHandler)) {
            return false;
        }
        List<TableFillObject> tableFillObjects = columnHandler.fillStrategy();
        if(ObjectUtils.isEmpty(tableFillObjects)) {
            return false;
        }
        return tableFillObjects.stream().anyMatch(x -> x.getEntityClass().equals(t));
    }


    /**
     * 判断是否存在该实体中指定配置字段的填充值
     */
    public static <T> boolean exists(Class<T> t, String proName) {
        AutoFillColumnHandler columnHandler = CustomApplicationUtils.getBean(AutoFillColumnHandler.class);
        if(ObjectUtils.isEmpty(columnHandler)) {
            return false;
        }
        List<TableFillObject> tableFillObjects = columnHandler.fillStrategy();
        if(ObjectUtils.isEmpty(tableFillObjects)) {
            return false;
        }
        Optional<TableFillObject> tableFillObject = tableFillObjects.stream().filter(x -> x.getEntityClass().equals(t)).findFirst();
        if (tableFillObject.isPresent()) {
            Object value = tableFillObject.get().getTableFillMapper().get(proName);
            return Objects.nonNull(value);
        }
        return false;
    }


}