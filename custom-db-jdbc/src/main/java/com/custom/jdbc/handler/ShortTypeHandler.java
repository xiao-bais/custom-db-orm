package com.custom.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/11/13 1:59
 * @desc
 */
public class ShortTypeHandler extends AbstractTypeHandler<Short>{
    @Override
    public Short getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        return castNumber(val).shortValue();
    }

    @Override
    public Short getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getShort(index);
    }
}
