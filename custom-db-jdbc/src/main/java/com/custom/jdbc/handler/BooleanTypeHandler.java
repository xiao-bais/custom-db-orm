package com.custom.jdbc.handler;

import com.custom.comm.utils.Constants;
import com.custom.comm.utils.ConvertUtil;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/11/13 1:00
 * @desc
 */
public class BooleanTypeHandler extends AbstractTypeHandler<Boolean> {
    @Override
    public Boolean getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Boolean) {
            return (Boolean) val;
        }
        if (val instanceof Integer) {
            return ((Integer) val) > 0;
        }
        if (val instanceof Long) {
            return ((Long) val) > 0L;
        }
        if (val instanceof Double) {
            return ((Double) val) > 0.0D;
        }
        if (val instanceof BigDecimal) {
            return ((BigDecimal) val).doubleValue() > 0.0D;
        }
        return val instanceof CharSequence
                && (String.valueOf(val).equalsIgnoreCase(Constants.CONST_TRUE) || "1".equals(val));

    }

    @Override
    public Boolean getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getBoolean(index);
    }

}
