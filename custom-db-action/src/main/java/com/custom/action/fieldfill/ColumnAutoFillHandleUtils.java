package com.custom.action.fieldfill;

import com.custom.comm.utils.CustomApplicationUtil;
import com.custom.comm.utils.RexUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/29 15:29
 * @Desc：对于字段自动填充的工具处理
 **/
public class ColumnAutoFillHandleUtils {

    private static final Logger logger = LoggerFactory.getLogger(ColumnAutoFillHandleUtils.class);

    /**
     * 通过java属性获取需要填充的字段值
     * @param t 填充class对象
     * @param proName 实体java属性名称
     * @return 需要填充的指定值
     */
    public static <T> Object getFillValue(Class<T> t, String proName) {
        if(RexUtil.hasRegex(proName, RexUtil.back_quotes)) {
            proName = RexUtil.regexStr(proName, RexUtil.back_quotes);
        }
        ColumnFillAutoHandler columnHandler = CustomApplicationUtil.getBean(ColumnFillAutoHandler.class);
        if(ObjectUtils.isEmpty(columnHandler)) {

            return null;
        }
        List<TableFillObject> tableFillObjects = columnHandler.fillStrategy();
        if(ObjectUtils.isEmpty(tableFillObjects)) {
            return null;
        }
        // 若无法找到该类的指定填充字段，可尝试去父类中查找
        TableFillObject fillObject = tableFillObjects.stream().filter(x -> x.getEntityClass().equals(t)).findFirst().orElse(null);
        if (Objects.isNull(fillObject)) {
            fillObject = tableFillObjects.stream().filter(x -> x.getEntityClass().isAssignableFrom(t)).findFirst().orElse(null);
            if (Objects.isNull(fillObject)) {
                return null;
            }
        }
        Object propertyValue = fillObject.getTableFillMapper().get(proName);
        if(Objects.isNull(propertyValue)) {
            String error = String.format("%s中未找到属性：%s的指定填充值", t, proName);
            if(fillObject.getNotFoundFieldThrowException()) {
                ExThrowsUtil.toCustom(error);
            }
            logger.error(error);
        }
        return propertyValue;
    }

    /**
     * 判断是否存在该实体的填充对象配置
     */
    public static <T> boolean exists(Class<T> t) {
        ColumnFillAutoHandler columnHandler = CustomApplicationUtil.getBean(ColumnFillAutoHandler.class);
        if(ObjectUtils.isEmpty(columnHandler)) {
            return false;
        }
        List<TableFillObject> tableFillObjects = columnHandler.fillStrategy();
        if(ObjectUtils.isEmpty(tableFillObjects)) {
            return false;
        }
        return tableFillObjects.stream().anyMatch(x -> x.getEntityClass().isAssignableFrom(t));
    }


    /**
     * 判断是否存在该实体中指定配置字段的填充值
     */
    public static <T> boolean exists(Class<T> t, String proName) {
        try {
            ColumnFillAutoHandler columnHandler = CustomApplicationUtil.getBean(ColumnFillAutoHandler.class);
            if(ObjectUtils.isEmpty(columnHandler)) {
                return false;
            }
            List<TableFillObject> tableFillObjects = columnHandler.fillStrategy();
            if(ObjectUtils.isEmpty(tableFillObjects)) {
                return false;
            }
            // 若无法找到该类的指定填充字段，可尝试去父类中查找
            Optional<TableFillObject> tableFillObject = tableFillObjects.stream().filter(x -> x.getEntityClass().equals(t)).findFirst();
            if (!tableFillObject.isPresent()) {
                tableFillObject = tableFillObjects.stream().filter(x -> x.getEntityClass().isAssignableFrom(t)).findFirst();
            }
            if (tableFillObject.isPresent()) {
                Object value = tableFillObject.get().getTableFillMapper().get(proName);
                return Objects.nonNull(value);
            }
        }catch (NoSuchBeanDefinitionException e) {
            return false;
        }
        return false;
    }


}
