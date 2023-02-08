package com.home;

import com.custom.action.service.DbServiceHelper;
import com.home.customtest.entity.Student;
import org.springframework.stereotype.Service;

/**
 * @author Xiao-Bai
 * @date 2023/2/8 18:02
 */
@Service
public class MyServiceImpl extends DbServiceHelper<Student> implements MyService {



}
