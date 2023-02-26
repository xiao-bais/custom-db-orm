package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.comm.enums.TableNameStrategy;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.StrUtils;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.home.customtest.entity.Student;
import com.home.customtest.entity.WorkEmp;
import lombok.val;

import java.util.Locale;

/**
 * @author  Xiao-Bai
 * @since  2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();
        MyService helper = new MyServiceImpl();

        Student student = jdbcDao.selectOne(Conditions.lambdaQuery(Student.class).lt(Student::getAge, 20));
        System.out.println("student = " + student);


//        long count = helper.where(e -> e.eq("age", 27)).count();
//        System.out.println("count = " + count);


    }









}
