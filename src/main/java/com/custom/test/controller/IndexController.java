package com.custom.test.controller;

import com.alibaba.fastjson.JSONArray;
import com.custom.comm.BackResult;
import com.custom.handler.JdbcDao;
import com.custom.test.Employee;
import com.custom.test.JdbcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/24 17:05
 * @Desc：
 **/
@RestController
@RequestMapping("aaa")
public class IndexController {

    @Autowired
    private JdbcTest jdbcTest;
    @Autowired
    private JdbcDao jdbcDao;



    @GetMapping("/getIndex")
    public BackResult<String> getIndex() throws Exception {
        Integer[] arrays = jdbcTest.getArrays(25, true);
//        String jsonString = JSONArray.toJSONString(arrays);
        List<Employee> employeeList = jdbcDao.selectList(Employee.class, " and a.age = ?", arrays[0]);
        return BackResult.bySuccess("成功11", JSONArray.toJSONString(employeeList));
    }


}
