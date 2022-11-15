package com.custom.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/11/16 0:24
 * @desc
 */
public class UnknownTypeHandler extends AbstractTypeHandler<Object> {
    @Override
    public Object getTypeValue(Object val) {
        return val;
    }

    @Override
    public Object getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getObject(index);
    }



}
