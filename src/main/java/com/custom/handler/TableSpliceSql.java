package com.custom.handler;

import com.custom.dbconfig.DbFieldsConst;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.ExceptionConst;
import com.custom.enums.DbMediaType;
import com.custom.enums.KeyStrategy;
import com.custom.exceptions.CustomCheckException;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2021/8/19
 * @Description 拼接sql语句专用类
 */
public class TableSpliceSql {

    private DbAnnotationsParserHandler annotationsParser;

    public TableSpliceSql(DbAnnotationsParserHandler annotationsParser) {
        this.annotationsParser = annotationsParser;
    }

    /**
     * 创建表
     */
    public <T> String getCreateTableSql(Class<T> t) throws Exception {
        List<Map<String, Object>> fieldMapList = annotationsParser.getParserByDbField(t);
        String tableName = annotationsParser.getParserByDbTable(t).get(DbFieldsConst.TABLE_NAME).toString();
        String taleByFieldKeySql = this.createTaleByFieldKeySql(t);
        String tableByFieldSql = this.createTableByFieldSql(fieldMapList);
        if(JudgeUtilsAx.isEmpty(tableByFieldSql)) {
            throw new CustomCheckException(ExceptionConst.EX_DBFIELD__NOTFOUND + t);
        }
        return String.format("create table `%s` (\n%s %s) ",
                tableName, taleByFieldKeySql, tableByFieldSql);
    }

    /**
     * 获取除主键外的其他字段创建语句
     */
    private String createTableByFieldSql(List<Map<String, Object>> mapList) {
        StringJoiner createFieldSql = new StringJoiner(",");
        for (Map<String, Object> map : mapList) {
            String fieldName = String.valueOf(map.get(DbFieldsConst.DB_FIELD_NAME));
            //如果注解上没注明对应的表字段,就以java属性字段来填充
            if(JudgeUtilsAx.isEmpty(fieldName)){
                fieldName = String.valueOf(map.get(DbFieldsConst.DB_CLASS_FIELD));
            }
            DbMediaType fieldType = (DbMediaType) map.get(DbFieldsConst.DB_FIELD_TYPE);

            String length = SymbolConst.EMPTY;
            if(DbMediaType.DbDate != fieldType && DbMediaType.DbDateTime != fieldType) {
                length = String.format("(%s)", map.get(DbFieldsConst.DB_FIELD_LENGTH));
            }
            String isNullField = SymbolConst.EMPTY;
            boolean isNull = (Boolean) map.get(DbFieldsConst.DB_IS_NULL);
            if(!isNull) {
                isNullField = DbFieldsConst.NOT_NULL;
            }
            String createField = String.format("`%s` %s%s %s comment '%s'\n",
                    fieldName, fieldType.getType(), length, isNullField, map.get(DbFieldsConst.DB_FIELD_DESC));
            createFieldSql.add(createField);
        }
        return createFieldSql.toString();
    }

    /**
     * 获取主键字段的创建语句
     */
    private <T> String createTaleByFieldKeySql(Class<T> t) throws Exception {
        String primaryKeySql = SymbolConst.EMPTY;
        Map<String, Object> map = annotationsParser.getParserByDbKey(t);
        if(map.isEmpty()) return primaryKeySql;

        String dbKey = String.valueOf(map.get(DbFieldsConst.DB_KEY));
        if(JudgeUtilsAx.isEmpty(dbKey)) {
            dbKey = String.valueOf(map.get(DbFieldsConst.KEY_FIELD));
        }
        DbMediaType dbType = (DbMediaType) map.get(DbFieldsConst.KEY_TYPE);//获取主键数据类型
        KeyStrategy keyType = (KeyStrategy) map.get(DbFieldsConst.KEY_STRATEGY);//获取主键增值类型
        String keyStrategy = SymbolConst.EMPTY;
        if(KeyStrategy.AUTO.equals(keyType)) {
            if(!CustomUtil.checkPrimaryKeyIsAutoIncrement(dbType))
                throw new CustomCheckException(ExceptionConst.EX_PRIMARY_CANNOT_MATCH);
                keyStrategy = DbFieldsConst.AUTO_INCREMENT;
        }
        primaryKeySql = String.format("`%s` %s(%s) primary key not null %s comment '%s' \n,",
                dbKey, dbType.getType(), map.get(DbFieldsConst.KEY_LENGTH), keyStrategy, map.get(DbFieldsConst.KEY_DESC));
        return primaryKeySql;
    }
}
