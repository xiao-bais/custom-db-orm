package com.custom.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author  Xiao-Bai
 * @since  2022/11/13 1:59
 * 
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

    @Override
    public ShortTypeHandler clone() {
        ShortTypeHandler builder = null;
        try {
            builder = (ShortTypeHandler) super.clone();
        } catch (CloneNotSupportedException e) {
            log().error(e.toString(), e);
        }
        return builder;
    }

    @Override
    public AbstractTypeHandler<Short> getClone() {
        return clone();
    }
}
