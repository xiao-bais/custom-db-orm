package com.home.customtest.dao;

import com.custom.annotations.loader.Query;
import com.custom.comm.BasicDao;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:57
 * @Desc：
 **/
public interface CustomDao  extends BasicDao{


    @Query("select emp_name from employee where sex = #{sex} and age = #{age}")
    String selectOneByCond(int sex, int age);

}
