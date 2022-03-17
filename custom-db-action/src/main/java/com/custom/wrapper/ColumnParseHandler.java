package com.custom.wrapper;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.CustomCheckException;
import com.custom.sqlparser.*;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 14:45
 * @Desc：解析Function函数中字段名称
 **/
public class ColumnParseHandler<T> {

    private final Class<T> cls;
    private final Field[] fields;
    private final Map<String, String> fieldMapper;

    public ColumnParseHandler(Class<T> cls) {
        this.cls = cls;
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(cls);
        fields = tableModel.getFields();
        this.fieldMapper = tableModel.getFieldMapper();
    }


    /**
     * 获取java属性字段
     */
    @SafeVarargs
    public final String[] getField(SFunction<T, ?>... funs) {
        String[] selectColumns = new String[funs.length];
        for (int i = 0; i < selectColumns.length; i++) {
            selectColumns[i] = getField(funs[i]);
        }
        return selectColumns;
    }

    /**
     * 获取java属性字段对应的表字段
     */
    @SafeVarargs
    public final String[] getColumn(SFunction<T, ?>... funs) {
        String[] selectColumns = new String[funs.length];
        for (int i = 0; i < selectColumns.length; i++) {
            selectColumns[i] = fieldMapper.get(getField(funs[i]));
        }
        return selectColumns;
    }

    /**
     * 获取java属性字段对应的表字段
     */
    public final String getColumn(SFunction<T, ?> func) {
        return fieldMapper.get(getField(func));
    }


    /**
     * 获取java属性字段
     */
    public String getField(SFunction<T, ?> fun) {
        SerializedLambda serializedLambda = getSerializedLambda(fun);
        String implMethodName = serializedLambda.getImplMethodName();
        String fieldName = implMethodName.substring(SymbolConst.GET.length());
        fieldName = fieldName.replaceFirst(String.valueOf(fieldName.charAt(0)), String.valueOf(fieldName.charAt(0)).toLowerCase());
        String finalFieldName = fieldName;
        Optional<Field> firstField = Arrays.stream(fields).filter(x -> x.getName().equals(finalFieldName)).findFirst();
        if (firstField.isPresent()) {
            return firstField.get().getName();
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
