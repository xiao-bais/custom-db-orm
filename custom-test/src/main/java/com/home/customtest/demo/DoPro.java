package com.home.customtest.demo;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/24 11:09
 * @Desc：
 **/
@ApiTest(value = "张三", age = 18, name = "张三哥哥")
public class DoPro {

    public static void main(String[] args) {
        Class<DoPro> doProClass = DoPro.class;
        ApiTest annotation = doProClass.getAnnotation(ApiTest.class);
        System.out.println("annotation.age() = " + annotation.age());
        System.out.println("annotation.value() = " + annotation.value());
        System.out.println("annotation.name() = " + annotation.name());
    }


}
