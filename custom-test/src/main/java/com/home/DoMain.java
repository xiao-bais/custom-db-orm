package com.home;

import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.wrapper.Conditions;
import com.home.customtest.entity.Student;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        JdbcDao jdbcDao = JdbcTestBuilder.builder().getJdbcDao();

        jdbcDao.selectList(Conditions.lambdaQuery(Student.class).eq(Student::getPassword, "11111"));

    }

}
