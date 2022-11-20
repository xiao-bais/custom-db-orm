package com.custom.jdbc.handler;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Xiao-Bai
 * @date 2022/11/13 1:14
 * @desc
 */
public class CharacterTypeHandler extends AbstractTypeHandler<Character> {
    @Override
    public Character getTypeValue(Object val) {
        if (val == null) {
            return null;
        }
        else if (val instanceof Character) {
            return (Character) val;
        }
        else if (val instanceof Boolean) {
            return (char) ((boolean) val ? 1 : 0);
        }
        return null;
    }

    @Override
    public Character getTypeValue(ResultSet rs, int index) throws SQLException {
        String res = rs.getString(index);
        if (res != null && res.length() > 0) {
            return res.charAt(0);
        }
        return null;
    }

    @Override
    public CharacterTypeHandler clone() {
        CharacterTypeHandler builder = null;
        try {
            builder = (CharacterTypeHandler) super.clone();
        } catch (CloneNotSupportedException e) {
            log().error(e.toString(), e);
        }
        return builder;
    }

    @Override
    public AbstractTypeHandler<Character> getClone() {
        return clone();
    }

}
