package com.custom.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/11/13 0:50
 * @desc
 */
public class FloatTypeHandler extends AbstractTypeHandler<Float> {
    @Override
    public Float getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        return castNumber(val).floatValue();
    }

    @Override
    public Float getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getFloat(index);
    }
}
