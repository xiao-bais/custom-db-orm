package com.home.customtest.demo;

import java.lang.annotation.*;

/**
 * @author  Xiao-Bai
 * @since  2021/12/24 11:10
 * @Desc：
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApiTest {

    String value();

    int age();

    String name();
}
