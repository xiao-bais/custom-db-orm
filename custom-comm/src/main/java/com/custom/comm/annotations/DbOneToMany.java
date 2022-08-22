package com.custom.comm.annotations;

import java.lang.annotation.*;

/**
 * @author Xiao-Bai
 * @date 2022/8/21 0:55
 * @desc 一对多映射注解，在查询的时候，若存在一对多的关系对象，则可启用该注解
 * <p>
 *     注意: 该注解只对查询生效
 * </p>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbOneToMany {

    /**
     * 一对多关联的实体对象
     * <br/> 若不填，则默认取被该注解标注的类型对象
     * <br/> 注意: 该注解不可作用在泛型为java自带的类型下({@link Object} 类除外)，否则在查询时会抛错
     */
    Class<?> joinTarget() default Object.class;

    /**
     * 当前类的关联字段(java属性即可)
     * <br/> 若不填，则默认取当前对象的主键 {@link DbKey}
     */
    String thisField() default "";

    /**
     * 与当前对象关联的字段(java属性即可)
     * <br/> 若不填，则默认取注解作用在该属性对象上的主键 {@link DbKey}
     */
    String joinField() default "";

    /**
     * 该列表是否以升序的方式来排序
     * <br/> 若{@link DbOneToMany#sortField()} 指定了排序字段，则以该字段来进行列表的升降序排列
     * <br/> 若{@link DbOneToMany#sortField()} 未指定排序字段，则默认以关联的对象主键{@link DbKey} 排序(前提是被关联的对象存在主键)
     */
    boolean orderByAsc() default true;

    /**
     * 指定排序的字段，同时对上面的orderByAsc起作用(java属性即可)
     */
    String sortField() default "";

}
