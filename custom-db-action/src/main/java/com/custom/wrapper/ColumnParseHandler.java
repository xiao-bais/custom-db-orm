package com.custom.wrapper;

import com.custom.comm.CustomUtil;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.CustomCheckException;
import com.custom.sqlparser.*;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 14:45
 * @Desc：解析Function函数中字段名称
 **/
public class ColumnParseHandler<T> {

    private final Class<T> cls;

    private final Field[] fields;

    public ColumnParseHandler(Class<T> cls, Field[] fields) {
        this.cls = cls;
        this.fields = fields;
    }

    public ColumnParseHandler(Class<T> cls) {
        this.cls = cls;
        this.fields = CustomUtil.getFields(cls);
    }

    /**
     * 获取表字段column
     */
    public String getColumn(SFunction<T,?> fun) {
        Field field = getField(fun);
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(cls);
        return parseField(field, tableModel);
    }

    @SafeVarargs
    public final String[] getColumn(SFunction<T, ?>... fun) {
        Field[] targetFields = parseColumns(fun);
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(cls);
        String[] selectColumns = new String[targetFields.length];
        for (int i = 0; i < targetFields.length; i++) {
            selectColumns[i] = parseField(targetFields[i], tableModel);
        }
        return selectColumns;
    }


    private String parseField(Field targetField, TableSqlBuilder<T> tableModel) {

        // 主键解析模板
        DbKeyParserModel<T> keyParserModel = tableModel.getKeyParserModel();
        if (keyParserModel != null && keyParserModel.getField().equals(targetField)) {
            return keyParserModel.getFieldSql();
        }
        // 除主键外的表字段解析模板
        List<DbFieldParserModel<T>> fieldParserModels = tableModel.getFieldParserModels();
        Optional<DbFieldParserModel<T>> firstDbFieldParserModel = fieldParserModels.stream().filter(x -> x.getField().equals(targetField)).findFirst();
        if(firstDbFieldParserModel.isPresent()) {
            return firstDbFieldParserModel.get().getFieldSql();
        }

        // 关联表方式1的解析模板
        List<DbRelationParserModel<T>> relatedParserModels = tableModel.getRelatedParserModels();
        Optional<DbRelationParserModel<T>> firstDbRelationParserModel = relatedParserModels.stream().filter(x -> x.getField().equals(targetField)).findFirst();
        if(firstDbRelationParserModel.isPresent()) {
            return firstDbRelationParserModel.get().getFieldSql();
        }

        // 关联表方式2的解析模板
        List<DbJoinTableParserModel<T>> joinDbMappers = tableModel.getJoinDbMappers();
        Optional<DbJoinTableParserModel<T>> firstDbJoinTableParserModel = joinDbMappers.stream().filter(x -> x.getField().equals(targetField)).findFirst();
        if(firstDbJoinTableParserModel.isPresent()) {
            return firstDbJoinTableParserModel.get().getFieldSql();
        }
        throw new CustomCheckException(targetField + " 未找到 @Db*注解");
    }

    /**
     * 从Function中获取实体的属性字段
     */
    @SafeVarargs
    public final Field[] parseColumns(SFunction<T, ?>... fun) {
        List<Field> fieldList = new ArrayList<>(fun.length);
        for (SFunction<T, ?> function : fun) {
            fieldList.add(getField(function));
        }
        return fieldList.toArray(new Field[0]);
    }

    public Field getField(SFunction<T, ?> fun) {
        SerializedLambda serializedLambda = getSerializedLambda(fun);
        String implMethodName = serializedLambda.getImplMethodName();
        String fieldName = implMethodName.substring(SymbolConst.GET.length());
        fieldName = fieldName.replaceFirst(String.valueOf(fieldName.charAt(0)), String.valueOf(fieldName.charAt(0)).toLowerCase());
        String finalFieldName = fieldName;
        Optional<Field> firstField = Arrays.stream(fields).filter(x -> x.getName().equals(finalFieldName)).findFirst();
        if (firstField.isPresent()) {
            return firstField.get();
        }
        throw new CustomCheckException(String.format("Unknown method: '%s', not found in class'%s', or please create getter or setter method with boxing type", implMethodName, cls.getName()));
    }



    /**
     * 从Function中获取序列化的信息
     */
    private SerializedLambda getSerializedLambda(SFunction<T, ?> fun) {

        Method writeMethod;
        SerializedLambda serializedLambda = null;
        try {
            // 从function中取出序列化方法
            writeMethod = fun.getClass().getDeclaredMethod("writeReplace");
            writeMethod.setAccessible(true);

            boolean accessible = writeMethod.isAccessible();
            serializedLambda = (SerializedLambda)writeMethod.invoke(fun);
            writeMethod.setAccessible(accessible);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return serializedLambda;
    }


}
