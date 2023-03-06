package com.home;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.custom.action.condition.Conditions;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.comm.enums.TableNameStrategy;
import com.custom.comm.page.DbPageRows;
import com.custom.comm.utils.Constants;
import com.custom.comm.utils.StrUtils;
import com.custom.jdbc.configuration.DbGlobalConfig;
import com.custom.tools.function.Joining;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;
import com.home.customtest.entity.WorkEmp;
import lombok.val;

import java.math.BigDecimal;
import java.util.*;

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

        Student student = new Student();
        student.setId(12);

//        List<Student> students = jdbcDao.selectListBySql(Student.class, "select * from student where id = 1");
//
//        DbPageRows<Student> studentDbPageRows = jdbcDao.selectPage(Student.class, "and a.id =  ?", null, 1);
//
//        Map<String, String> stringMap = jdbcDao.selectMap(String.class, String.class, "select id, name from student where id = 1");
//
//        Employee employee = jdbcDao.selectOne(Employee.class, " and a.explain is null and a.dept_id = ? and a.age > ?", 1, 25);

        Student[] selectArrays = jdbcDao.selectArrays(Student.class, "select * from student a where a.id in (?,?,?,?)", 1, 2, null, 4);

        System.out.println("1 = " + 1);


//        long count = helper.where(e -> e.eq("age", 27)).count();
//        System.out.println("count = " + count);


    }









}
