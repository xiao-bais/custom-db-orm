package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.sqlparser.DbJoinToOneParseModel;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.comm.CustomUtil;
import com.custom.comm.readwrite.ReadFieldHelper;
import com.home.customtest.dao.StudentDao;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Dept;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
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

        List<Student> studentList = jdbcOpDao.selectList(Conditions.lambdaQuery(Student.class).ge(Student::getAge, 22)

        );

        jdbcDao.selectList(Conditions.lambdaQuery(Employee.class).eq(Employee::getDeptId, 5).eq(Employee::getExplain, "aaaa"));

    }

}
