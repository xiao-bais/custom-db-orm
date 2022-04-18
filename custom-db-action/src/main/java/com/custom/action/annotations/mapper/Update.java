package com.custom.action.annotations.mapper;

import java.lang.annotation.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 16:52
 * @Desc：适用于dao层动态代理方法上使用
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Update {

    /**
     * 示例sql1：update employee set name = #{name} where id = #{id}
     * 示例sql2：delete from employee where name = #{name}
     * 示例sql3：insert into employee(name, address) values(#{name},  #{address})
     */
    String value();

    /**
     * true：参数以放置顺序来匹配sql中'?'的位置
     * false: 以参数的名称来替换sql中对应的参数名称（例如：以 name 将 #{name} 替换成 '?'）
     * 两者不可同时作用在一条sql上
     */
    boolean isOrder() default false;


}
