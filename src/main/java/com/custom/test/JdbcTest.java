package com.custom.test;

import com.custom.annotations.reader.Query;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 17:27
 * @Desc：
 **/
public interface JdbcTest {

    @Query(value = "select * from employee", isPath = false)
    List<Employee> getList();

    @Query("select * from employee where id = 5")
    Employee getById();

    @Query("select * from employee where id = 5")
    Map<String, Object> getMap();

    @Query("select emp_name from employee")
    Set<Integer> getSets();

    @Query("select emp_name from employee where age = 25")
    String testHel(String name);

    @Query(value = "select age from employee", isPath = false)
    int[] getArrays();



}
