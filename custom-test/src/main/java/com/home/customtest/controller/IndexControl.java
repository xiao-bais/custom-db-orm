package com.home.customtest.controller;

import com.custom.comm.BackResult;
import com.custom.handler.JdbcDao;
import com.custom.sqlparser.CustomDao;
import com.custom.wrapper.ConditionEntity;
import com.custom.wrapper.LambdaConditionEntity;
import com.home.customtest.entity.Aklis;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private CustomDao customDao;


    @GetMapping("/getMain")
    public BackResult<List<Student>> getIndex(String key) throws Exception {
        List<Student> students = customDao.selectList(Student.class, new LambdaConditionEntity<>(Student.class)
                .ge(Student::getAge, 22).like(Student::getAddress, "山东")
                .between(Student::getAge, 21, 25)
                .select(Student::getName, Student::getProvince, Student::getCity, Student::getArea)
                .or(x -> x.select(Student::getAge)
                        .exists("select 1 from student2 stu2 where stu2.id = a.id and stu2.password = '12345678zcy'")
                        .orderByAsc(Student::getId)
                        .orderByDesc(Student::getAge)
                ));
        return BackResult.bySuccess("success01", students);
    }

    @PostMapping("saveInfo")
    public BackResult<Aklis> saveInfo(@RequestBody Map<String, Object> map) throws Exception {

        Aklis aklis = new Aklis();
        aklis.setName(map.get("name").toString());
        aklis.setAge(22);
        customDao.insert(aklis);

        aklis.setAddress("河南洛阳");
        customDao.updateByKey(aklis);

        return BackResult.bySuccess(aklis);
    }

}
