package com.home;

import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.action.sqlparser.JdbcDao;
import com.home.customtest.entity.Student;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

//        JdbcOpDao jdbcDao = JdbcTestBuilder.builder().getJdbcDao();
        JdbcDao jdbcDao = JdbcTestBuilder.builder().getJdbcDao2();

//        DbPageRows<Student> studentDbPageRows = jdbcDao2.selectPageRows(Student.class, "and a.sex = ?", new DbPageRows<>(1, 3), 1);

        Student childStudent = new Student();
        childStudent.setId(22);
        childStudent.setSex(false);
        childStudent.setName("张三");
        childStudent.setNickName("yiss");
        jdbcDao.updateColumnByKey(childStudent, op -> {
            op.add(Student::getName);
            op.add(Student::getSex);
            op.add(Student::getNickName);
        });

//        List<Student> studentList = jdbcDao.selectList(Conditions.lambdaQuery(Student.class));



        System.out.println("childStudent.getId() = " + childStudent.getId());


    }

}
