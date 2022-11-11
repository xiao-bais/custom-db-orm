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
    public String getTypeValue() {
        return String.valueOf(thisVal());
    }

    @Override
    public String getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }

    @Override
    public String getTypeNoNullValue() {
        return thisValIsNull() ? Constants.EMPTY : String.valueOf(thisVal());
    }

    @Override
    public String castTypeValue(Object obj) {
        if (obj == null) {
            return Constants.EMPTY;
        }
        if (obj instanceof CharSequence) {
            return String.valueOf(obj);
        }
        return null;
    }


    public StringTypeHandler() {
        super();
    }

    public StringTypeHandler(Object val) {
        super(val);
    }



}
