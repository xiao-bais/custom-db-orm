package com.custom.action.condition;

import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.sqlparser.ColumnFunctionMap;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.jdbc.GlobalDataHandler;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 14:45
 * @Desc：解析Function函数中字段名称
 **/
public class DefaultColumnParseHandler<T> implements ColumnParseHandler<T> {

    private final Class<T> thisClass;
    private final List<Field> fieldList;
    private final TableSqlBuilder<T> tableModel;
    private final Map<String, String> fieldMapper;
    /**
     * 每个对象的Function函数，java属性，以及sql字段名称缓存
     */
    private final static Map<Class<?>, List<ColumnFunctionMap<?>>> COLUMN_PARSE_MAP = new ConcurrentHashMap<>();

    public DefaultColumnParseHandler(Class<T> thisClass) {
        this.thisClass = thisClass;
        this.tableModel = TableInfoCache.getTableModel(thisClass);
        this.fieldMapper = TableInfoCache.getFieldMap(thisClass);
        this.fieldList = Arrays.stream(tableModel.getFields()).collect(Collectors.toList());
    }

    @Override
    public Class<T> getThisClass() {
        return this.thisClass;
    }

    @Override
    public List<Field> loadFields() {
        return this.fieldList;
    }

    /**
     * 获取java属性字段
     */
    @Override
    public String parseToField(SFunction<T, ?> func) {
        JudgeUtil.checkObjNotNull(func);
        SerializedLambda serializedLambda = parseSerializedLambda(func);
        String implMethodName = serializedLambda.getImplMethodName();
        // 猜想的java属性名称
        String guessFieldName;

        // 若方法以get开头，则以去除get前缀的方式去找寻java属性
        if (implMethodName.startsWith(SymbolConstant.GETTER)) {
            guessFieldName  = implMethodName.substring(SymbolConstant.GETTER.length());
        }
        // 若方法以is开头，则以去除is前缀的方式去找寻java属性
        else if (implMethodName.startsWith(SymbolConstant.IS)) {
            guessFieldName  = implMethodName.substring(SymbolConstant.GETTER.length());
        }
        //否则，判定方法名与属性名称相同
        else {
            guessFieldName = implMethodName;
        }

        // 最终的java属性名称
        final String finalFieldName = guessFieldName.replaceFirst(String.valueOf(guessFieldName.charAt(0)),
                String.valueOf(guessFieldName.charAt(0)).toLowerCase());

//        COLUMN_PARSE_MAP.get()


        Optional<Field> firstField = fieldList.stream().filter(x -> x.getName().equals(finalFieldName)).findFirst();
        firstField.ifPresent(op -> {

        });
        if (firstField.isPresent()) {
            return firstField.get().getName();
        }

        if (!tableModel.isFindUpDbJoinTables()) {
            ExThrowsUtil.toCustom("当@DbTable的findUpDbJoinTables设置为false时，不支持使用父类属性进行条件构造");
        }
        throw new CustomCheckException(String.format("Unknown method: '%s', not found in class'%s', " +
                        "field may not exist, or please create a getter or setter method of boxing type for the field",
                implMethodName, thisClass.getName()));
    }

    /**
     * 获取java属性字段对应的表字段
     */
    @Override
    public String parseToColumn(SFunction<T, ?> func) {
        String field = parseToField(func);
        if(GlobalDataHandler.hasSqlKeyword(field)) {
            field = GlobalDataHandler.wrapperSqlKeyword(field);
        }
        String column = fieldMapper.get(field);
        if(Objects.isNull(column)) {
            throw new CustomCheckException("属性" + field + "上未标注Db*注解");
        }
        return column;
    }
}
