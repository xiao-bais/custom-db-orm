package com.custom.jdbc.handler;

import com.custom.comm.exceptions.CustomCheckException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Xiao-Bai
 * @date 2022/11/13 1:24
 * @desc
 */
public class DateTypeHandler extends AbstractTypeHandler<Date>{

    private final static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    public Date getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        else if (val instanceof Date) {
            return (Date) val;
        }
        else if (val instanceof Integer) {
            int len = String.valueOf(val).length();
            if (len == 10) {
                return new Date(((int) val) * 1000L);
            }
        }else if (val instanceof Long) {
            int len = String.valueOf(val).length();
            if (len == 13) {
                return new Date((long) val);
            }
        }else if (val instanceof CharSequence) {
            String value = String.valueOf(val);
            if (value.contains("-")) {
                try {
                    return sdf1.parse(value);
                } catch (ParseException e1) {
                    try {
                        return sdf2.parse(value);
                    } catch (ParseException e2) {
                        throw new CustomCheckException("Cannot convert to date format");
                    }
                }
            }
            else if (value.contains("/")) {
                try {
                    return sdf3.parse(value);
                } catch (ParseException e1) {
                    try {
                        return sdf4.parse(value);
                    } catch (ParseException e2) {
                        throw new CustomCheckException("Cannot convert to date format");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Date getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getDate(index);
    }
}
