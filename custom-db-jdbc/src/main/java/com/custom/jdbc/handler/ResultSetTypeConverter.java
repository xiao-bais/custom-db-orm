package com.custom.jdbc.handler;

import com.custom.comm.utils.CustomUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Xiao-Bai
 * @date 2022/11/13 22:08
 * @desc
 */
public class ResultSetTypeConverter<T> {



    /**
     * 将rs中的结果写入自定义对象
     * @param rs jdbc结果集对象
     * @return this type value
     */
    public T writeInCustomType(ResultSet rs) throws SQLException, InstantiationException, IllegalAccessException {
        ObjectTypeConverter<T> otc = new ObjectTypeConverter<>();
        // 实例化该对象，前提是需要存在无参构造，否则可能抛出异常
        T instance = resClass.newInstance();
        List<Field> loadFields = Arrays.stream(CustomUtil.loadFields(resClass)).collect(Collectors.toList());

        Map<String, Object> resMap = new HashMap<>();

        return instance;
    }



    /**
     * 将rs中的结果写入数组(泛型的类型仅限于常用的基本类型、字符串、日期类型)
     * @param res 待写入的数组
     * @param rs jdbc结果集对象
     */
    public void writeInArrays(Object res, ResultSet rs) throws SQLException {
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
    public void writeInMap(Map<String, T> map, ResultSet rs) throws SQLException {
        ObjectTypeConverter<T> otc = new ObjectTypeConverter<>(resClass);
        ResultSetMetaData rsMetaData = rs.getMetaData();
        // 该类型的转换处理
        AbstractTypeHandler<T> thisTypeHandler = (AbstractTypeHandler<T>) otc.getThisTypeHandler();
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
    public void writeInCollection(Collection<T> coll, ResultSet rs) throws SQLException {
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


    private final Class<T> resClass;

    private boolean isUnderlineToCamel;

    public ResultSetTypeConverter(Class<T> resClass) {
        this.resClass = resClass;
    }

    public ResultSetTypeConverter(Class<T> resClass, boolean isUnderlineToCamel) {
        this.resClass = resClass;
        this.isUnderlineToCamel = isUnderlineToCamel;
    }
}
