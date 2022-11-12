package com.custom.jdbc.handler;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/11/13 0:52
 * @desc
 */
public class BigDecimalTypeHandler extends AbstractTypeHandler<BigDecimal> {


    @Override
    public BigDecimal getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        return (BigDecimal) castNumber(val);
    }

    @Override
    public BigDecimal getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getBigDecimal(index);
    }


}
