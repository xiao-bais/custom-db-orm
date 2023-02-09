package com.custom.jdbc.handler;

import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 类型处理
 * @author  Xiao-Bai
 * @since  2022/11/12 0:11
 */
@SuppressWarnings("unchecked")
public abstract class AbstractTypeHandler<T> implements TypeHandler<T>, NonNullableTypeHandler<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Logger log() {
        return logger;
    }

    /**
     * 是否下划线转驼峰
     */
    private boolean isUnderlineToCamel = false;

    public void setUnderlineToCamel(boolean underlineToCamel) {
        isUnderlineToCamel = underlineToCamel;
    }

    /**
     * 转换成数字类型
     */
    protected Number castNumber(Object val) {
        if (val instanceof CharSequence) {
            String s = String.valueOf(val);
            s = s.trim();
            if (StrUtils.isBlank(s)) {
               return new BigDecimal(0);

            }else throw new NumberFormatException("Cannot convert to numeric type");
        }
        Asserts.allowed(val instanceof Number, "Cannot convert to numeric type");
        return (Number) val;
    }

    /**
     * 获取不为空的默认值
     */
    protected T getTargetInstance() {
        Class<T> targetClass = thisClass();
        Object val = null;

        if (Boolean.class.equals(targetClass)) {
            val = false;
        }
        else if (Character.class.equals(targetClass)) {
            val =  '\u0000';
        }

        // 数字类型
        else if (Number.class.isAssignableFrom(targetClass)) {
            if (targetClass.equals(Integer.class)) {
                val = 0;
            } else if (targetClass.equals(Long.class)) {
                val = 0L;
            } else if (targetClass.equals(Double.class)) {
                val = 0.0d;
            } else if (targetClass.equals(Float.class)) {
                val = 0.0f;
            } else if (targetClass.equals(Short.class)) {
                val = (short) 0;
            }else if (targetClass.equals(Byte.class)) {
                val = (byte) 0;
            }else if (targetClass.equals(BigDecimal.class)) {
                val = BigDecimal.ZERO;
            }else if (targetClass.equals(BigInteger.class)) {
                val = BigInteger.ZERO;
            }
        }

        // 字符串
        else if (CharSequence.class.isAssignableFrom(targetClass)) {
            val = Constants.EMPTY;
        }
        // 数组，不支持
        else if (targetClass.isArray()) {
            throw new UnsupportedOperationException();
        }
        // map 映射结构
        else if (Map.class.isAssignableFrom(targetClass)) {
            val = new HashMap<>();
        }
        // list 数组结构
        else if (List.class.isAssignableFrom(targetClass)) {
            val = new ArrayList<>();
        }
        // hash set
        else if (Set.class.isAssignableFrom(targetClass)) {
            val = new HashSet<>();
        }
        // 自定义类型
        else {

            // 是否是接口
            if (Modifier.isInterface(targetClass.getModifiers())) {
                throw new UnsupportedOperationException("interface cannot support instantiation ");
            }
            // 是否是抽象类
            else if (Modifier.isAbstract(targetClass.getModifiers())) {
                throw new UnsupportedOperationException("abstract class cannot support instantiation ");
            }
            // 枚举类
            else if (targetClass.isEnum()) {
                throw new UnsupportedOperationException("enum class cannot support instantiation ");
            }
            else {
                try {

                    // 自定义类型的实例化
                    val = ReflectUtil.getInstance(targetClass);

                } catch (NoSuchMethodException | InvocationTargetException
                        | InstantiationException | IllegalAccessException e) {

                    throw new CustomCheckException(e.getMessage(), e);
                }
            }
        }
        return (T) val;
    }

    /**
     * 是否为null
     */
    public boolean thisValIsEmpty(Object val) {
        if (val == null) {
            val = true;
        }
        if (val instanceof CharSequence) {
            val = StrUtils.isBlank(String.valueOf(val));
        }
        return JudgeUtil.isEmpty(val);
    }

    /**
     * 解析泛型类
     */
    private Class<T> thisClass() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];
        return (Class<T>) typeArgument;
    }

    public T getTypeValue(ResultSet rs, String column) throws SQLException {
        if (isUnderlineToCamel) {
            column = CustomUtil.underlineToCamel(column);
        }
        return (T) rs.getObject(column);
    }


    @Override
    public T getTypeNoNullValue(Object val) {
        if (val == null) {
            return getTargetInstance();
        }
        return getTypeValue(val);
    }





}
