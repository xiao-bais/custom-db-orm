package com.custom.comm.annotations;

import java.lang.annotation.*;

/**
 *  在实体中指定忽略的属性，不属于表的字段
 *  若字段上同时标注了{@link DbField} 或者 {@link DbKey} 与此类注解，则此注解记为无效
 *  该类作用与{@link DbField#exist()} = false 一致
 * @author Xiao-Bai
 * @date 2022/7/20 0020 17:48
 * @see DbField#exist()
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbNotField {

}
