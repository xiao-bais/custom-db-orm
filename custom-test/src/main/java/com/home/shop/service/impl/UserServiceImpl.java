package com.home.shop.service.impl;

import com.custom.action.core.JdbcDao;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.home.shop.service.UserService;

/**
* @author  Xiao-Bai
* @since  2022-12-16 15:11
*/

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JdbcDao jdbcDao;




}