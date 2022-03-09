package com.home.customtest.controller;

import com.custom.comm.BackResult;
import com.custom.handler.JdbcDao;
import com.custom.sqlparser.CustomDao;
import com.custom.wrapper.ConditionEntity;
import com.custom.wrapper.LambdaConditionEntity;
import com.home.customtest.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private CustomDao customDao;


    @GetMapping("/getMain")
    public BackResult<Employee> getIndex(String key) throws Exception {
        ConditionEntity<Employee> conditionEntity = new ConditionEntity<>(Employee.class);
        conditionEntity.like("emp_name", "工")
                .eq("sex", true)
                .in("age", Stream.of(20,23,26).collect(Collectors.toList()))
                .and(new ConditionEntity<>(Employee.class).like("dept.name", "财务"));
        Employee employee = customDao.selectOne(new LambdaConditionEntity<>(Employee.class).like(Employee::getEmpName, "沾上干"));
        return BackResult.bySuccess("success01", employee);
    }

}
