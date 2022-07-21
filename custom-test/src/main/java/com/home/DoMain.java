package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.condition.Conditions;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Student;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        JdbcDao jdbcDao = JdbcTestBuilder.builder().getJdbcDao();

//        jdbcDao.selectList(Conditions.lambdaQuery(Student.class).eq(Student::getPassword, ""));

//        jdbcDao.selectList(Student.class, "and a.name = ? ", "张三");

//        List<Object> objects = jdbcDao.selectObjs(Conditions.lambdaQuery(Student.class).onlyPrimary().between(Student::getMoney, 4000, 6000).select(Student::getName));
//        System.out.println("objects = " + objects);

        ChildStudent student = jdbcDao.selectByKey(ChildStudent.class, 1);

        Student stu = new Student();
        stu.setSex(false);
        stu.setAge(22);
        stu.setName("");
        List<Student> studentList = jdbcDao.selectList(Conditions.allEqQuery(Student.class, stu));


    }

}
