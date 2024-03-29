package com.custom.jdbc.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author  Xiao-Bai
 * @since  2022/11/12 0012 15:32
 */
public class LongTypeHandler extends AbstractTypeHandler<Long> {
    @Override
    public Long getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        else if (val instanceof Boolean) {
            return (boolean) val ? 1L : 0;
        }else if (val instanceof CharSequence) {
            return Long.parseLong(String.valueOf(val));
        }else if (val instanceof Date) {
            return ((Date) val).getTime();
        }
        return castNumber(val).longValue();
    }

    @Override
    public Long getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getLong(index);
    }

    @Override
    public LongTypeHandler clone() {
        LongTypeHandler builder = null;
        try {
            builder = (LongTypeHandler) super.clone();
        } catch (CloneNotSupportedException e) {
            log().error(e.toString(), e);
        }
        return builder;
    }

    @Override
    public AbstractTypeHandler<Long> getClone() {
        return clone();
    }
}
