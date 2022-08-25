package com.custom.action.condition;

import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.sqlparser.ColumnFunctionMap;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.comm.Asserts;
import com.custom.comm.CustomUtil;
import com.custom.jdbc.GlobalDataHandler;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/3 14:45
 * @Desc：解析Function函数中字段名称
 **/
@Slf4j
public class DefaultColumnParseHandler<T> implements ColumnParseHandler<T> {

    private final Class<T> thisClass;
    private final List<Field> fieldList;
    private final TableSqlBuilder<T> tableModel;
    private final Map<String, String> fieldMapper;
    /**
     * 每个对象的Function函数，java属性，以及sql字段名称缓存
     */
    private final List<ColumnFunctionMap<T>> COLUMN_PARSE_LIST = new ArrayList<>();

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
        Asserts.notNull(func);

        SerializedLambda serializedLambda = this.parseSerializedLambda(func);
        ColumnFunctionMap<T> columnFunctionMap = this.columnFunctionMapResolve(serializedLambda);
        if (columnFunctionMap != null) {
            return columnFunctionMap.getPropertyName();
        }
        Class<?> entityClass = CustomUtil.loadReallyClassName(serializedLambda.getImplClass());

        if (entityClass == null) {
            throw new NullPointerException();
        }
        ColumnFunctionMap<T> functionMapsCache = this.createFunctionMapsCache(func, serializedLambda);
        return functionMapsCache.getPropertyName();
    }

    /**
     * 查找缓存
     */
    @SuppressWarnings("unchecked")
    private ColumnFunctionMap<T> columnFunctionMapResolve(SerializedLambda serializedLambda) {
        String implMethodSignature = serializedLambda.getImplMethodSignature();
        Class<?> propertyType = CustomUtil.loadReallyClassName(
                implMethodSignature.substring(3, implMethodSignature.indexOf(";"))
        );

        if (JudgeUtil.isNotEmpty(COLUMN_PARSE_LIST)) {
            List<ColumnFunctionMap<T>> functionMapList = COLUMN_PARSE_LIST.stream()
                    .filter(op -> op.getGetMethodName().equals(serializedLambda.getImplMethodName()))
                    .filter(op -> op.getPropertyType().equals(propertyType))
                    .collect(Collectors.toList());
            if (functionMapList.size() > 1) {
                StringJoiner expMethodNames = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
                functionMapList.stream()
                        .map(ColumnFunctionMap::getGetMethodName)
                        .forEach(expMethodNames::add);
                ExThrowsUtil.toCustom("Lambda parsing error, found multiple matching results: " + expMethodNames);
            }else if (functionMapList.isEmpty()) {
                String implMethodName = serializedLambda.getImplMethodName();
                throw new CustomCheckException("Cannot find a matching property with method name: '%s'", implMethodName);
            }
            return functionMapList.get(0);
        }
        return null;
    }


    /**
     * 创建函数解析缓存
     */
    public ColumnFunctionMap<T> createFunctionMapsCache(SFunction<T, ?> function, SerializedLambda serializedLambda) {

        for (Field field : this.fieldList) {
            ColumnFunctionMap<T> functionMap = new ColumnFunctionMap<>();
            String fieldName = field.getName();
            functionMap.setPropertyName(fieldName);
            try {
                PropertyDescriptor descriptor = new PropertyDescriptor(fieldName, this.thisClass);
                Method readMethod = descriptor.getReadMethod();
                functionMap.setGetMethodName(readMethod.getName());
                functionMap.setColumn(fieldMapper.get(fieldName));
                functionMap.setPropertyType(field.getType());
                functionMap.setAliasColumn(fieldMapper.get(fieldName));
                functionMap.setLambdaFunction(function);
                functionMap.setEntityClass(thisClass);
                COLUMN_PARSE_LIST.add(functionMap);
            }catch (IntrospectionException e) {
                log.error(e.toString(), e);
            }
        }
        String implMethodName = serializedLambda.getImplMethodName();
        return COLUMN_PARSE_LIST.stream()
                .filter(op -> op.getGetMethodName().equals(implMethodName))
                .findFirst()
                .orElseThrow(() -> {
                    if (!tableModel.isFindUpDbJoinTables()) {
                        ExThrowsUtil.toCustom("当@DbTable的findUpDbJoinTables设置为false时，不支持使用父类属性进行条件构造");
                    }
                    return new CustomCheckException("Cannot find a matching property with method name: '%s'", implMethodName);
                });
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
