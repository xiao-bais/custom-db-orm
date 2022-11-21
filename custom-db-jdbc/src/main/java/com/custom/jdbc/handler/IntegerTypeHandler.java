package com.custom.jdbc.handler;

import java.math.BigDecimal;
import java.sql.Date;
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
        else if (val instanceof Boolean) {
            return (boolean) val ? 1 : 0;
        }else if (val instanceof CharSequence) {
            return Integer.parseInt(String.valueOf(val));
        }else if (val instanceof Date) {
            return (int) (((Date) val).getTime() / 1000);
        }
        return castNumber(val).intValue();
    }

    @Override
    public Integer getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getInt(index);
    }

    @Override
    public IntegerTypeHandler clone() {
        IntegerTypeHandler builder = null;
        try {
            builder = (IntegerTypeHandler) super.clone();
        } catch (CloneNotSupportedException e) {
            log().error(e.toString(), e);
        }
        return builder;
    }

    @Override
    public AbstractTypeHandler<Integer> getClone() {
        return clone();
    }
}
