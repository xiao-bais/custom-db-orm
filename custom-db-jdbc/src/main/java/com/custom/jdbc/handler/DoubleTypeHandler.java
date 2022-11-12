package com.custom.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/11/12 0012 15:34
 */
public class DoubleTypeHandler extends NumberTypeHandler<Double> {
    @Override
    public Double getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        return castNumber(val).doubleValue();
    }

    @Override
    public Double getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getDouble(index);
    }

    @Override
    public Double getTypeNoNullValue(Object val) {
        if (val == null) {
            return getNumberDefaultVal().doubleValue();
        }
        return castNumber(val).doubleValue();
    }


    public static void main(String[] args) {
        DoubleTypeHandler typeHandler = new DoubleTypeHandler();
        Number numberDefaultVal = typeHandler.getTypeNoNullValue(null);
        System.out.println("numberDefaultVal = " + numberDefaultVal);
    }

}
