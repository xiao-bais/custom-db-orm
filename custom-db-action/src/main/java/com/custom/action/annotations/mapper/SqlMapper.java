package com.custom.action.annotations.mapper;

import java.lang.annotation.*;

/**
 * @author Xiao-Bai
 * @date 2021/12/1 23:23
 * @desc: 作用与继承BasicDao相同，两者选其一
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SqlMapper {
}
