package com.custom.jdbc.handler;

import com.custom.comm.utils.Asserts;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/11/12 0012 15:40
 */
public abstract class NumberTypeHandler<T> extends AbstractTypeHandler<Number> {

    public Number getNumberDefaultVal() {

        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];

        if (!Number.class.isAssignableFrom((Class<?>) typeArgument)) {
            throw new IllegalArgumentException("Cannot convert to numeric type");
        }

        if (typeArgument.equals(Integer.class)) {
            return 0;
        } else if (typeArgument.equals(Long.class)) {
            return 0L;
        } else if (typeArgument.equals(Double.class)) {
            return 0.0d;
        } else if (typeArgument.equals(Float.class)) {
            return 0.0f;
        } else if (typeArgument.equals(Short.class)) {
            return (short) 0;
        }else if (typeArgument.equals(Byte.class)) {
            return (byte) 0;
        }else if (typeArgument.equals(BigDecimal.class)) {
            return BigDecimal.ZERO;
        }else if (typeArgument.equals(BigInteger.class)) {
            return BigInteger.ZERO;
        }
        return 0;
    }

    @Override
    public Number getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        return castNumber(val);
    }

    @Override
    public Number getTypeValue(ResultSet rs, int index) throws SQLException {
        Object obj = rs.getObject(index);
        return castNumber(obj);
    }

    @Override
    public Number getTypeNoNullValue(Object val) {
        if (val == null) {
            return getNumberDefaultVal();
        }
        try {
            return castNumber(val);
        } catch (Exception e) {
            return getNumberDefaultVal();
        }
    }


}
