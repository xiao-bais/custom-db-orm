package com.home;

import com.custom.action.sqlparser.JdbcDao;
import com.home.customtest.entity.Employee;
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

        Employee employee = new Employee();
        employee.setEmpName("周汉卿");
        employee.setAge(19);
        employee.setSex(false);
        employee.setAddress("湖南岳阳");
        jdbcDao.insert(employee);



    }

}
