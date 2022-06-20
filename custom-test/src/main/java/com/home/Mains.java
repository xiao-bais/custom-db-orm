package com.home;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.custom.action.wrapper.ConditionWrapper;
import com.custom.action.wrapper.DefaultConditionWrapper;
import com.custom.action.wrapper.LambdaConditionWrapper;
import com.home.customtest.entity.Student;

/**
 * @Author Xiao-Bai
 * @Date 2022/5/9 17:04
 * @Desc：
 **/
public class Mains {


    public static void main(String[] args) {

        DefaultConditionWrapper<Student> query = new DefaultConditionWrapper<>(Student.class);
            query.eq("a.name", "李佳航")
                 .between("age", 20, 25);


        System.out.println("query = " + query);
        ConditionWrapper<Student> wrapper = query;
        System.out.println("wrapper = " + wrapper);

        String jsonString = JSONObject.toJSONString(wrapper);
//        TypeReference<LambdaConditionWrapper<Student>> reference = new TypeReference<LambdaConditionWrapper<Student>>(Student.class){};
        ConditionWrapper<Student> lambda = JSONObject.parseObject(jsonString, ConditionWrapper.class);


//        lambda.gt(Student::getMoney, 4500.5);

        System.out.println("lambdaConditionWrapper = " + lambda);


    }




}
