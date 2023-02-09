package com.home.customtest.controller;

import com.custom.action.core.JdbcOpDao;
import com.custom.comm.utils.back.BackResult;
import com.home.customtest.entity.Province;
import com.home.customtest.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author  Xiao-Bai
 * @since  2021/11/27 15:32
 * @Desc：
 **/
@SuppressWarnings("all")
@RestController
@RequestMapping("/one")
public class IndexControl {

    @Autowired
    private JdbcOpDao jdbcDao;

    @Autowired
    private StudentMapper studentMapper;

//    @Resource
//    private CustomTestDao customTestDao;


//    @GetMapping("/getMain")
//    public BackResult<List<ChildStudent>> getIndex(String key) throws Exception {
//        List<ChildStudent> students = jdbcDao.selectList(Conditions.lambdaQuery(ChildStudent.class)
//                .ge(ChildStudent::getAge, 22).like(Student::getAddress, "山东")
//                .between(ChildStudent::getAge, 21, 25)
//                .select(ChildStudent::getName, Student::getProvince, Student::getCity, Student::getArea)
//                .or(x -> x.select(ChildStudent::getAge)
//                        .exists("select 1 from student stu2 where stu2.id = a.id and stu2.password = '12345678zcy'")
//                        .orderByAsc(ChildStudent::getId)
//                        .orderByDesc(ChildStudent::getAge)
//                        .toDefault().toLambda()
//                ));
//        return BackResult.bySuccess("success01", students);
//    }

//    @GetMapping("/search")
//    public BackResult<Student> getKeyInfo(String key) throws Exception {
//        long l = System.currentTimeMillis();
//        Student student = customTestDao.selectByOne(key);
//        long l1 = System.currentTimeMillis();
//        System.out.println("l1 = " + (l1 - l));
//        return BackResult.bySuccess(student);
//    }


    @GetMapping("/comTime")
    public BackResult<List<Province>> getComTime() throws Exception {

        List<Province> provinces = jdbcDao.selectList(new Province());
        long t1 = System.currentTimeMillis();
        List<Province> provinces2 = jdbcDao.selectList(new Province());
        long t2 = System.currentTimeMillis();

        List<Province> provinceList = studentMapper.getProvinces();
        long t3 = System.currentTimeMillis();
        System.out.println("t2 = " + (t2 - t1));
        System.out.println("t3 = " + (t3 - t2));

        return BackResult.bySuccess(provinces);

    }


}
