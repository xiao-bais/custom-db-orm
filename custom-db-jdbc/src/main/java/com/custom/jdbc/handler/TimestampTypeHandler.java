package com.custom.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Xiao-Bai
 * @since 2023/2/25 22:47
 */
public class TimestampTypeHandler extends AbstractTypeHandler<Timestamp> {


    @Override
    public Timestamp getTypeValue(Object val) {
        if (val == null) {
            return null;
        } else if (val instanceof Timestamp) {
            return (Timestamp) val;
        } else if (val instanceof Date) {
            Date date = (Date) val;
            return new Timestamp(date.getTime());
        } else if (val instanceof Integer) {
            int intVal = (Integer) val;
            if (intVal > 0 && String.valueOf(intVal).length() != 10) {
                throw new ClassCastException(Integer.class + " cannot be case to " + Timestamp.class);
            }
            return new Timestamp(intVal * 1000L);
        } else if (val instanceof Long) {
            long longVal = (Long) val;
            if (longVal > 0 && String.valueOf(longVal).length() != 13) {
                throw new ClassCastException(Long.class + " cannot be case to " + Timestamp.class);
            }
            return new Timestamp(longVal);
        }
        throw new ClassCastException(val.getClass() + " cannot be case to " + Timestamp.class);
    }

    @Override
    public Timestamp getTypeValue(ResultSet rs, int index) throws SQLException {
        return rs.getTimestamp(index);
    }

    @Override
    public TypeHandler<Timestamp> getClone() {
        return clone();
    }

    @Override
    public TimestampTypeHandler clone() {
        TimestampTypeHandler builder = null;
        try {
            builder = (TimestampTypeHandler) super.clone();
        } catch (CloneNotSupportedException e) {
            log().error(e.toString(), e);
        }
        return builder;
    }
}
