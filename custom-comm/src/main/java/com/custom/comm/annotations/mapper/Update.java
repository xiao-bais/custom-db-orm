package com.custom.comm.annotations.mapper;

import java.lang.annotation.*;

/**
 * @author  Xiao-Bai
 * @since 2021/11/19 16:52
 * @Desc：适用于dao层动态代理方法上使用
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Update {

    /**
     * <br/>示例sql1：update employee set name = #{name} where id = #{id}
     * <br/示例sql2：delete from employee where name = #{name}
     * <br/示例sql3：insert into employee(name, address) values(#{name},  #{address})
     */
    String value();

    /**
     * <li>true：参数以放置顺序来匹配sql中'?'的位置</li>
     * <li>false: 以参数的名称来替换sql中对应的参数名称(例如：以 name 将 #{name} 替换成 '?')</li>
     * <li>两者不可同时作用在一条sql上</li>
     */
    boolean order() default false;


}
