package com.custom.handler;

import com.custom.annotations.DbField;
import com.custom.annotations.DbJoinTables;
import com.custom.annotations.DbKey;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.DbFieldsConst;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.KeyStrategy;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;
import com.custom.logic.LogicDeleteFieldSqlHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.custom.dbconfig.DbFieldsConst.DB_JOIN_TABLE;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/4
 * @Description 解析字段处理类
 */
public class DbParserFieldHandler {

    private DbAnnotationsParserHandler dbAnnoParser;

    public DbParserFieldHandler(){
        dbAnnoParser = new DbAnnotationsParserHandler();
    }

    /**
     * 根据实体类获取对应数据库表中所有字段
     */
    public <T> String[] getDbFields(Class<T> t){
        List<String> dbFields = new ArrayList<>();
        List<Map<String, Object>> elementList = dbAnnoParser.getParserByDbField(t);
        elementList.forEach(elementMap -> dbFields.add(String.valueOf(elementMap.get(DbFieldsConst.DB_FIELD_NAME))));
        return dbFields.toArray(new String[0]);
    }

    /**
     * 获取一个类的所有标注@DbField注解的属性名称
     */
    public <T> String[] getFiledNames(Class<T> t) throws Exception {
        Field[] fields = dbAnnoParser.getFields(t);
        List<String> fieldList = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isAnnotationPresent(DbField.class)) {
                fieldList.add(fields[i].getName());
            }
        }
        return fieldList.toArray(new String[0]);
    }

    /**
     * 获取对象中所有的属性值
     */
    public <T> List<Object> getFieldsVal(T t, String[] fields) throws Exception {
        List<Object> dataList = new ArrayList<>();
        for (String field : fields) {
            Object fieldValue = this.getFieldValue(t, field);
            dataList.add(fieldValue);
        }
        return dataList;
    }

    /**
     * 获取字段值
     */
    public <T> Object getFieldValue(T t,  String fieldName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object value;
        String firstLetter;
        String getter;
        try {
            firstLetter = fieldName.substring(0, 1).toUpperCase();
            getter = SymbolConst.GET + firstLetter + fieldName.substring(1);
            Method method = t.getClass().getMethod(getter);
            value = method.invoke(t);
        }catch (NoSuchMethodException e){
            try {
                firstLetter = fieldName.substring(0, 1).toUpperCase();
                Method method = t.getClass().getMethod(SymbolConst.IS + firstLetter + fieldName.substring(1));
                value = method.invoke(t);
            }catch (NoSuchMethodException v) {
                Method method = t.getClass().getMethod(fieldName);
                value = method.invoke(t);
            }
        }
        return value;
    }

    /**
     * 获取实体类对应的表名
     */
    public <T> String getDbTableName(Class<T> t) {
        return dbAnnoParser.getParserByDbTable(t).get(DbFieldsConst.TABLE_NAME).toString();
    }

    /**
     * 获取表别名
     */
    public <T> String getDbTableAlias(Class<T> t) {
        return dbAnnoParser.getParserByDbTable(t).get(DbFieldsConst.TABLE_ALIAS).toString();
    }

    /**
    * 获取@DbTable解析
    */
    public <T> Map<String, Object> getDbTable(Class<T> t) {
        return dbAnnoParser.getParserByDbTable(t);
    }

    /**
     * 该类是否有@DbKey注解
     */
    public <T> boolean isDbKeyTag(Class<T> t) {
        Field[] fields = t.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(DbKey.class)) return true;
        }
        return false;
    }

    /**
     * 表连接的条件->是: 已经有一个, 否: 还没有
     */
    boolean isExistRelation(List<Map<String, String>> list, String tableName, String condition) {
        if(list.size() <= 0) return false;
        for (Map<String, String> tableMap : list) {
            if(tableName.equals(tableMap.get(DB_JOIN_TABLE)) && condition.equals(tableMap.get(DbFieldsConst.DB_JOIN_CONDITION))) return true;
        }
        return false;
    }

    /**
     * 获取查询sql
     */
   public <T> String getSelectSql(Class<T> t) throws Exception {
        if(!CustomUtil.isDbRelationTag(t) && !t.isAnnotationPresent(DbJoinTables.class)) {
            return getBasicTableSql(t);
        }
        if(t.isAnnotationPresent(DbJoinTables.class)) {
            return getJoinTableSql(t);
        }
        return getRelatedTableSql(t);
   }

   /**
    * 获取表的基础查询sql
    */
    private <T> String getBasicTableSql(Class<T> t) throws Exception {
        return String.format("select %s \nfrom %s %s ", getBasicFieldSql(t), getDbTableName(t), getDbTableAlias(t));
    }

    /**
     * 获取表查询的主表查询字段
     */
    private <T> String getBasicFieldSql(Class<T> t) throws Exception {
        StringJoiner fieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        String alias = getDbTableAlias(t);//获取表的别名

        if(isDbKeyTag(t)){
            Map<String, Object> dbKeyMap = dbAnnoParser.getParserByDbKey(t);
            String keySql = String.format("%s.`%s` `%s`", alias, dbKeyMap.get(DbFieldsConst.DB_KEY), dbKeyMap.get(DbFieldsConst.KEY_FIELD));
            fieldSql.add(keySql);
        }

        List<Map<String, Object>> dbFieldMapList = dbAnnoParser.getParserByDbField(t);
        for (Map<String, Object> dbFieldMap : dbFieldMapList) {
            String dbFieldSql = String.format("%s.`%s` `%s`", alias, dbFieldMap.get(DbFieldsConst.DB_FIELD_NAME), dbFieldMap.get(DbFieldsConst.DB_CLASS_FIELD));
            fieldSql.add(dbFieldSql);
        }
        return fieldSql.toString();
    }

    /**
    * 获取关联查询的sql1
    */
    private <T> String getRelatedTableSql(Class<T> t) throws Exception {
        List<Map<String, String>> relationMapList = dbAnnoParser.getParserByDbRelated(t);
        StringBuilder relationSql = new StringBuilder();//最终生成的表连接sql
        List<Map<String, String>> joinTables = new ArrayList<>();//要关联的表
        StringJoiner relationTableFields = new StringJoiner(",");//查询关联表字段sql
        //需要查询的字段sql(basicTableSql + relationTableFields)
        StringBuilder queryFieldSql = new StringBuilder();

        for (Map<String, String> relationMap : relationMapList) {
            Map<String, String> tableMap = new HashMap<>();
            String joinSql = "";
            String joinTable = relationMap.get(DbFieldsConst.DB_JOIN_TABLE);//关联表
            String condition = relationMap.get(DbFieldsConst.DB_JOIN_CONDITION);//关联条件
            String joinAlias = relationMap.get(DbFieldsConst.DB_JOIN_ALIAS);//关联表别名
            String joinStyle = relationMap.get(DbFieldsConst.DB_JOIN_STYLE);//关联方式
            //如果有存在相同的关联条件,那么就不需要再次拼接关联
            if(!isExistRelation(joinTables, joinTable, condition)) {
                tableMap.put(DbFieldsConst.DB_JOIN_TABLE, joinTable);
                tableMap.put(DbFieldsConst.DB_JOIN_CONDITION, condition);
                joinTables.add(tableMap);
                joinSql = String.format("\n%s %s %s on %s", joinStyle, joinTable, joinAlias ,condition);
                relationSql.append(joinSql);
            }
            relationTableFields.add(String.format("%s.`%s` `%s`",joinAlias, relationMap.get(DbFieldsConst.DB_JOIN_MAP_FIELD), relationMap.get(DbFieldsConst.DB_JOIN_MAP_CLASS_FIELD)));
        }
        Map<String, Object> tableMap = dbAnnoParser.getParserByDbTable(t);
        queryFieldSql.append(String.format("%s %s", getBasicFieldSql(t), SymbolConst.SEPARATOR_COMMA_2));
        queryFieldSql.append(relationTableFields);
        return String.format("select %s \nfrom %s %s \n%s",
                queryFieldSql, tableMap.get(DbFieldsConst.TABLE_NAME), tableMap.get(DbFieldsConst.TABLE_ALIAS), relationSql);
    }

    /**
     * 获取关联查询的sql2
     */
    private <T> String getJoinTableSql(Class<T> t) throws Exception {

        List<String> dbJoinSqls = dbAnnoParser.getParserByDbJoinTable(t);
        List<Map<String, String>> dbMapFields = dbAnnoParser.getParserDbMap(t);
        String joinFieldSql = dbMapFields.stream()
                .map(mapField -> String.format(",%s `%s`",
                        CustomUtil.getJoinFieldStr(String.valueOf(mapField.get(DbFieldsConst.DB_MAP))),
                        mapField.get(DbFieldsConst.DB_MAP_FIELD))
                ).collect(Collectors.joining());

        String selectSql = String.format("select %s %s \nfrom %s %s ", getBasicFieldSql(t), joinFieldSql, getDbTableName(t), getDbTableAlias(t));
        StringBuilder joinTaleSql = new StringBuilder();
        dbJoinSqls.stream().map(joinSql -> String.format("%s\n", joinSql)).forEach(joinTaleSql::append);
        return String.format("%s \n%s", selectSql, joinTaleSql);
    }

    /**
     * 获取数据库主键
     */
    public <T> String getDbFieldKey(Class<T> t) throws Exception {
        return String.valueOf(dbAnnoParser.getParserByDbKey(t).get(DbFieldsConst.DB_KEY));
    }

    /**
     * 获取主键对应的java属性字段
     */
    public <T> String getFieldKey(Class<T> t) throws Exception {
        return String.valueOf(dbAnnoParser.getParserByDbKey(t).get(DbFieldsConst.KEY_FIELD));
    }

    /**
     * 获取主键对应的java属性类型
     */
    public <T> Field getFieldKeyType(Class<T> t) throws Exception {
        return t.getDeclaredField(String.valueOf(dbAnnoParser.getParserByDbKey(t).get(DbFieldsConst.KEY_FIELD)));
    }

    /**
     * 生成主键值
     */
    public <T> Object generateKey(T t) throws Exception {
        Object value = null;
        Map<String, Object> parserByDbKey = dbAnnoParser.getParserByDbKey(t.getClass());
        KeyStrategy keyType = (KeyStrategy) parserByDbKey.get(DbFieldsConst.KEY_STRATEGY);
        String fieldKey = String.valueOf(parserByDbKey.get(DbFieldsConst.KEY_FIELD));
        switch (keyType){
            case INPUT:
                value = getFieldValue(t, fieldKey);
                if(JudgeUtilsAx.isEmpty(value)) {
                    throw new CustomCheckException(ExceptionConst.EX_PRIMARY_KEY_VALUE_NOT_EMPTY + t.getClass());
                }
                break;
            case AUTO:
                if(!isKeyByValid(t.getClass(), fieldKey)) {
                    throw new CustomCheckException(ExceptionConst.EX_PRIMARY_CANNOT_MATCH + t.getClass());
                }
                value = 0;
                break;
            case UUID:
                Field field = t.getClass().getDeclaredField(fieldKey);
                Class<?> type = field.getType();
                if(type != String.class) {
                    throw new CustomCheckException(ExceptionConst.EX_PRIMARY_CANNOT_MATCH + t.getClass());
                }
                value = CustomUtil.getUUID();
                break;
        }
    return value;
    }

    /**
     * 判断主键是否是有效类型
     */
    public <T> boolean isKeyByValid(Class<T> t, String fieldKey) throws Exception {
        Field field = t.getDeclaredField(fieldKey);
        Class<?> type = field.getType();
        return type == int.class || type == Integer.class || type == long.class || type == Long.class;
    }

    /**
     * 获取主键增值类型
     */
    public <T> KeyStrategy getDbKeyType(Class<T> t) throws Exception {
        return (KeyStrategy) dbAnnoParser.getParserByDbKey(t).get(DbFieldsConst.KEY_STRATEGY);
    }

    /**
     * 通过表字段名称找到属性名称
     */
    public <T> String getProFieldName(Class<T> t, String dbField) throws Exception {
        if(isDbKeyTag(t)) {
            Map<String, Object> parserByDbKey = dbAnnoParser.getParserByDbKey(t);
            if(dbField.equals(parserByDbKey.get(DbFieldsConst.DB_KEY))) {
                return String.valueOf(parserByDbKey.get(DbFieldsConst.KEY_FIELD));
            }
        }
        List<Map<String, Object>> parserByDbField = dbAnnoParser.getParserByDbField(t);
        for (Map<String, Object> objectMap : parserByDbField) {
            if(dbField.equals(objectMap.get(DbFieldsConst.DB_FIELD_NAME))) {
                return String.valueOf(objectMap.get(DbFieldsConst.DB_CLASS_FIELD));
            }
        }
        throw new SQLException(String.format("Unknown column name: '%s'", dbField));
    }


    /**
    * 按条件删除时，匹配是否是逻辑删除
    */
    public <T> String getDeleteSql(Class<T> t, String logicValidSql, String logicInValidSql, String condition) throws Exception{
        Map<String, Object> tableMap = dbAnnoParser.getParserByDbTable(t);
        Object alias = tableMap.get(DbFieldsConst.TABLE_ALIAS);
        Object table = tableMap.get(DbFieldsConst.TABLE_NAME);
        String sql;

        if(JudgeUtilsAx.isNotEmpty(logicInValidSql)) {
            sql = String.format(" update %s %s set %s.%s where %s.%s %s", table, alias, alias, logicInValidSql, alias, logicValidSql, condition);
        }else {
            sql = String.format(" delete from %s %s where 1 = 1 %s", table, alias, condition);
        }
        return sql;
    }

    /**
    * 获取表的常用属性
    */
    public <T> String[] getTableBaseFieldArray(Class<T> t) throws Exception {
        Map<String, Object> tableMap = getDbTable(t);
        String alias = String.valueOf(tableMap.get(DbFieldsConst.TABLE_ALIAS));
        String table = String.valueOf(tableMap.get(DbFieldsConst.TABLE_NAME));
        String dbKey = getDbFieldKey(t);
        return new String[]{table, alias, dbKey};
    }

    /**
    * 添加逻辑删除的部分sql
    */
    public <T> String checkConditionAndLogicDeleteSql(Class<T> t, String condition, String logicSql) {
        LogicDeleteFieldSqlHandler handler = () -> {
            String sql;
            if (JudgeUtilsAx.isNotEmpty(condition)) {
                sql = JudgeUtilsAx.isNotEmpty(logicSql) ?
                        String.format(" \nwhere %s.%s %s ", getDbTableAlias(t), logicSql, condition) : String.format(" \nwhere 1 = 1 %s ", condition);
            } else {
                sql = JudgeUtilsAx.isNotEmpty(logicSql) ?
                        String.format(" \nwhere %s.%s ", getDbTableAlias(t), logicSql) : condition;
            }
            return sql;
        };
        return handler.handleSql();
    }

}
