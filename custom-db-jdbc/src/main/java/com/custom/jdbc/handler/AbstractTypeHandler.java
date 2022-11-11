package com.custom.jdbc.handler;

import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.StrUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Xiao-Bai
 * @date 2022/11/12 0:11
 * @desc
 */
@SuppressWarnings("unchecked")
public abstract class AbstractTypeHandler<T> implements TypeHandler<T>  {

    /**
     * 是否下划线转驼峰
     */
    private boolean isUnderlineToCamel = false;
    private Object val;

    public void setUnderlineToCamel(boolean underlineToCamel) {
        isUnderlineToCamel = underlineToCamel;
    }

    public AbstractTypeHandler(Object val) {
        this.val = val;
    }

    public boolean thisValIsNull() {
        if (val == null) {
            return true;
        }
        if (val instanceof CharSequence) {
            return StrUtils.isBlank(String.valueOf(val));
        }
        return JudgeUtil.isEmpty(val);
    }

    public Object thisVal() {
        return val;
    }

    public AbstractTypeHandler(){}

    public T getTypeValue(ResultSet rs, String column) throws SQLException {
        if (isUnderlineToCamel) {
            column = CustomUtil.underlineToCamel(column);
        }
        return (T) rs.getObject(column);
    }


}
