package com.home.customtest.dao;

import com.custom.annotations.mapper.Query;
import com.custom.annotations.mapper.SqlMapper;
import com.custom.annotations.mapper.SqlPath;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.WorkEmp;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:57
 * @Desc：
 **/
@SqlMapper
public interface CustomTestDao {


    @Query("select emp_name from employee where sex = #{sex} and ${name} = #{age}")
    String selectOneByCond(int sex,  int age, String name);

    @SqlPath(value = "/sql/selectOne.sql", isOrder = true)
    Employee selectByOne(int age);

    @Query(value = "select age from employee where age in (#{ages}) and emp_name = #{empName}",isOrder = true)
    List<Integer> getAges(int[] ages, String empName);

    @Query("select * from employee where age in (#{emp.ageList}) and emp_name = #{emp.eName}")
    List<Employee> getConditr(WorkEmp emp);

}
