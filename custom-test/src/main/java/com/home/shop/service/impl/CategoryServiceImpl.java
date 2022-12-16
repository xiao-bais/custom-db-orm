package com.home.shop.service.impl;

import com.custom.action.core.JdbcDao;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.home.shop.service.CategoryService;

/**
* @Author Xiao-Bai
* @Date 2022-12-16 15:11
*/

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private JdbcDao jdbcDao;




}