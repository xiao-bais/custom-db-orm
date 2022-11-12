package com.custom.jdbc.handler;

import com.custom.comm.utils.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/11/12 1:27
 * @desc
 */
public class StringTypeHandler extends AbstractTypeHandler<String> {


    @Override
    public String getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        return String.valueOf(val);
    }

    @Override
    public String getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }

    @Override
    public String getTypeNoNullValue(Object val) {
        return thisValIsEmpty(val) ? Constants.EMPTY : String.valueOf(val);
    }



}
