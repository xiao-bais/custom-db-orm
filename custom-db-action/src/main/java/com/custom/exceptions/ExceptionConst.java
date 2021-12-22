package com.custom.exceptions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/12 0012 21:22
 * @Version 1.0
 * @Description ExceptionConst
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
     * 只查询一个，但找到多个结果
     */
    public final static String EX_QUERY_MORE_RESULT = "One was queried, but more were found:(%s) ";

    /**
    * 'Set'不支持返回多列结果
    */
    public final static String EX_QUERY_SET_RESULT = "The 'Set<%s>' does not support returning multiple column results";

    /**
     * 'Array'不支持返回多列结果
     */
    public final static String EX_QUERY_ARRAY_RESULT = "The 'Arrays' does not support returning multiple column results";

    /**
     * 删除条件不能为空
     */
    public final static String EX_DEL_CONDITION_NOT_EMPTY = "Delete condition cannot be empty";

    /**
    * 删除的主键不能为空
    */
    public final static String EX_DEL_PRIMARY_KEY_NOT_EMPTY = "Delete primary key cannot be empty";

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

    /**
     * 同一个对象中不支持DBRelated与DbJoinTables同时存在
     */
    public final static String EX_DBRELATED_DBJOINTABLES_NOT_SUPPORT = "@DbRelated and @DbJoinTables cannot exist in a class at the same time";

    /**
     * 找不到注解DbJoinTables
     */
    public final static String EX_DB_JOIN_TABLES_NOTFOUND = " @DbJoinTables was not found in class ";

    /**
    * 未配置逻辑删除字段的对应值
    */
    public final static String EX_LOGIC_EMPTY_VALUE = " The corresponding value of the logical deletion field is not configured ";

    /**
    * 基于主键查询时，未指定主键的值
    */
    public final static String EX_PRIMARY_KEY_NOT_SPECIFIED = "The value of the primary key is not specified when querying based on the primary key";

    /**
    * 建议使用包装类型来接收返回值
    */
    public final static String EX_NOT_SUPPORT_USE_BASIC_TYPE = "It is recommended to use the wrapper type to receive the return value of the method ： %s.%s()";

    /**
    * 该方法上未找到@Query 或 @Update 或 @SqlPath注解
    */
    public final static String EX_NOT_FOUND_ANNO__SQL_READ = "The '@Update' or '@Query' annotation was not found on the method : %s.%s()";

    /**
    * 未继承BasicDao
    */
    public final static String EX_NOT_INHERITED_BASIC_DAO = "Execution error, possibly because '%s' does not inherit com.custom.comm.BasicDao or this interface is not annotated with @SqlMapper";


    /**
    * 找不到sql中的参数对应的名称
    */
    public final static String EX_NOT_FOUND_PARAMS_VALUE = "unknown column, The value corresponding to parameter '%s' in SQL cannot be found \n Sql Error =====> \n%s \n==========\n";

    /**
    * 找不到sql中的参数
    */
    public final static String EX_NOT_FOUND_PARAMS_NAME = "empty column, The value corresponding to parameter '%s' in SQL cannot be found \n Sql Error =====> \n%s \n==========\n";

    /**
    * 找不到该路径
    */
    public final static String EX_NOT_FOUND_URL = "The package url cannot be found：'%s'";

    /**
    * 无法解析：#{}与? 不能同时出现在同一条sql上
    */
    public final static String EX_UNABLE_TO_RESOLVE_SQL = " The SQL cannot be resolved '%s'";

    public final static String EX_USE_ORDER_TRUE = " 方法注解上建议使用 isOrder = true";
    public final static String EX_USE_ORDER_FALSE = " 方法注解上建议使用 isOrder = false";




    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
