package com.custom.action.sqlparser;

import com.custom.comm.JudgeUtil;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/24 11:14
 * @Desc：实体解析模板缓存
 **/
@SuppressWarnings("unchecked")
public class TableInfoCache {

    /**
     * 实体解析模板缓存
     * <br/>key-实体全路径名称
     * <br/>value-实体解析模板 {@link TableSqlBuilder}
     */
    private final static Map<String, Object> tableModel = new CustomLocalCache();
    private static Boolean underlineToCamel = false;

    public static <T> TableSqlBuilder<T> getTableModel(Class<T> cls) {
        TableSqlBuilder<T> tableSqlBuilder = (TableSqlBuilder<T>) tableModel.get(cls.getName());
        if(Objects.isNull(tableSqlBuilder)) {
            tableSqlBuilder = new TableSqlBuilder<>(cls, underlineToCamel);
            tableModel.put(cls.getName(), tableSqlBuilder);
        }
        return tableSqlBuilder;
    }
    public static void setUnderlineToCamel(boolean underlineToCamel) {
        TableInfoCache.underlineToCamel = underlineToCamel;
    }


    /**
     * 获取表字段到java字段的映射
     */
    public static <T> Map<String, String> getColumnMap(Class<T> cls) {
        return getTableModel(cls).getColumnMapper();
    }

    /**
     * 获取java字段到表字段的映射
     */
    public static <T> Map<String, String> getFieldMap(Class<T> cls) {
        return getTableModel(cls).getFieldMapper();
    }

    /**
     * 获取表名称
     */
    public static <T> String tableName(Class<T> cls) {
        return getTableModel(cls).getTable();
    }


    /**
     * 表的逻辑删除字段缓存
     * <br/>key-实体全路径名称
     * <br/>value(是否存在逻辑删除字段)-true or false
     */
    private final static Map<String, Object> tableLogic = new CustomLocalCache();

    public static void setTableLogic(String key, Object val) {
        tableLogic.put(key, val);
    }

    public static Boolean isExistsLogic(String table) {
        return (Boolean) tableLogic.get(table);
    }

    /**
     * 实体查询时，是否存在相互引用的情况
     * <br/>key - 实体全路径名称
     * <br/>value - 是否存在逻辑删除字段: true or false
     */
    private final static Map<String, Object> entityExistCrossReference = new CustomLocalCache();

    public static boolean existCrossReference(Class<?> thisClass, Class<?> joinClass) {
        Boolean thisExist = (Boolean) entityExistCrossReference.get(thisClass.getName());
        if (thisExist != null && thisExist) {
            return true;
        }
        Boolean joinExist = (Boolean) entityExistCrossReference.get(joinClass.getName());
        if (joinExist != null && joinExist) {
            return true;
        }

        TableSqlBuilder<?> tableModel = getTableModel(joinClass);
        if (JudgeUtil.isNotEmpty(tableModel.getOneToOneFieldList())) {
            for (Field field : tableModel.getOneToOneFieldList()) {
                if (field.getType().isAssignableFrom(thisClass)
                        || thisClass.isAssignableFrom(field.getType())) {
                    entityExistCrossReference.put(thisClass.getName(), true);
                    entityExistCrossReference.put(joinClass.getName(), true);
                    return true;
                }
            }
        }
        entityExistCrossReference.put(thisClass.getName(), false);
        entityExistCrossReference.put(joinClass.getName(), false);
        return false;
    }



}
