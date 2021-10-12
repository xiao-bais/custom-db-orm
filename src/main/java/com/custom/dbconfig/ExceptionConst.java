package com.custom.dbconfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/12 0012 21:22
 * @Version 1.0
 * @Description GlobalVarManager
 */
public class ExceptionConst {

    /**
     * 全局变量
     */
    @SuppressWarnings(value = "Unckecked")
    public static Map<String, Object> currMap  = new ConcurrentHashMap<>();

    /**
     * 异常--该类上找不到DbTable注解
     */
    public final static String EX_DBTABLE__NOTFOUND = "@DbTable not found in class ";

    /**
     * 异常--该类上找不到DbField注解
     */
    public final static String EX_DBFIELD__NOTFOUND = "@DbField not found in class ";

    /**
     * 异常--该类上找不到DbKey注解
     */
    public final static String EX_DBKEY_NOTFOUND = " @DbKey was not found in class ";

    /**
     * 参数为空异常
     */
    public final static String EX_PARAM_EMPTY = "Parameter cannot be empty";

    /**
     * 主键值为空
     */
    public final static String EX_PRIMARY_KEY_VALUE_NOT_EMPTY = "Primary key cannot be empty in class ";

    /**
     * 主键重复
     */
    public final static String EX_PRIMARY_REPEAT = "Multiple @DbKey annotations are not allowed in the same class in class ";

    /**
     * 主键的Java类型与xxx类中指定的表数据类型不匹配
     */
    public final static String EX_PRIMARY_CANNOT_MATCH = "The Java type of the primary key does not match the table data type specified in the class ";

    /**
     * 不允许的主键类型
     */
    public final static String EX_PRIMARY_NOT_ALLOW = " Primary key type not allowed ";

    /**
     * 找不到要删除的表名称
     */
    public final static String EX_DROP_TABLE_NOT_FOUND = "The table name to delete cannot be found or does not exist in the database ";

    /**
     * 只查询一个，但找到2个结果
     */
    public final static String EX_QUERY_MORE_RESULT = "One was queried, but more were found:(%s) ";

    /**
     * 删除条件不能为空
     */
    public final static String EX_DEL_CONDITION_NOT_EMPTY = "Delete condition cannot be empty";

    /**
     * sql类型转java类型时,类型转换异常或无法匹配对应类型
     */
    public final static String EX_SQL_TYPE_UNABLE_CAST = "When SQL type is converted to Java type, type conversion exception or type mismatch exception occurs when mapping from SQL field '%s' to Java property";

    /**
     * 文件路径不能为空
     */
    public final static String EX_FILE_NOT_EMPTY = "The filePath to be Not Empty";

    /**
     * SQL不能为空
     */
    public final static String EX_SQL_NOT_EMPTY = "The Sql to be Not Empty";

    /**
     * 该字段找不到
     */
    public final static String EX_NO_SUCH_FIELD = "The unknown field by: ";

    public final static String EX_NOT_SUPPORT_INSERT_TYPE = "Batch data insertion of self increasing primary key type is not supported at present in class ";




}


