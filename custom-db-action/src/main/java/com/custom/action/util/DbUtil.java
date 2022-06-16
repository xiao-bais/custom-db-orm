package com.custom.action.util;

import com.custom.action.sqlparser.TableInfoCache;
import com.custom.comm.CustomUtil;
import com.custom.comm.RexUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbRelated;
import com.custom.comm.enums.DbType;
import com.custom.comm.enums.SqlLike;
import com.custom.jdbc.CustomJdbcExecutor;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author Xiao-Bai
 * @date 2022/4/18 21:48
 * @desc:
 */
public class DbUtil {

    /**
     * 该类是否存在主键
     */
    public static <T> boolean isKeyTag(Class<T> clazz){
        Field[] fields = TableInfoCache.getTableModel(clazz).getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class)) return true;
        }
        return false;
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
        return String.format("ifnull(%s, '')", column);
    }

    public static String ifNull(String column, String fieldName) {
        return String.format("ifnull(%s, '') %s", column, fieldName);
    }

    /**
     * 由于部分表可能没有逻辑删除字段，所以在每一次执行时，都需检查该表有没有逻辑删除的字段，以保证sql正常执行
     */
    public static boolean checkLogicFieldIsExist(String table, String logicField, CustomJdbcExecutor jdbcExecutor) throws Exception {
        Boolean existsLogic = TableInfoCache.isExistsLogic(table);
        if (existsLogic != null) {
            return existsLogic;
        }
        String existSql = String.format("select count(*) count from information_schema.columns where table_name = '%s' and column_name = '%s'", table, logicField);
        long count = jdbcExecutor.executeExist(existSql);
        TableInfoCache.setTableLogic(table, count > 0);
        return count > 0;
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

    public static String sqlConcat(SqlLike sqlLike) {
        String sql = SymbolConstant.EMPTY;
        switch (sqlLike) {
            case LEFT:
                sql = "concat('%', ?)";
                break;
            case RIGHT:
                sql = "concat(?, '%')";
                break;
            case LIKE:
                sql = "concat('%', ?, '%')";
                break;
        }
        return sql;
    }

    /**
     * 消除sql条件中的第一个and/or
     */
    public static String trimSqlCondition(String condition) {
        String finalCondition = condition;
        if(condition.trim().startsWith(SymbolConstant.AND)) {
            finalCondition = condition.replaceFirst(SymbolConstant.AND, SymbolConstant.EMPTY);
        }else if(condition.trim().startsWith(SymbolConstant.OR)) {
            finalCondition = condition.replaceFirst(SymbolConstant.OR, SymbolConstant.EMPTY);
        }
        return finalCondition.trim();
    }

    /**
     * 消除sql条件中的第一个and
     */
    public static String trimAppendSqlCondition(String condition) {
        String finalCondition = condition;
        if(condition.trim().startsWith(SymbolConstant.AND)) {
            finalCondition = condition.replaceFirst(SymbolConstant.AND, SymbolConstant.EMPTY);
        }
        return finalCondition.trim();
    }

    /**
     * sql中若以OR开头，则替换成AND
     */
    public static String replaceOrWithAndOnSqlCondition(String condition) {
        String finalCondition = condition;
        if(condition.trim().startsWith(SymbolConstant.OR)) {
            finalCondition = condition.replaceFirst(SymbolConstant.OR, SymbolConstant.AND);
        }
        return finalCondition.trim();
    }

}
