package com.custom.jdbc.handler;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author  Xiao-Bai
 * @since  2022/11/13 0:50
 * 
 */
public class FloatTypeHandler extends AbstractTypeHandler<Float> {
    @Override
    public Float getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        else if (val instanceof Boolean) {
            return (boolean) val ? 1.0F : 0;
        }
        return castNumber(val).floatValue();
    }

    @Override
    public Float getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getFloat(index);
    }

    @Override
    public FloatTypeHandler clone() {
        FloatTypeHandler builder = null;
        try {
            builder = (FloatTypeHandler) super.clone();
        } catch (CloneNotSupportedException e) {
            log().error(e.toString(), e);
        }
        return builder;
    }

    @Override
    public AbstractTypeHandler<Float> getClone() {
        return clone();
    }
}
