package com.custom.action.condition;

import com.custom.action.condition.support.TableSupport;
import com.custom.action.core.DbKeyParserModel;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.core.ColumnPropertyMap;
import com.custom.action.core.TableInfoCache;
import com.custom.action.core.TableParseModel;
import com.custom.comm.utils.ReflectUtil;
import com.custom.comm.utils.lambda.LambdaUtil;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.lambda.SFunction;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 解析Function函数中字段名称
 * @author   Xiao-Bai
 * @since  2022/3/3 14:45
 **/
@Slf4j
public class DefaultColumnParseHandler<T> implements ColumnParseHandler<T> {

    private final Class<T> thisClass;
    private final List<Field> fieldList;
    private final Map<String, String> fieldMapper;

    /**
     * 表中对字段的映射
     */
    private final static Map<String, Map<String, ColumnFieldCache>> COLUMN_CACHE = new ConcurrentHashMap<>();

    public DefaultColumnParseHandler(Class<T> thisClass, TableSupport tableSupport) {
        this.thisClass = thisClass;
        this.fieldMapper = tableSupport.fieldMap();
        this.fieldList = tableSupport.fields();
    }



    /**
     * 创建表与字段的映射缓存
     */
    public static <T> Map<String, ColumnFieldCache> createColumnCache(TableParseModel<T> tableModel) {
        Map<String, ColumnFieldCache> cacheMap = new HashMap<>();
        List<PropertyDescriptor> properties = tableModel.getPropertyList();
        for (PropertyDescriptor property : properties) {
            String getter = property.getReadMethod().getName();
            String field = property.getName();
            String column = tableModel.getFieldMapper().get(field);
            ColumnFieldCache fieldCache = new ColumnFieldCache(field, getter, column);
            cacheMap.put(property.getReadMethod().getName(), fieldCache);
        }
        COLUMN_CACHE.put(tableModel.getEntityClass().getName(), cacheMap);
        return cacheMap;
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
        SerializedLambda serializedLambda = LambdaUtil.resolve(func);
        String implMethodName = serializedLambda.getImplMethodName();

        Map<String, ColumnFieldCache> fieldCacheMap = Optional.ofNullable(COLUMN_CACHE.get(thisClass.getName()))
                .orElseGet(() -> createColumnCache(TableInfoCache.getTableModel(thisClass)));

        ColumnFieldCache fieldCache = fieldCacheMap.get(implMethodName);
        if (fieldCache == null) {
            throw new CustomCheckException("Cannot find a matching property with method name: '%s'", implMethodName);
        }
        return fieldCache.getField();
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
        SerializedLambda serializedLambda = LambdaUtil.resolve(func);
        String implMethodName = serializedLambda.getImplMethodName();
        return ColumnPropertyMap.parse2Column(thisClass, implMethodName);
    }


}
