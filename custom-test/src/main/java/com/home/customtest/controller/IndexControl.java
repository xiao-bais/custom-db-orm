package com.home.customtest.controller;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.wrapper.Conditions;
import com.custom.comm.BackResult;
import com.home.customtest.dao.CustomTestDao;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/27 15:32
 * @Desc：
 **/
@SuppressWarnings("all")
@RestController
@RequestMapping("/one")
public class IndexControl {

    @Autowired
    private JdbcDao jdbcDao;

    @Resource
    private CustomTestDao customTestDao;


    @GetMapping("/getMain")
    public BackResult<List<ChildStudent>> getIndex(String key) throws Exception {
        List<ChildStudent> students = jdbcDao.selectList(Conditions.lambdaQuery(ChildStudent.class)
                .ge(ChildStudent::getAge, 22).like(Student::getAddress, "山东")
                .between(ChildStudent::getAge, 21, 25)
                .select(ChildStudent::getName, Student::getProvince, Student::getCity, Student::getArea)
                .or(x -> x.select(ChildStudent::getAge)
                        .exists("select 1 from student stu2 where stu2.id = a.id and stu2.password = '12345678zcy'")
                        .orderByAsc(ChildStudent::getId)
                        .orderByDesc(ChildStudent::getAge)
                ));
        return BackResult.bySuccess("success01", students);
    }

    @GetMapping("search")
    public BackResult<Student> getKeyInfo(String key) throws Exception {
        long l = System.currentTimeMillis();
        Student student = customTestDao.selectByOne(key);
        long l1 = System.currentTimeMillis();
        System.out.println("l1 = " + (l1 - l));
        return BackResult.bySuccess(student);
    }
}
