package com.custom.tools.data;

import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.ReflectUtil;
import com.custom.comm.utils.lambda.LambdaUtil;
import com.custom.comm.utils.lambda.SFunction;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Xiao-Bai
 * @date 2022/11/23 0:33
 * 数据合并工具
 * list1 与 list2 中存在可关联的值时
 * 可用此类，将两个集合合并成一个集合.
 * 有点类似于sql中的表关联
 */
public class DataJoining<T> {

    /**
     * 主集合
     */
    private final List<T> primaryList;
    /**
     * 待合并的集合
     */
    private List<T> otherList;
    /**
     * 合并的条件
     */
    private final JoinCondition<T> condition;
    /**
     * 属性描述信息
     */
    private List<PropertyDescriptor> properties;

    public DataJoining(Class<T> targetClass, List<T> primaryList, List<T> otherList, JoinCondition<T> condition) throws IntrospectionException {
        this.primaryList = primaryList;
        this.otherList = otherList;
        Asserts.notNull(condition, "合并条件不允许为空");
        this.condition = condition;
        this.properties = ReflectUtil.getProperties(targetClass);
    }

    public void setOtherList(Class<T> targetClass, List<T> otherList) throws IntrospectionException {
        this.properties = ReflectUtil.getProperties(targetClass);
        this.otherList = otherList;
    }

    /**
     * 获取合并后的结果
     * @param fields 指定要合并的字段(java属性)
     */
    public List<T> getResult(String... fields) throws InvocationTargetException, IllegalAccessException {

        List<T> result = new ArrayList<>();

        if (primaryList == null) {
            if (otherList == null) {
                return result;
            } else return otherList;
        } else if (otherList == null) {
            return result;
        }

        // 复制一份
        List<T> copyList = new ArrayList<>(primaryList);

        for (T currObj : primaryList) {

            Predicate<T> doJoin = condition.doJoin(currObj);
            T target = otherList.stream().filter(doJoin).findFirst().orElse(null);
            if (target != null) {
                for (String field : fields) {

                    PropertyDescriptor property = properties.stream()
                            .filter(op -> op.getName().equals(field))
                            .findFirst()
                            .orElse(null);

                    if (property != null) {
                        Object targetVal = property.getReadMethod().invoke(target);
                        property.getWriteMethod().invoke(currObj, targetVal);
                    }

                }
                copyList.remove(target);
            }
            result.add(currObj);
        }

        if (!copyList.isEmpty()) {
            result.addAll(copyList);
        }
        return result;
    }


    /**
     * 获取合并后的结果
     * @param fields 指定要合并的字段(java属性对应的Function)
     */
    public List<T> getResult(SFunction<T, ?>... fields) throws InvocationTargetException, IllegalAccessException {

        List<String> fieldList = new ArrayList<>();
        for (SFunction<T, ?> field : fields) {
            String targetGetter = LambdaUtil.getImplMethodName(field);

            properties.stream()
                    .map(PropertyDescriptor::getReadMethod)
                    .map(Method::getName)
                    .filter(t -> t.equals(targetGetter)).findFirst().ifPresent(fieldList::add);

        }
        return getResult(fieldList.toArray(new String[0]));
    }



}
