package com.custom.comm.annotations;

import com.custom.comm.enums.FillStrategy;
import com.custom.comm.utils.Constants;
import com.custom.comm.enums.DbType;

import java.lang.annotation.*;

/**
 * sql字段
 * @author  Xiao-Bai
 * @since 2021/6/30
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbField {

    /**
     * 字段名称
     * @return value
     */
    String value() default Constants.EMPTY;

    /**
     * 数据类型
     * @return dataType
     */
    DbType dataType() default DbType.DbVarchar;

    /**
     * 字段说明
     * @return desc
     */
    String desc() default Constants.EMPTY;

    /**
     * 是否为空，只在创建表的时候用到
     * @return isNull
     */
    boolean isNull() default true;

    /**
     * 是否存在该表字段，作用与{@link DbNotField}一致
     */
    boolean exist() default true;

    /**
     * 自动填充策略，在参数为实体时的插入或者修改(逻辑删除)时，自动填充指定字段的值
     * <br/> 注意：插入或修改时，只有当参数是实体对象时才会生效
     * <br/> 逻辑删除时也会进行填充(前提是有配置填充)
     */
    FillStrategy fillStrategy() default FillStrategy.DEFAULT;


}
