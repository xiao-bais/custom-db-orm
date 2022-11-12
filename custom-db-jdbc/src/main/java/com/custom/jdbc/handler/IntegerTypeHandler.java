package com.custom.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import static sun.security.krb5.Confounder.intValue;

/**
 * @author Xiao-Bai
 * @date 2022/11/12 0012 14:33
 */
public class IntegerTypeHandler extends AbstractTypeHandler<Integer> {


    @Override
    public Integer getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        return castNumber(val).intValue();
    }

    @Override
    public Integer getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getInt(index);
    }
}
