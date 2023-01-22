package com.home.customtest.dao;

import com.custom.comm.annotations.mapper.Query;
import com.custom.comm.annotations.mapper.SqlMapper;
import com.home.customtest.entity.Student;

import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/13 3:01
 * @Desc
 */
@SqlMapper
public interface StudentDao {

    @Query(value = "select * from employee where age = #{age} and address = #{addr}")
    Map<String, Object> getEmpInfoByMap(int age, String addr);

    @Query("select * from student where age > #{age}")
    List<Student> getStudentInfoByAge(Integer age);

}
