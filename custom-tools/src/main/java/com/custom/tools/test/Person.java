package com.custom.tools.test;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Xiao-Bai
 * @date 2022/12/29 23:22
 * @desc
 */
@Getter
@Setter
public class Person {

    private String name;

    private String nickName;

    private Integer age;

    private Boolean sex;

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }
}
