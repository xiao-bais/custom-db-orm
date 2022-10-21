package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.jdbc.back.BackResult;
import com.home.customtest.entity.Employee;
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

        List<Student> students = jdbcOpDao.selectList(Conditions.lambdaQuery(Student.class)
                .between(Student::getId, 88, 89)
        );

        BackResult.execCall(op -> {
            Employee employee = jdbcDao.selectByKey(Employee.class, "94b7dbaee3c448c29a95dd4618249d45");
            employee.setEmpName("1231");
            jdbcOpDao.updateByKey(employee);

            employee.setExplain("213332");
            jdbcOpDao.updateByKey(employee);
            int a = 1 / 0;
        });



    }


}
