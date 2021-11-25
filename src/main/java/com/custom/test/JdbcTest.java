package com.custom.test;

import com.custom.annotations.loader.Query;
import com.custom.annotations.loader.Update;
import com.custom.comm.BasicDao;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 17:27
 * @Desc：
 **/
public interface JdbcTest extends BasicDao {

    @Query(value = "select * from employee")
    List<Employee> getList();

    @Query("select * from employee where id = 5")
    Employee getById();

    @Query("select * from employee where id = 5")
    Map<String, Object> getMap();

    @Query("select emp_name from employee")
    Set<Integer> getSets();

    @Query("select emp_name from employee where age = 25")
    String testHel(String name);

    @Query(value = "select age from employee where age = #{myAge} and sex = #{mySex} ")
    Integer[] getArrays(int myAge, boolean mySex);

    @Update(value = "update employee set emp_name = #{name} where id = #{id}")
    int updateById(String name, int id);



}
