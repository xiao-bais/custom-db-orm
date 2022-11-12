package com.custom.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/11/12 0012 15:32
 */
public class LongTypeHandler extends AbstractTypeHandler<Long> {
    @Override
    public Long getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        return castNumber(val).longValue();
    }

    @Override
    public Long getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getLong(index);
    }
}
