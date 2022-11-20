package com.custom.jdbc.handler;

import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.ReflectUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Xiao-Bai
 * @date 2022/11/13 22:08
 * @desc
 */
@SuppressWarnings("unchecked")
public class ResultSetTypeMappedHandler<T> {

    private static final Map<String, MappedTargetCache<?>> OBJECT_HANDLE_CACHE = new ConcurrentHashMap<>();

    /**
     * 返回结果集中该行映射的对象
     * @param rs jdbc结果集对象
     * @return this type value
     */
    public T getTargetObject(ResultSet rs)
            throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        ResultSetMetaData rsMetaData = rs.getMetaData();
        Map<String, Object> resMap = new HashMap<>();
        // 循环取值
        for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
            // 查询的列名
            String label = rsMetaData.getColumnLabel(i + 1);
            // 列名对应的值
            Object value = rs.getObject(label);

            resMap.put(label, value);
        }

        return this.mappingTargetObject(resMap);
    }


    /**
     * 将map映射到目标类，并返回该类型的实例对象
     */
    private T mappingTargetObject(Map<String, Object> resultMap)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        // 实例化该对象，前提是需要存在无参构造，否则可能抛出异常
        T instance = ReflectUtil.getInstance(resClass);

        MappedTargetCache<T> mappedTargetCache = (MappedTargetCache<T>) OBJECT_HANDLE_CACHE.get(resClass.getName());
        if (mappedTargetCache == null) {
            mappedTargetCache = new MappedTargetCache<>(resClass);
            OBJECT_HANDLE_CACHE.putIfAbsent(resClass.getName(), mappedTargetCache);
        }

        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {

            String label = entry.getKey();
            if (isUnderlineToCamel) {
                label = CustomUtil.underlineToCamel(label);
            }
            Object value = entry.getValue();
            if (value != null) {
                MappedTargetCache.FieldCache fieldCache = mappedTargetCache.findForName(label);

                if (fieldCache != null) {
                    TypeHandler<?> typeHandler = fieldCache.getTypeHandler();
                    // 映射时，最终的格式或类型转换
                    Object newValue = typeHandler.getTypeValue(value);
                    PropertyDescriptor descriptor = fieldCache.getDescriptor();
                    Method writeMethod = descriptor.getWriteMethod();
                    writeMethod.invoke(instance, newValue);
                }
            }

        }

        return instance;
    }



    /**
     * 将rs中的结果写入数组(泛型的类型仅限于常用的基本类型、字符串、日期类型)
     * @param res 待写入的数组
     * @param rs jdbc结果集对象
     */
    public void writeForArrays(Object res, ResultSet rs) throws SQLException {
        ObjectTypeConverter<T> otc = new ObjectTypeConverter<>(resClass);
        // 只取每一行的第一列
        int len = 0;
        while (rs.next()) {
            T value = otc.getRsValue(rs, 1);
            Array.set(res, len, value);
            len ++;
        }
    }


    /**
     * 将rs中的结果写入map(泛型的类型仅限于常用的基本类型、字符串、日期)
     * @param map 待写入的map
     * @param rs jdbc结果集对象
     */
    public void writeForMap(Map<String, T> map, ResultSet rs) throws SQLException {
        ObjectTypeConverter<T> otc = new ObjectTypeConverter<>(resClass);
        ResultSetMetaData rsMetaData = rs.getMetaData();
        // 该类型的转换处理
        AbstractTypeHandler<T> thisTypeHandler = ((AbstractTypeHandler<T>) otc.getThisTypeHandler()).getClone();
        thisTypeHandler.setUnderlineToCamel(isUnderlineToCamel);

        // 循环取值
        for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
            // 查询的列名
            String label = rsMetaData.getColumnLabel(i + 1);
            // 列名对应的值
            T value = thisTypeHandler.getTypeValue(label);
            map.put(label, value);
        }
    }


    /**
     * 将rs中的结果写入集合(泛型的类型仅限于常用的基本类型、字符串、日期)
     * @param coll 待写入的list
     * @param rs jdbc结果集对象
     */
    public void writeForCollection(Collection<T> coll, ResultSet rs) throws SQLException {
        ObjectTypeConverter<T> otc = new ObjectTypeConverter<>(resClass);
        // 只取每一行的第一列
        while (rs.next()) {
            T value = otc.getRsValue(rs, 1);
            coll.add(value);
        }
    }


    /**
     * 转换基础类型
     */
    public T getTargetValue(Object val) {
        ObjectTypeConverter<T> otc = new ObjectTypeConverter<>(resClass, val);
        return otc.getValue();
    }

    public T getTargetValue(ResultSet rs, int index) throws SQLException {
        ObjectTypeConverter<T> otc = new ObjectTypeConverter<>(resClass);
        return otc.getRsValue(rs, index);
    }


    private final Class<T> resClass;
    private final boolean isUnderlineToCamel;

    public ResultSetTypeMappedHandler(Class<T> resClass, boolean isUnderlineToCamel) {
        this.resClass = resClass;
        this.isUnderlineToCamel = isUnderlineToCamel;
    }
}
