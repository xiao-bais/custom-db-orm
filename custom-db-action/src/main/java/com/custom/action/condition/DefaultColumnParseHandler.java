package com.custom.action.condition;

import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.sqlparser.ColumnPropertyMap;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableParseModel;
import com.custom.action.util.LambdaResolveUtil;
import com.custom.comm.Asserts;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
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
    private final Map<String, String> fieldMapper;
    /**
     * 每个对象的Function函数，java属性，以及sql字段名称缓存
     */
    private final List<ColumnPropertyMap<T>> columnParseList;

    public DefaultColumnParseHandler(Class<T> thisClass) {
        this.thisClass = thisClass;
        TableParseModel<T> tableModel = TableInfoCache.getTableModel(thisClass);
        this.fieldMapper = TableInfoCache.getFieldMap(thisClass);
        this.columnParseList = tableModel.columnPropertyMaps();
        if (JudgeUtil.isEmpty(columnParseList)) {
            ExThrowsUtil.toCustom("该类找不到可解析的字段：" + this.thisClass);
        }
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
        SerializedLambda serializedLambda = LambdaResolveUtil.resolve(func);
        String implMethodName = serializedLambda.getImplMethodName();

        List<ColumnPropertyMap<T>> columnPropertyMaps = columnParseList.stream()
                .filter(op -> op.getGetMethodName().equals(implMethodName))
                .collect(Collectors.toList());
        if (JudgeUtil.isEmpty(columnPropertyMaps)) {
            throw new CustomCheckException("Cannot find a matching property with method name: '%s'", implMethodName);
        } else if (columnPropertyMaps.size() > 1) {
            StringJoiner expMethodNames = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
            columnPropertyMaps.stream()
                    .map(ColumnPropertyMap::getGetMethodName)
                    .forEach(expMethodNames::add);
            ExThrowsUtil.toCustom("Lambda parsing error, found multiple matching results: " + expMethodNames);
        }
        return columnPropertyMaps.get(0).getPropertyName();
    }


    /**
     * 获取java属性字段对应的表字段
     */
    @Override
    public String parseToColumn(SFunction<T, ?> func) {
        String field = parseToField(func);
        String column = fieldMapper.get(field);
        if(Objects.isNull(column)) {
            throw new CustomCheckException("Property '" + field + "',  the table field mapped by this attribute cannot be found");
        }
        return column;
    }

    @Override
    public String parseToNormalColumn(SFunction<T, ?> func) {
        SerializedLambda serializedLambda = LambdaResolveUtil.resolve(func);
        String implMethodName = serializedLambda.getImplMethodName();
        return ColumnPropertyMap.parse2Column(thisClass, implMethodName);
    }
}
