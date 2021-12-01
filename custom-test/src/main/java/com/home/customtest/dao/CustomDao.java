package com.home.customtest.dao;

import com.custom.annotations.mapper.Query;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:57
 * @Desc：
 **/
public interface CustomDao {


    @Query("select emp_name from employee where sex = #{sex} and ${name} = #{age}")
    String selectOneByCond(int sex,  int age, String name);

}
