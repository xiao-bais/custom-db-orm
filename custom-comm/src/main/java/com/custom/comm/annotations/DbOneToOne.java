package com.custom.comm.annotations;


import com.custom.comm.utils.Constants;

import java.lang.annotation.*;

/**
 * @author Xiao-Bai
 * @date 2022/8/21 0:55
 * @desc 一对一映射注解，在查询的时候，若存在一对一的关系对象，则可启用该注解
 * <p>
 *     注意: 该注解只对查询生效
 * </p>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbOneToOne {

    /**
     * 一对一关联的实体对象
     * <br/> 若不填，则默认取被该注解标注的类型对象
     * <br/> 注意: 该注解不可作用在java自带的类型下({@link Object}, {@link java.util.Map} 类除外)，否则在查询时会抛错
     */
    Class<?> joinTarget() default Object.class;

    /**
     * 当前类的关联字段(java属性即可)
     * <br/> 若不填，则默认取当前对象的主键 {@link DbKey}
     */
    String thisField() default Constants.EMPTY;

    /**
     * 与当前对象关联的字段(java属性即可)
     * <br/> 若不填，则默认取注解作用在该属性对象上的主键 {@link DbKey}
     */
    String joinField() default Constants.EMPTY;

    /**
     * 若在一对一查询时，查询到不止一条数据的情况下，是否抛出异常
     * <br/> true - 是
     * <br/> false - 否，取多条中的第一条
     */
    boolean isThrowErr() default true;


}
