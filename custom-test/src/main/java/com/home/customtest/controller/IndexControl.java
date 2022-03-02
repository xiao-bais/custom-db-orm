package com.home.customtest.controller;

import com.custom.comm.BackResult;
import com.custom.handler.JdbcDao;
import com.custom.sqlparser.CustomDao;
import com.custom.wrapper.ConditionEntity;
import com.home.customtest.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
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
    public BackResult<List<Employee>> getIndex(String key) throws Exception {
        ConditionEntity<Employee> conditionEntity = new ConditionEntity<>(Employee.class);
        conditionEntity.like("emp_name", "工")
                .eq("sex", true)
                .in("age", Stream.of(20,23,26).collect(Collectors.toList()))
                .and(new ConditionEntity<>(Employee.class).like("dept.name", "财务"));
        List<Employee> employees = customDao.selectList(Employee.class, null);
        return BackResult.bySuccess("success01", employees);
    }

}
