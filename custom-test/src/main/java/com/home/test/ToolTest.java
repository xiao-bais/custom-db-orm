package com.home.test;

import com.custom.tools.objects.ObjBuilder;
import com.custom.tools.testmodel.Person;
import com.custom.tools.tree.CmTreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  Xiao-Bai
 * @since  2022/11/23 0:28
 * 工具类的一些测试类
 */
public class ToolTest {

    public static void main(String[] args) {

        Person person1 = ObjBuilder.of(Person::new)
                .with(Person::setId, 1)
                .with(Person::setName, "张晓峰1")
                .with(Person::setNickName, "疯子")
                .with(Person::setAge, 20)
                .with(Person::setParentId, 0)
                .build();

        Person person2 = ObjBuilder.of(Person::new)
                .with(Person::setId, 2)
                .with(Person::setName, "张晓峰2")
                .with(Person::setNickName, "疯子")
                .with(Person::setAge, 20)
                .with(Person::setParentId, 0)
                .build();

        Person person3 = ObjBuilder.of(Person::new)
                .with(Person::setId, 3)
                .with(Person::setName, "张晓峰3")
                .with(Person::setNickName, "疯子")
                .with(Person::setAge, 20)
                .with(Person::setParentId, 1)
                .build();

        Person person4 = ObjBuilder.of(Person::new)
                .with(Person::setId, 4)
                .with(Person::setName, "张晓峰")
                .with(Person::setNickName, "疯子")
                .with(Person::setAge, 20)
                .with(Person::setParentId, 2)
                .build();

        Person person5 = ObjBuilder.of(Person::new)
                .with(Person::setId, 5)
                .with(Person::setName, "张晓峰")
                .with(Person::setNickName, "疯子")
                .with(Person::setAge, 20)
                .with(Person::setParentId, 3)
                .build();


        ArrayList<Person> list = new ArrayList<>();
        list.add(person1);
        list.add(person2);
        list.add(person3);
        list.add(person4);
        list.add(person5);

        // 获得递归后的最顶级父节点列表
        List<Person> personList = CmTreeNode.of(list)
                // 设置最顶级节点的实例
                .top(Person::new)// 或.top(Person::new)
                // 设置顶级父节点列表的查找条件
                .topListCond(e -> e.getParentId().equals(0))
                // 设置向下查找子节点时的条件 o1-parent o2-child
                .childCond(o1 -> o2 -> o1.getId().equals(o2.getParentId()))
                // 设置父节点的set方法
                .childrenSet(Person::setPersonList)
                // 开始递归计算
                .buildTrees();

        System.out.println("personList = " + personList);


    }
}
