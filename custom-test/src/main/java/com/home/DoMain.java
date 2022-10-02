package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultConditionWrapper;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.home.customtest.entity.Student;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();


        DefaultConditionWrapper<Student> conditionWrapper = Conditions.query(Student.class)
                .select("`name`")
                .eq("age", 20).like("name", "5");
        List<Student> studentList = jdbcDao.selectList(conditionWrapper);
        System.out.println("studentList = " + studentList);


    }


}
