package com.custom.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/11/12 0012 15:34
 */
public class DoubleTypeHandler extends AbstractTypeHandler<Double> {
    @Override
    public Double getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        else if (val instanceof Boolean) {
            return (boolean) val ? 1.0D : 0;
        }
        return castNumber(val).doubleValue();
    }

    @Override
    public Double getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getDouble(index);
    }

    @Override
    public DoubleTypeHandler clone() {
        DoubleTypeHandler builder = null;
        try {
            builder = (DoubleTypeHandler) super.clone();
        } catch (CloneNotSupportedException e) {
            log().error(e.toString(), e);
        }
        return builder;
    }

}
