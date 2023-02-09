package com.custom.action.util;

import com.custom.action.core.DbKeyParserModel;
import com.custom.action.core.TableInfoCache;
import com.custom.comm.utils.RexUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.StrUtils;

/**
 * sql字段工具类
 * @author   Xiao-Bai
 * @since  2022/4/18 21:48
 */
public class DbUtil {


    /**
     * 该类是否存在主键
     */
    public static <T> boolean hasPriKey(Class<T> clazz) {
        DbKeyParserModel<T> keyParserModel = TableInfoCache.getTableModel(clazz).getKeyParserModel();
        return keyParserModel != null;
    }

    /**
     * 返回自定义包装的查询字段
     */
    public static String wrapperSqlColumn(String wrapperColumn, String fieldName, boolean isNullToEmpty) {
        boolean hasIfNull = RexUtil.hasRegex(wrapperColumn, RexUtil.sql_if_null);
        if (isNullToEmpty && !hasIfNull) {
            return DbUtil.ifNull(wrapperColumn, fieldName);
        }
        return sqlSelectWrapper(wrapperColumn, fieldName);
    }


    public static String ifNull(String column) {
        return String.format("IFNULL(%s, '')", column);
    }

    public static String ifNull(String column, String fieldName) {
        return String.format("IFNULL(%s, '') %s", column, fieldName);
    }


    /**
     * sql的查询包装
     * @param val1 a.name-表字段
     * @param val2 username-映射的java实体属性
     * @return a.name username
     */
    public static String sqlSelectWrapper(String val1, String val2) {
        return String.format("%s %s", val1, val2);
    }

    /**
     * 完整sql字段，例如：a.name
     * a 为表的别名
     * name 为表字段
     */
    public static String fullSqlColumn(String val1, String val2) {
        return String.format("%s.%s", val1, val2);
    }

    /**
     * 逻辑删除字段组装
     */
    public static String formatLogicSql(String alias, String logicColumn, Object value) {
        return String.format("%s.%s = %s ", alias, logicColumn, value);
    }

    /**
     * condition sql格式化
     */
    public static String formatSqlCondition(String column) {
        return String.format("%s = ?", column);
    }

    /**
     * 格式化sql条件，直接映射(a = b)
     */
    public static String formatMapperSqlCondition(String column, Object val) {
        return String.format("%s = %s", column, val);
    }


    public static String formatSqlAndCondition(String column) {
        return String.format("AND %s = ?", column);
    }
    public static String formatSqlAndCondition(String alias, String column) {
        return String.format("AND %s.%s = ?", alias, column);
    }
    /**
     * 组装sql条件
     */
    public static String applyCondition(String v1, String v2, String v3) {
        return String.format(" %s %s %s ?", v1, v2, v3);
    }

    public static String applyCondition(String v1, String v2, String v3, String v4) {
        return String.format(" %s %s %s %s", v1, v2, v3, v4);
    }

    public static String applyInCondition(String v1, String v2, String v3, String v4) {
        return String.format(" %s %s %s (%s)", v1, v2, v3, v4);
    }

    public static String applyExistsCondition(String v1, String v2, String v3) {
        return String.format(" %s %s (%s)", v1, v2, v3);
    }

    public static String applyIsNullCondition(String v1, String v2, String v3) {
        return String.format(" %s %s %s", v1, v2, v3);
    }

    /**
     * 消除sql条件中的第一个and/or
     */
    public static String trimSqlCondition(String condition) {
        String finalCondition = condition.trim();
        if(StrUtils.startWithIgnoreCase(finalCondition, Constants.AND)) {
            finalCondition = finalCondition.substring(3);
        }else if(StrUtils.startWithIgnoreCase(finalCondition, Constants.OR)) {
            finalCondition = finalCondition.substring(2);
        }
        return finalCondition.trim();
    }

    /**
     * 消除sql条件中的第一个and
     */
    public static String trimFirstAndBySqlCondition(String condition) {
        String finalCondition = condition;
        if(condition.trim().startsWith(Constants.AND)) {
            finalCondition = condition.replaceFirst(Constants.AND, Constants.EMPTY);
        }
        return finalCondition.trim();
    }

    /**
     * sql中若以OR开头，则替换成AND
     */
    public static String replaceOrWithAndOnSqlCondition(String condition) {
        String finalCondition = condition;
        if(condition.trim().startsWith(Constants.OR)) {
            finalCondition = condition.replaceFirst(Constants.OR, Constants.AND);
        }
        return finalCondition.trim();
    }

}
