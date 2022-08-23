package com.home;

import com.custom.action.sqlparser.DbJoinToOneParseModel;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.comm.CustomUtil;
import com.home.customtest.dao.StudentDao;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Dept;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.lang.reflect.Field;
import java.util.function.Predicate;

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

        Dept student = jdbcDao.selectByKey(Dept.class, 1);

        System.out.println("student = " + student);

    }

}
