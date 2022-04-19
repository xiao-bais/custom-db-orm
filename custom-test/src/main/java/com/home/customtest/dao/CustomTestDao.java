package com.home.customtest.dao;

import com.custom.comm.annotations.mapper.Query;
import com.custom.comm.annotations.mapper.SqlMapper;
import com.custom.comm.annotations.mapper.SqlPath;
import com.custom.comm.annotations.mapper.Update;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.WorkEmp;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:57
 * @Desc：
 **/
@SqlMapper
public interface CustomTestDao {


    @Query("select emp_name from employee where sex = #{sex} and ${name} = #{age}")
    String selectOneByCond(int sex,  int age, String name);

    @SqlPath(value = "/sql/selectOne.sql")
    Employee selectByOne(int age);

    @Query(value = "select age from employee where age in (#{ages}) and emp_name = #{empName}",isOrder = true)
    List<Integer> getAges(int[] ages, String empName);

    @Query("select emp_name empName from employee where age in (#{emp.ageList}) and emp_name = #{emp.empName}")
    List<Employee> getConditr(WorkEmp emp);

    @Query("select * from employee where 1=1 and address like concat('%', #{searchMap.name}, '%')")
    Employee getEmpInfoByMap(Map<String, String> searchMap);

    @Query("select * from employee where age in (#{opSet})")
    Employee getEmpInfoBySet(Set<Integer> opSet);

    @Query("select age from employee where age in (#{arr})")
    Integer[] getEmpInfoByArray(int[] arr);

    @Query(value = "select * from employee where age = ? and address = ?",isOrder = true)
    Map<String, Object> getEmpInfoByMap(int age, String addr);

    @Update("update employee set age = #{age}, address = #{address}, dept_id = #{dept} where emp_name = #{name}")
    int updateEmp(int age, String name, String address, int dept);

    @Update(value = "insert into employee(emp_name,address,age) values(?,?,?)", isOrder = true)
    int saveEmp(String name, String addr, int age);

    @Update(value = "insert into employee(emp_name,address,age) values(#{name}, #{address}, #{age})", isOrder = true)
    int saveEmp2(String name, String addr, int age);

}
