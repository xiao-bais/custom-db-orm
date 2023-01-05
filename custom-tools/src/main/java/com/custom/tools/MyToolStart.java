package com.custom.tools;

import com.custom.tools.data.DataJoining;
import com.custom.tools.test.Person;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Xiao-Bai
 * @date 2022/11/23 0:28
 * 工具类的一些测试类
 */
public class MyToolStart {

    public static void main(String[] args) throws Exception {

        int[] arr = {15,7,10,2,5,9,3,10,11};

        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }

        System.out.println("arr = " + Arrays.toString(arr));


//        List<Person> list = new ArrayList<>();
//        Person p1 = new Person();
//        p1.setName("张三");
//        p1.setAge(18);
//        list.add(p1);
//
//
//        List<Person> list2 = new ArrayList<>();
//        Person p2 = new Person();
//        p2.setName("张三");
//        p2.setSex(false);
//        list2.add(p2);
//
//        Person p3 = new Person();
//        p3.setName("李四");
//        p3.setAge(20);
//        list2.add(p3);
//
//
//        DataJoining<Person> dataJoining = new DataJoining<>(Person.class, list, list2, o1 -> o2 -> o1.getName().equals(o2.getName()));
//
//
//        dataJoining.joinStart(Person::getAge, Person::getSex);


    }
}
