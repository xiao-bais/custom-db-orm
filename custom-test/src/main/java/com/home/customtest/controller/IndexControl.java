package com.home.customtest.controller;

import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.wrapper.Conditions;
import com.custom.action.wrapper.LambdaConditionEntity;
import com.custom.comm.BackResult;
import com.home.customtest.entity.Aklis;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("saveInfo")
    public BackResult<List<Aklis>> saveInfo(@RequestBody Map<String, Object> map) throws Exception {

        List<Aklis> aklisList = new ArrayList<>();

        Aklis aklis = new Aklis();
        aklis.setName(map.get("name").toString());
        aklis.setAge(25);


        Aklis aklis2 = new Aklis();
        aklis2.setName(map.get("name").toString());
        aklis2.setAge(28);

        aklisList.add(aklis);
        aklisList.add(aklis2);

        jdbcDao.insert(aklisList);

        return BackResult.bySuccess(aklisList);
    }

}
