package com.home.customtest.demo;

import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/24 11:09
 * @Desc：
 **/
public class DoPro<T> extends Employee<T> {

    private T entity;

    private String name;

    public static void main(String[] args) {

        DoPro<Student> doPro = new DoPro<>();
        Type[] actualTypeArguments = ((ParameterizedType) doPro.getClass().getGenericSuperclass()).getActualTypeArguments();
        System.out.println("actualTypeArguments = " + Arrays.toString(actualTypeArguments));


    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
