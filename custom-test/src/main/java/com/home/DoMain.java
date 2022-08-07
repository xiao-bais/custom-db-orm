package com.home;

import com.custom.action.condition.*;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.home.customtest.entity.ChildStudent;

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

//        jdbcDao.selectList(Conditions.lambdaQuery(Student.class).eq(Student::getPassword, ""));

//        jdbcDao.selectList(Student.class, "and a.name = ? ", "张三");

//        List<Object> objects = jdbcDao.selectObjs(Conditions.lambdaQuery(Student.class).onlyPrimary().between(Student::getMoney, 4000, 6000).select(Student::getName));
//        System.out.println("objects = " + objects);

//        ChildStudent student = jdbcDao.selectByKey(ChildStudent.class, 1);

//        List<ChildStudent> childStudents = jdbcDao.selectList(Conditions.lambdaQuery(ChildStudent.class)
//                .select(ChildStudent::getSex)
//                .select(x -> x.count(ChildStudent::getSex, ChildStudent::getCountAge))
//                .groupBy(ChildStudent::getSex)
//        );

        jdbcOpDao.updateSelective(Conditions.lambdaUpdate(ChildStudent.class)
                .setter(x -> x.set(ChildStudent::getPhone, "158xxxxxxxx"))
                .where(x -> x.eq(ChildStudent::getName, "张三")
                        .or(p -> p.ge(ChildStudent::getAge, 22).between(ChildStudent::getMoney, 4000, 5000))
                )
        );


//        System.out.println("mapList = " + childStudents);

    }

}
