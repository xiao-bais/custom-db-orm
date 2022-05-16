package com.home;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.home.customtest.entity.Student;

/**
 * @Author Xiao-Bai
 * @Date 2022/5/9 17:04
 * @Desc：
 **/
public class Mains {


    public static void main(String[] args) {

        Student student = new Student();
        student.setName("张三");
        student.setAge(22);

        String jsonString = JSONObject.toJSONString(student, SerializerFeature.WriteNullStringAsEmpty);
        System.out.println("adada = " + jsonString);

    }




}
