package com.custom.jdbc.handler;

import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.StrUtils;
import lombok.val;

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

    public void setUnderlineToCamel(boolean underlineToCamel) {
        isUnderlineToCamel = underlineToCamel;
    }

    /**
     * 转换成数字类型
     */
    public Number castNumber(Object val) {
        Asserts.allowed(val instanceof Number, "Cannot convert to numeric type");
        return (Number) val;
    }

    /**
     * 是否为null
     */
    public boolean thisValIsEmpty(Object val) {
        if (val == null) {
            return true;
        }
        if (val instanceof CharSequence) {
            return StrUtils.isBlank(String.valueOf(val));
        }
        return JudgeUtil.isEmpty(val);
    }


    public T getTypeValue(ResultSet rs, String column) throws SQLException {
        if (isUnderlineToCamel) {
            column = CustomUtil.underlineToCamel(column);
        }
        return (T) rs.getObject(column);
    }


}
