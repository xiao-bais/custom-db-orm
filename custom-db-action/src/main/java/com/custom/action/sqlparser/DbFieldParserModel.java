package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractTableModel;
import com.custom.action.util.DbUtil;
import com.custom.comm.*;
import com.custom.comm.annotations.DbField;
import com.custom.comm.enums.DbType;
import com.custom.jdbc.GlobalDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

/**
 * @author Xiao-Bai
 * @date 2021/12/1 23:39
 * @desc:对于@DbField注解的解析
 */
public class DbFieldParserModel<T> extends AbstractTableModel<T> {

    private static final Logger logger = LoggerFactory.getLogger(DbFieldParserModel.class);

    private T entity;

    /**
    * java字段名称
    */
    private String fieldName;

    /**
    * sql列名
    */
    private String column;

    /**
     * 查询时，指定查询字段的包装
     * 例：concat('user-', a.name) columnName
     * <p>
     *     当设置了默认值后，查询时若值为null，则set默认值
     *     ifnull(a.name, '默认值') AS 映射字段
     * </p>
     */
    private String wrapperColumn;

    /**
     * 查询时若当前字段为字符串类型，是否null转为空字符串
     * <p>
     *     当设置了默认值后，查询时若值为null，则set默认值
     *     ifnull(a.name, '默认值') AS 映射字段
     * </p>
     */
    private boolean isNullToEmpty;

    /**
    * sql字段类型
    */
    private DbType dbType;

    /**
    * java字段类型
    */
    private Class<?> type;

    /**
     * 字段属性
     */
    private final Field field;

    /**
    * 字段值
    */
    private Object value;

    /**
    * sql字段长度
    */
    private String length;

    /**
    * sql字段说明
    */
    private String desc;

    /**
    * 是否为空
    */
    private boolean isNull;

    /**
     * 是否开启了表的默认值
     * 开启以后，在新增时若java属性值为null，则自动添加给定的默认值
     * 若字段跟表同时开启了默认值配置，则以字段设置的默认值优先
     */
    private boolean enabledDefaultValue;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 是否标注了@DbField注解
     */
    private final boolean existsDbField;

    public DbFieldParserModel(T t, Field field, String table, String alias, boolean underlineToCamel, boolean enabledDefaultValue, boolean existsDbField) {
        this(field, table, alias, underlineToCamel, enabledDefaultValue, existsDbField);
        this.entity = t;
    }

    /**
     * 存在@DbField注解下的属性解析
     */
    public DbFieldParserModel(Field field, String table, String alias, boolean underlineToCamel, boolean enabledDefaultValue, boolean existsDbField) {
        this.fieldName = GlobalDataHandler.hasSqlKeyword(field.getName()) ? GlobalDataHandler.wrapperSqlKeyword(field.getName()) : field.getName();
        this.type = field.getType();
        this.field = field;
        this.existsDbField = existsDbField;
        if (!existsDbField) {
            this.noneAnnotationParser(table, alias, underlineToCamel);
            return;
        }
        DbField annotation = field.getAnnotation(DbField.class);
        if (JudgeUtil.isEmpty(annotation.value())) {
            this.column = underlineToCamel ? CustomUtil.camelToUnderline(this.fieldName) : this.fieldName;
        }else {
            this.column = annotation.value();
        }
        if(GlobalDataHandler.hasSqlKeyword(column)) {
            this.column = GlobalDataHandler.wrapperSqlKeyword(column);
        }
        this.wrapperColumn = annotation.wrapperColumn();
        this.isNullToEmpty = annotation.isNullToEmpty();
        this.isNull = annotation.isNull();
        this.desc = annotation.desc();
        this.dbType = annotation.dataType() == DbType.DbVarchar ? DbType.getDbMediaType(field.getType()) : annotation.dataType();
        this.length = this.dbType.getLength();
        this.defaultValue = annotation.defaultValue();
        this.enabledDefaultValue = enabledDefaultValue;
        super.setTable(table);
        super.setAlias(alias);
        this.defaultValueInjector();
    }

    /**
     * 无注解的属性解析
     */
    public void noneAnnotationParser(String table, String alias, boolean underlineToCamel) {
        this.column = underlineToCamel ? CustomUtil.camelToUnderline(this.fieldName) : this.fieldName;
        if(GlobalDataHandler.hasSqlKeyword(column)) {
            this.column = GlobalDataHandler.wrapperSqlKeyword(column);
        }
        super.setTable(table);
        super.setAlias(alias);
        this.defaultValueInjector();
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }


