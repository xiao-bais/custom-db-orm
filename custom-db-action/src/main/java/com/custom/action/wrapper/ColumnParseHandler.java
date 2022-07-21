package com.custom.action.wrapper;

import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.jdbc.GlobalDataHandler;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 14:45
 * @Desc：解析Function函数中字段名称
 **/
public class ColumnParseHandler<T> {

    private final Class<T> cls;
    private final Field[] fields;
    private final TableSqlBuilder<T> tableModel;
    private final Map<String, String> fieldMapper;

    public ColumnParseHandler(Class<T> cls) {
        this.cls = cls;
        this.tableModel = TableInfoCache.getTableModel(cls);
        this.fieldMapper = TableInfoCache.getFieldMap(cls);
        this.fields = tableModel.getFields();
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
            String field = getField(funs[i]);
            String targetField = fieldMapper.get(field);
            if(targetField == null) {
                ExThrowsUtil.toCustom("属性" + field + "上未标注Db*注解");
            }
            selectColumns[i] = targetField;
        }
        return selectColumns;
    }

    /**
     * 获取java属性字段对应的表字段
     */
    public final String getColumn(SFunction<T, ?> func) {
        String field = getField(func);
        if(GlobalDataHandler.hasSqlKeyword(field)) {
            field = GlobalDataHandler.wrapperSqlKeyword(field);
        }
        String column = fieldMapper.get(field);
        if(Objects.isNull(column)) {
            throw new CustomCheckException("属性" + field + "上未标注Db*注解");
        }
        return column;
    }


    /**
     * 获取java属性字段
     */
    public String getField(SFunction<T, ?> fun) {
        JudgeUtil.checkObjNotNull(fun);
        SerializedLambda serializedLambda = getSerializedLambda(fun);
        String implMethodName = serializedLambda.getImplMethodName();
        String fieldName = implMethodName.substring(SymbolConstant.GETTER.length());
        fieldName = fieldName.replaceFirst(String.valueOf(fieldName.charAt(0)), String.valueOf(fieldName.charAt(0)).toLowerCase());
        String finalFieldName = fieldName;
        Optional<Field> firstField = Arrays.stream(fields).filter(x -> x.getName().equals(finalFieldName)).findFirst();
        if (firstField.isPresent()) {
            return firstField.get().getName();
        }
        if (!tableModel.isFindUpDbJoinTables()) {
            ExThrowsUtil.toCustom("当@DbTable的findUpDbJoinTables设置为false时，不支持使用父类属性进行条件构造");
        }
        throw new CustomCheckException(String.format("Unknown method: '%s', not found in class'%s', field may not exist, or please create a getter or setter method of boxing type for the field", implMethodName, cls.getName()));
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
            serializedLambda = (SerializedLambda)writeMethod.invoke(fun);
            writeMethod.setAccessible(false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (Objects.isNull(serializedLambda)) {
            ExThrowsUtil.toCustom("无法解析：" + fun);
        }

        return serializedLambda;
    }

    public Field[] getFields() {
        return fields;
    }
}
