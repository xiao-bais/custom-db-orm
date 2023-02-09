package com.custom.tools;

import com.custom.tools.objects.ObjBuilder;
import com.custom.tools.test.Person;

/**
 * @author  Xiao-Bai
 * @since  2022/11/23 0:28
 * 工具类的一些测试类
 */
public class MyToolStart {

    public static void main(String[] args) throws Exception {

        Person person = ObjBuilder.of(Person::new)
                .with(Person::setName, "张晓峰")
                .with(Person::setNickName, "疯子")
                .with(Person::setAge, 20)
                .build();

        System.out.println("person = " + person);


    }
}
