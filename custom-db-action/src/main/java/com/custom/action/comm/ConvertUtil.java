package com.custom.action.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/29 19:02
 * @Desc：类型转换工具
 **/
@SuppressWarnings("unchecked")
public class ConvertUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConvertUtil.class);


    /**
     * 类型转换
     */
    public static <T> T transToObject(Class<T> transType, Object value) {
        if (Objects.nonNull(value) && JudgeUtilsAx.isNotEmpty(value)) {
            if (transType.equals(Integer.class) || transType.equals(Integer.TYPE)) {
                return (T) Integer.valueOf(value.toString());
            }
            if (transType.equals(Long.class) || transType.equals(Long.TYPE)) {
                return (T) Long.valueOf(value.toString());
            }
            if (transType.equals(Float.class) || transType.equals(Float.TYPE)) {
                return (T) Float.valueOf(value.toString());
            }
            if (transType.equals(Short.class) || transType.equals(Short.TYPE)) {
                return (T) Short.valueOf(value.toString());
            }
            if (transType.equals(Boolean.class) || transType.equals(Boolean.TYPE)) {
                return (T) Boolean.valueOf(value.toString());
            }
            if (transType.equals(Byte.class) || transType.equals(Byte.TYPE)) {
                return (T) Byte.valueOf(value.toString());
            }
            if (transType.equals(Character.class) || transType.equals(Character.TYPE)) {
                return (T) Character.valueOf(value.toString().charAt(0));
            }
            if (transType.equals(Double.class) || transType.equals(Double.TYPE)) {
                return (T) Double.valueOf(value.toString());
            }
            if (transType.equals(String.class)) {
                return (T) value;
            }
            if (transType.equals(BigDecimal.class)) {
                return (T) new BigDecimal(value.toString());
            }
            if (transType.equals(LocalDateTime.class)) {
                return (T) LocalDateTime.parse(value.toString());
            }
            if (transType.equals(Date.class)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    return (T) sdf.parse(value.toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            return null;
        }
        return getDefaultVal(transType);
    }


    /**
     * 获取默认值
     */
    public static <T> T getDefaultVal(Class<T> type) {
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) return (T) new Integer(0);
        if (type.equals(Long.class) || type.equals(Long.TYPE)) return (T) new Long(0L);
        if (type.equals(Short.class) || type.equals(Short.TYPE)) return (T) new Short("0");
        if (type.equals(Float.class) || type.equals(Float.TYPE)) return (T) new Float(0.0f);
        if (type.equals(Double.class) || type.equals(Double.TYPE)) return (T) new Double(0.0d);
        if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) return (T) Boolean.valueOf(false);
        if (type.equals(Byte.class) || type.equals(Byte.TYPE)) return (T) new Byte("0");
        if (type.equals(Character.class) || type.equals(Character.TYPE)) return (T) new Character('\u0000');
        return null;
    }

    public static void main(String[] args) {
        System.out.println("Integer.TYPE = " + Integer.TYPE);
    }
}
