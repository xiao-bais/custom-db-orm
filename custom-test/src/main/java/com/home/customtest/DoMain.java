package com.home.customtest;

import com.custom.wrapper.ConditionEntity;
import com.home.customtest.entity.Employee;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
public class DoMain {


    public static void main(String[] args) throws Exception {

        ConditionEntity<Employee> conditionEntity = new ConditionEntity<>(Employee.class);
        conditionEntity.select("name", "age", "birth");

    }
}
