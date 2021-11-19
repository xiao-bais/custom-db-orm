package com.custom.test;

import com.custom.annotations.reader.Query;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/19 17:27
 * @Desc：
 **/
public interface JdbcTest {

    @Query("select * from employee")
    List<Employee> getList();



}
