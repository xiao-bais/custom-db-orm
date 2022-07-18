package com.custom.comm;

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
        if (Objects.nonNull(value) && JudgeUtil.isNotEmpty(value)) {
            if (Integer.class.isAssignableFrom(transType) || transType.equals(Integer.TYPE)) {
                return (T) Integer.valueOf(value.toString());
            }
            if (Long.class.isAssignableFrom(transType) || transType.equals(Long.TYPE)) {
                return (T) Long.valueOf(value.toString());
            }
            if (Float.class.isAssignableFrom(transType) || transType.equals(Float.TYPE)) {
                return (T) Float.valueOf(value.toString());
            }
            if (Short.class.isAssignableFrom(transType) || transType.equals(Short.TYPE)) {
                return (T) Short.valueOf(value.toString());
            }
            if (Boolean.class.isAssignableFrom(transType) || transType.equals(Boolean.TYPE)) {
                return (T) Boolean.valueOf(value.toString());
            }
            if (Byte.class.isAssignableFrom(transType) || transType.equals(Byte.TYPE)) {
                return (T) Byte.valueOf(value.toString());
            }
            if (Character.class.isAssignableFrom(transType) || transType.equals(Character.TYPE)) {
                return (T) Character.valueOf(value.toString().charAt(0));
            }
            if (Double.class.isAssignableFrom(transType) || transType.equals(Double.TYPE)) {
                return (T) Double.valueOf(value.toString());
            }
            if (CharSequence.class.isAssignableFrom(transType)) {
                return (T) String.valueOf(value);
            }
            if (transType.equals(BigDecimal.class)) {
                return (T) new BigDecimal(String.valueOf(value));
            }
            if (transType.equals(LocalDateTime.class)) {
                return (T) LocalDateTime.parse(String.valueOf(value));
            }
            if (transType.equals(Date.class)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    return (T) sdf.parse(String.valueOf(value));
                } catch (ParseException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            return null;
        }
        return allTypeDefaultVal(transType);
    }


    /**
     * 获取默认值
     */
    public static <T> T allTypeDefaultVal(Class<T> type) {
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


    public static boolean conBool(Integer i) {
        return i != null && i > 0;
    }

    public static boolean conBool(BigDecimal i) {
        return i != null && i.doubleValue() > 0;
    }

    public static boolean conBool(Long i) {
        return i != null && i > 0L;
    }

    public static boolean conBool(Double i) {
        return i != null && i > 0D;
    }

    public static boolean conBool(Object i) {
        if (i == null) {
            return false;
        }
        if (i instanceof Boolean) {
            return (Boolean) i;
        }
        if (i instanceof Integer) {
            return conBool((Integer) i);
        }
        if (i instanceof Long) {
            return conBool((Long) i);
        }
        if (i instanceof Double) {
            return conBool((Double) i);
        }
        if (i instanceof BigDecimal) {
            return conBool((BigDecimal) i);
        }
        return i instanceof CharSequence
                && (String.valueOf(i).equalsIgnoreCase(SymbolConstant.CONST_TRUE) || "1".equals(i));
    }


    public static int conInt(Long i) {
        if (i == null) return 0;
        return i.intValue();
    }

    public static int conInt(Double i) {
        if (i == null) return 0;
        return i.intValue();
    }

    public static int conInt(BigDecimal i) {
        if (i == null) return 0;
        return i.intValue();
    }

    public static int conInt(Boolean i) {
        if (i == null) return 0;
        return i ? 1 : 0;
    }

    public static int conInt(String i) {
        if (i == null) return 0;
        return Integer.parseInt(i);
    }

    public static int conInt(Object i) {
        if (i == null) return 0;
        if (i instanceof Integer) {
            return (Integer) i;
        }
        if (i instanceof Boolean) {
            return conInt((Boolean) i);
        }
        if (i instanceof Long) {
            return conInt((Long) i);
        }
        if (i instanceof Double) {
            return conInt((Double) i);
        }
        if (i instanceof BigDecimal) {
            return conInt((BigDecimal) i);
        }
        return i instanceof CharSequence ? conInt(String.valueOf(i)) : 0;
    }



    public static void main(String[] args) {

        boolean conBool = conBool("false");
        System.out.println("conBool = " + conBool);

    }
}
