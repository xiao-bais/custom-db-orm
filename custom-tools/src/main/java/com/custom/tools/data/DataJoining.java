package com.custom.tools.data;

import com.custom.comm.utils.AssertUtil;
import com.custom.comm.utils.ReflectUtil;
import com.custom.comm.utils.lambda.LambdaUtil;
import com.custom.comm.utils.lambda.SFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author  Xiao-Bai
 * @since  2022/11/23 0:33
 * 数据合并工具
 * list1 与 list2 中存在可关联的值时
 * 可用此类，将两个集合合并成一个集合.
 * 有点类似于sql中的表关联
 */
public class DataJoining<T> {

    private final Logger log = LoggerFactory.getLogger(DataJoining.class);


    private final Class<T> targetClass;
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

    public DataJoining(Class<T> targetClass, List<T> primaryList, List<T> otherList, JoinCondition<T> condition) {
        this.targetClass = targetClass;
        this.primaryList = primaryList;
        this.otherList = otherList;
        AssertUtil.notNull(condition, "合并条件不允许为空");
        this.condition = condition;
        try {
            this.properties = ReflectUtil.getProperties(targetClass);
        } catch (IntrospectionException e) {
            log.error(e.toString(), e);
        }
    }

    public void setOtherList(List<T> otherList) throws IntrospectionException {
        this.properties = ReflectUtil.getProperties(targetClass);
        this.otherList = otherList;
    }

    /**
     * 获取合并后的结果
     * @param fields 指定要合并的字段(java属性)
     */
    public void joinStart(String... fields) {

        if (primaryList == null) {
            return;
        } else if (otherList == null) {
            return;
        }

        // 复制一份
        List<T> copyList = new ArrayList<>(otherList);

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
                        Object targetVal = null;
                        try {
                            targetVal = property.getReadMethod().invoke(target);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            log.error(e.toString(), e);
                        }
                        if (targetVal != null) {
                            try {
                                property.getWriteMethod().invoke(currObj, targetVal);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                log.error(e.toString(), e);
                            }
                        }
                    }

                }
                copyList.remove(target);
            }
        }

        if (!copyList.isEmpty()) {
            primaryList.addAll(copyList);
        }
    }


    /**
     * 获取合并后的结果
     * @param fields 指定要合并的字段(java属性对应的Function)
     */
    @SafeVarargs
    public final void joinStart(SFunction<T, ?>... fields) {

        List<String> fieldList = new ArrayList<>();
        for (SFunction<T, ?> field : fields) {
            String targetGetter = LambdaUtil.getImplMethodName(field);

            properties.stream()
                    .filter(op -> op.getReadMethod().getName().equals(targetGetter))
                    .map(PropertyDescriptor::getName)
                    .findFirst().ifPresent(fieldList::add);
        }
        joinStart(fieldList.toArray(new String[0]));
    }



}
