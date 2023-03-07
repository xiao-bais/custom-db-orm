package com.home;

import com.custom.action.core.DoTargetExecutor;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.home.customtest.entity.Student;

/**
 * @author  Xiao-Bai
 * @since  2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();
        MyService helper = new MyServiceImpl();

        Student student = new Student();
        student.setId(12);

//        List<Student> students = jdbcDao.selectListBySql(Student.class, "select * from student where id = 1");
//
//        DbPageRows<Student> studentDbPageRows = jdbcDao.selectPage(Student.class, "and a.id =  ?", null, 1);
//
//        Map<String, String> stringMap = jdbcDao.selectMap(String.class, String.class, "select id, name from student where id = 1");
//
//        Employee employee = jdbcDao.selectOne(Employee.class, " and a.explain is null and a.dept_id = ? and a.age > ?", 1, 25);
//
//        Student[] selectArrays = jdbcDao.selectArrays(Student.class, "select * from student a where a.id in (?,?,?,?)", 1, 2, null, 4);

        Student one = jdbcDao.createChain(Student.class)
                .where(x -> x.eq("id", 1001))
                .getOne();

        System.out.println("one = " + one);


//        long count = helper.where(e -> e.eq("age", 27)).count();
//        System.out.println("count = " + count);


    }









}
