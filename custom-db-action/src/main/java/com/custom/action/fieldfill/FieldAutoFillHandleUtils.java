package com.custom.action.fieldfill;

import com.custom.comm.CustomApplicationUtil;
import com.custom.comm.RexUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(FieldAutoFillHandleUtils.class);

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
        AutoFillColumnHandler columnHandler = CustomApplicationUtil.getBean(AutoFillColumnHandler.class);
        if(ObjectUtils.isEmpty(columnHandler)) {
            return null;
        }
        List<TableFillObject> tableFillObjects = columnHandler.fillStrategy();
        if(ObjectUtils.isEmpty(tableFillObjects)) {
            return null;
        }
        TableFillObject fillObject = tableFillObjects.stream().filter(x -> x.getEntityClass().equals(t)).findFirst().orElse(null);
        if (Objects.isNull(fillObject)) {
            return null;
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
        AutoFillColumnHandler columnHandler = CustomApplicationUtil.getBean(AutoFillColumnHandler.class);
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
        AutoFillColumnHandler columnHandler = CustomApplicationUtil.getBean(AutoFillColumnHandler.class);
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
