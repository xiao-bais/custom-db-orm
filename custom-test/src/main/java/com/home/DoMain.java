package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultUpdateSetSqlSetter;
import com.custom.action.condition.LambdaUpdateSetSqlSetter;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.jdbc.interfaces.TransactionExecutor;
import com.custom.proxy.InterfacesProxyExecutor;
import com.custom.tools.objects.ObjBuilder;
import com.home.customtest.dao.StudentDao;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.math.BigDecimal;
import java.util.Arrays;
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


        Student student = ObjBuilder.of(Student::new)
                .with(Student::setName, "天命之子")
                .with(Student::setAge, 18)
                .with(Student::setPhone, "18878762351")
                .build();
        List<Student> students = jdbcDao.selectList(student);


    }








}
