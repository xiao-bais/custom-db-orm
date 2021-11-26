package com.custom.controller;

import com.custom.comm.BackResult;
import com.custom.test.Employee;
import com.custom.handler.JdbcDao;
import com.custom.test.JdbcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2021/11/26 22:46
 * @desc:
 */
@RestController
@RequestMapping("/getMain")
public class TestController {

    @Autowired
    private JdbcDao jdbcDao;
    @Autowired
    private JdbcTest jdbcTest;


    @GetMapping("/getIndex")
    public BackResult<List<Employee>> getTest01() throws Exception {
        List<Employee> employees = jdbcDao.selectList(Employee.class, null);
        return BackResult.bySuccess(employees);
    }


}
