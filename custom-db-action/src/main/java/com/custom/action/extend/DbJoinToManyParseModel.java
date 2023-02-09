package com.custom.action.extend;

import com.custom.action.core.TableInfoCache;
import com.custom.action.core.TableParseModel;
import com.custom.action.dbaction.AbstractJoinToResult;
import com.custom.action.util.DbUtil;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.StrUtils;
import com.custom.comm.utils.Constants;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbOneToMany;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * 一对多注解{@link DbOneToMany}解析对象
 * @author   Xiao-Bai
 * @since  2022/8/22 1:18
 */
public class DbJoinToManyParseModel extends AbstractJoinToResult {

    /**
     * 该列表是否以升序的方式来排序
     * <br/> 若{@link DbOneToMany#sortField()} 指定了排序字段，则以该字段来进行列表的升降序排列
     * <br/> 若{@link DbOneToMany#sortField()} 未指定排序字段，则默认以关联的对象主键{@link DbKey} 排序(前提是被关联的对象存在主键)
     */
    private final boolean orderByAsc;

    /**
     * 指定排序的字段，同时对上面的orderByAsc起作用(java属性即可)
     */
    private final String sortField;

    /**
     * 补充条件
     * <br/> 若当前主外键条件还不足以满足一对多的查询，则可由此补充剩余的条件
     * <br/> 例如 and a.type = 2
     */
    private final String fillSuffix;


    public DbJoinToManyParseModel(Field joinToManyField, Class<?> topNode) {
        DbOneToMany oneToMany = joinToManyField.getAnnotation(DbOneToMany.class);
        this.orderByAsc = oneToMany.orderByAsc();
        this.sortField = oneToMany.sortField();
        this.fillSuffix = oneToMany.fillSuffix();
        super.setThisField(oneToMany.thisField());
        super.setJoinField(oneToMany.joinField());

        if (Map.class.isAssignableFrom(oneToMany.joinTarget())
                || Map.class.isAssignableFrom(joinToManyField.getType())) {
            throw new UnsupportedOperationException(String.format("In '%s', @DbOneToMany.joinTarget() or field type does not support working on java.util.Map",
                    joinToManyField));
        }

        // 主表
        setThisClass(joinToManyField.getDeclaringClass());
        // 关联对象不是Object类时，去给定的目标类
        if (!Object.class.equals(oneToMany.joinTarget())) {
            setJoinTarget(oneToMany.joinTarget());

        } else {
            // 否则取关联字段的类型
            Class<?> joinCollectionType = joinToManyField.getType();
            if (!Collection.class.isAssignableFrom(joinCollectionType)) {
                throw new IllegalArgumentException("@DbOneToMany is not allowed to act on properties of non collection type : " + joinToManyField);
            }
            ParameterizedTypeImpl genericType = (ParameterizedTypeImpl) joinToManyField.getGenericType();
            Type[] actualTypeArguments = genericType.getActualTypeArguments();
            if (actualTypeArguments.length == 0 || CustomUtil.isNotAllowedGenericType((Class<?>) actualTypeArguments[0])) {
                throw new CustomCheckException("@DbOneToMany does not support acting on Java property with generic type %s in Field : %s"
                        , actualTypeArguments[0], joinToManyField);
            }
            Class<?> joinTarget = (Class<?>) actualTypeArguments[0];

            if (Object.class.equals(joinTarget)) {
                throw new CustomCheckException("The entity type associated in %s DbOneToOne.joinTarget() is not specified"
                        , joinToManyField.getDeclaringClass());
            }
            setJoinTarget(joinTarget);
        }
        super.initJoinProperty(joinToManyField.getName(), oneToMany.strategy(), topNode);

    }


    public String queryCondPrefix() {
        return DbUtil.formatSqlAndCondition(getJoinAlias(), getJoinColumn()) + " " + this.fillSuffix;
    }


    /**
     * 排序规则
     */
    public String queryCondSuffix() {
        if (StrUtils.isBlank(sortField.trim())) {
            return "";
        }
        TableParseModel<?> tableModel = TableInfoCache.getTableModel(getJoinTarget());
        return Constants.ORDER_BY
                + tableModel.getFieldMapper().get(sortField) + " "
                + (orderByAsc ? Constants.ASC : Constants.DESC);
    }
}
