package com.home;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.custom.action.wrapper.ConditionWrapper;
import com.custom.action.wrapper.DefaultConditionWrapper;
import com.custom.action.wrapper.LambdaConditionWrapper;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Xiao-Bai
 * @Date 2022/5/9 17:04
 * @Desc：
 **/
public class Mains {

    private static Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]*");

    public static void main(String[] args) {

        String str = "1";

        boolean b = Boolean.parseBoolean(str);
        System.out.println("i = " + b);


    }



}
