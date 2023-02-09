package com.custom.action.interfaces;

import com.custom.comm.utils.lambda.SFunction;
import com.custom.comm.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于条件构造器中的字段解析接口定义
 * @author  Xiao-Bai
 * @since  2022/8/11 2:07
 */
public interface ColumnParseHandler<T> {


    /**
     * 获取当前解析的实体Class对象
     */
    Class<T> getThisClass();

    /**
     * 获取当前解析的实体class对象中的所有字段属性(包含父类的字段，所有字段仅限于private，并且未被final、static修饰的属性)
     * <p>
     *     若当前使用非custom-x，则需手动实现该方法
     * </p>
     */
   default List<Field> loadFields() {
       Class<T> thisClass = getThisClass();
       return ReflectUtil.loadFields(thisClass, false);
   }

    /**
     * 解析函数接口后，返回解析后的java字段属性
     * @param funcList 接口函数数组
     * @return
     */
   default List<String> parseToFields(List<SFunction<T, ?>> funcList) {
       List<String> parseFieldList = new ArrayList<>();
       for (SFunction<T, ?> func : funcList) {
           parseFieldList.add(this.parseToField(func));
       }
       return parseFieldList;
   }

    /**
     * 解析函数接口后，返回解析后的java字段属性
     * @param func
     * @return
     */
    String parseToField(SFunction<T, ?> func);

    /**
     * 解析函数接口后，返回解析后的java字段对应的sql表字段
     * @param funcList 接口函数数组
     * @return
     */
   default List<String> parseToColumns(List<SFunction<T, ?>> funcList) {
       List<String> parseColumnList = new ArrayList<>();
       for (SFunction<T, ?> func : funcList) {
           parseColumnList.add(this.parseToColumn(func));
       }
       return parseColumnList;
   }

    /**
     * 解析函数接口后，返回解析后的sql字表字段(带别名)
     * @param func
     * @return
     */
    String parseToColumn(SFunction<T, ?> func);

    /**
     * 解析函数接口后，返回解析后的sql表字段(不带别名)
     */
    String parseToNormalColumn(SFunction<T, ?> func);

}
