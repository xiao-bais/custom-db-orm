package com.custom.dbconfig;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/10
 * @Description
 */
public class DbFieldsConst {

    //数据库
    public static final String DATA_BASE = "database";
    //连接
    public static final String CONN = "conn";
    //自定义策略
    public static final String CUSTOM_STRATEGY = "customStrategy";

    //表名称
    public static final  String TABLE_NAME = "tableName";
    //表对应实体类名称
    public static final  String CLASS_NAME = "className";
    //表别名
    public static final  String TABLE_ALIAS = "alias";

    //主键sql字段
    public static final  String DB_KEY = "dbKey";
    //主键长度
    public static final  String KEY_LENGTH = "length";
    //主键说明
    public static final  String KEY_DESC = "desc";
    //主键策略
    public static final  String KEY_STRATEGY = "strategy";
    //主键数据类型
    public static final  String KEY_TYPE = "dbType";
    //主键java属性字段
    public static final  String KEY_FIELD = "fieldKey";

    //sql字段类型
    public static final  String DB_FIELD_TYPE = "fieldType";
    //sql对应java属性字段
    public static final  String DB_CLASS_FIELD = "fieldName";
    //sql字段名称
    public static final  String DB_FIELD_NAME = "dbFieldName";
    //sql字段长度
    public static final  String DB_FIELD_LENGTH = "length";
    //sql字段说明
    public static final  String DB_FIELD_DESC = "desc";
    //sql字段是否为空
    public static final  String DB_IS_NULL = "isNull";


    //关联属性（条件）
    public static final  String DB_JOIN_TABLE = "joinTable";
    //关联表别名
    public static final  String DB_JOIN_ALIAS = "joinAlias";
    //关联条件
    public static final  String DB_JOIN_CONDITION = "condition";
    //关联方式（‘left join’ or ‘right join’ or other）
    public static final  String DB_JOIN_STYLE = "joinStyle";
    //关联后查询的字段
    public static final  String DB_JOIN_MAP_FIELD = "dbField";
    //关联查询后映射对应java字段
    public static final  String DB_JOIN_MAP_CLASS_FIELD = "fieldName";


}
