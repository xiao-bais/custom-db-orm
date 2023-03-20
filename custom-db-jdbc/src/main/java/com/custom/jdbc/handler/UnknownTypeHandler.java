package com.custom.jdbc.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author  Xiao-Bai
 * @since  2022/11/16 0:24
 * 
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

    @Override
    public UnknownTypeHandler clone() {
        UnknownTypeHandler builder = null;
        try {
            builder = (UnknownTypeHandler) super.clone();
        } catch (CloneNotSupportedException e) {
            log().error(e.toString(), e);
        }
        return builder;
    }

    @Override
    public AbstractTypeHandler<Object> getClone() {
        return clone();
    }



}
