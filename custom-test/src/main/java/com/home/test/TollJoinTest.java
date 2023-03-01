package com.home.test;

import com.custom.tools.data.DataJoining;
import com.custom.tools.data.DataSumming;
import com.custom.tools.objects.ObjBuilder;
import com.custom.tools.testmodel.Person;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiao-Bai
 * @since 2023/2/28 12:25
 */
public class TollJoinTest {

    public static void main(String[] args) throws IntrospectionException {

        Person person1 = ObjBuilder.of(Person::new)
                .with(Person::setId, 1)
                .with(Person::setName, "张晓峰1")
                .build();
        List<Person> list1 = new ArrayList<>();
        list1.add(person1);

        Person person2 = ObjBuilder.of(Person::new)
                .with(Person::setId, 1)
                .with(Person::setAge, 20)
                .with(Person::setParentId, 3)
                .build();
        List<Person> list2 = new ArrayList<>();
        list2.add(person2);

        Person person3 = ObjBuilder.of(Person::new)
                .with(Person::setId, 1)
                .with(Person::setNickName, "疯子")
                .build();
        List<Person> list3 = new ArrayList<>();
        list3.add(person3);


        DataJoining<Person> dataJoining = new DataJoining<>(Person.class, list1, list2, (o1, o2) -> o1.getId().equals(o2.getId()));

        dataJoining.joinStart(Person::getAge, Person::getParentId);
        dataJoining.setOtherList(list3);
        dataJoining.joinStart(Person::getNickName);

        System.out.println("list1 = " + list1);

    }
}
