package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.LambdaConditionWrapper;
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

        LambdaConditionWrapper<Student> conditionWrapper = Conditions.lambdaQuery(Student.class).ge(Student::getAge, 22)
                .eq(Student::getPassword, "123456")
                .like(Student::getNickName, "a")
                .lt(Student::getAge, 25);
        List<Student> studentList = jdbcOpDao.selectList(conditionWrapper);

        LambdaConditionWrapper<Student> conditionWrapper2 = Conditions.lambdaQuery(Student.class).ge(Student::getAge, 23)
                .eq(Student::getPassword, "666666")
                .like(Student::getNickName, "bb")
                .lt(Student::getAge, 24);
        List<Student> studentList2 = jdbcOpDao.selectList(conditionWrapper2);

    }

}