    /**
     * 默认值注入
     */
    private void defaultValueInjector() {
        // 若本身未设置默认值，则给定表的默认值
        if (JudgeUtil.isEmpty(this.defaultValue) && this.enabledDefaultValue) {
            this.defaultValue = this.dbType.getValue();
            return;
        }

        Object value = null;
        String tmpValue = String.valueOf(this.defaultValue);
        if (String.valueOf(this.defaultValue).matches(RexUtil.check_number)) {
            try {
                if (Integer.class.isAssignableFrom(this.type)) {
                    value = Integer.parseInt(tmpValue);
                } else if (Long.class.isAssignableFrom(this.type)) {
                    value = Long.parseLong(tmpValue);
                    // 如果java类型是Boolean 而默认值需要一致，true可写"true" 或 "1"，否则判定为false
                } else if (Boolean.class.isAssignableFrom(this.type)) {
                    value = ConvertUtil.conBool(tmpValue);
                }
            } catch (NumberFormatException e) {
                if (Double.class.isAssignableFrom(this.type)) {
                    value = Double.parseDouble(tmpValue);
                } else if (Float.class.isAssignableFrom(this.type)) {
                    value = Float.parseFloat(tmpValue);
                } else if (BigDecimal.class.isAssignableFrom(this.type)) {
                    value = new BigDecimal(tmpValue);
                }
            }
        }else if (Boolean.class.isAssignableFrom(this.type)) {
            value = Boolean.parseBoolean(tmpValue);
        }else if (CharSequence.class.isAssignableFrom(this.type) && !tmpValue.equals("null")) {
            value = tmpValue;
        }
        this.defaultValue = value;
    }


    /**
     * 构建创建表的sql语句
     */
    @Override
    public String buildTableSql() {
        if (!existsDbField) {
            return SymbolConstant.EMPTY;
        }
        String newColumn = this.column;
        if (!RexUtil.hasRegex(this.column, RexUtil.back_quotes)) {
            newColumn = GlobalDataHandler.wrapperSqlKeyword(this.column);
        }
        StringBuilder fieldSql = new StringBuilder(newColumn).append(" ");
        if (dbType == DbType.DbDate || dbType == DbType.DbDateTime)
            fieldSql.append(dbType.getType()).append(" ");
        else
            fieldSql.append(dbType.getType())
                    .append(SymbolConstant.BRACKETS_LEFT)
                    .append(this.length)
                    .append(SymbolConstant.BRACKETS_RIGHT).append(" ");
        return fieldSql.append("default ").append(CharSequence.class.isAssignableFrom(this.type) ?
                    String.valueOf(this.defaultValue).equals(SymbolConstant.EMPTY) ? "''"
                            : String.format("'%s'", this.defaultValue) : this.defaultValue).append(" ")
                .append(this.isNull ? "null" : "not null").append(" ")
                .append(String.format(" comment '%s'", this.desc)).toString();
    }

    @Override
    protected Object getValue(T x) {
        try {
            value = getFieldValue(x, fieldName);
        }catch (InvocationTargetException | IllegalAccessException
                | NoSuchMethodException | NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return value;
    }

    @Override
    public String getFieldSql() {
        return DbUtil.fullSqlColumn(this.getAlias(), this.column);
    }

    @Override
    public String getSelectFieldSql() {
        if (JudgeUtil.isNotEmpty(this.defaultValue)) {
            return DbUtil.ifNull(this.column, this.defaultValue, this.fieldName);
        }
        if (JudgeUtil.isNotEmpty(this.wrapperColumn)) {
            return DbUtil.wrapperSqlColumn(this.wrapperColumn, this.fieldName, this.isNullToEmpty);
        }
        String selectColumn = DbUtil.fullSqlColumn(this.getAlias(), this.column);
        String column = this.isNullToEmpty ? DbUtil.ifNull(selectColumn) : selectColumn;
        return DbUtil.sqlSelectWrapper(column, this.fieldName);
    }

    public Object getValue() {
        try {
            String readValueField = fieldName;
            char[] chars = readValueField.toCharArray();
            if (chars[0] == 96 && chars[chars.length - 1] == 96) {
                readValueField = GlobalDataHandler.removeSqlKeywordWrapper(fieldName);
            }
            value = getFieldValue(entity, readValueField);
        }catch (InvocationTargetException | IllegalAccessException
                | NoSuchMethodException | NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return value;
    }

    protected void setValue(Object value) {
        super.setFieldValue(entity, this.field, value);
    }

    public Field getField() {
        return field;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean isExistsDbField() {
        return existsDbField;
    }
}
