package com.home.customtest.controller;

import com.custom.comm.BackResult;
import com.custom.handler.JdbcDao;
import com.home.customtest.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/27 15:32
 * @Desc：
 **/
@RestController
@RequestMapping("/one")
public class IndexControl {


    @Autowired
    private JdbcDao jdbcDao;


    @GetMapping("/getMain")
    public BackResult<String> getIndex() throws Exception {
        List<Employee> employees = jdbcDao.selectList(Employee.class, null);
        return BackResult.bySuccess("success01", employees.toString());
    }

}
