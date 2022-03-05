package com.custom.wrapper;

import com.custom.comm.CustomUtil;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.CustomCheckException;

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

    public ColumnParseHandler(Class<T> cls) {
        this.cls = cls;
        this.fields = CustomUtil.getFields(cls);
    }

    /**
     * 从Function中获取实现的字段
     */
    @SafeVarargs
    public final Field[] parseColumns(SFunction<T, ?>... fun) {
        List<Field> fieldList = new ArrayList<>(fun.length);
        for (SFunction<T, ?> function : fun) {
            SerializedLambda serializedLambda = getSerializedLambda(function);
            String implMethodName = serializedLambda.getImplMethodName();
            String fieldName = implMethodName.substring(SymbolConst.GET.length());
            fieldName = fieldName.replaceFirst(String.valueOf(fieldName.charAt(0)), String.valueOf(fieldName.charAt(0)).toLowerCase());
            String finalFieldName = fieldName;
            Optional<Field> firstField = Arrays.stream(fields).filter(x -> x.getName().equals(finalFieldName)).findFirst();
            if (firstField.isPresent()) {
                fieldList.add(firstField.get());
            }else throw new CustomCheckException(String.format("Unknown method: '%s', not found in class'%s', or please create getter or setter method with boxing type", implMethodName, cls.getName()));
        }
        return fieldList.toArray(new Field[0]);
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
