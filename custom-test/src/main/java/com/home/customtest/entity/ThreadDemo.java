package com.home.customtest.entity;

import com.custom.action.core.JdbcDao;
import com.custom.comm.utils.back.BackResult;

/**
 * @author Xiao-Bai
 * @date 2022/10/13 0:13
 * @desc
 */
public class ThreadDemo implements Runnable {

    private JdbcDao jdbcDao;
    private int i;

    public ThreadDemo(JdbcDao jdbcDao, int i) {
        this.jdbcDao = jdbcDao;
        this.i = i;
    }

    @Override
    public void run() {
        if (i == 1) {
            BackResult<Object> objectBackResult = BackResult.execCall(x -> {
                Student student = jdbcDao.selectByKey(Student.class, 11);
                Thread.sleep(2000);
                Student student1 = jdbcDao.selectByKey(Student.class, 13);
            });
        } else {
            Student student = jdbcDao.selectByKey(Student.class, 14);
            Student student2 = jdbcDao.selectByKey(Student.class, 15);
            Student student3 = jdbcDao.selectByKey(Student.class, 16);
            Student student4 = jdbcDao.selectByKey(Student.class, 17);
            Student student5 = jdbcDao.selectByKey(Student.class, 18);
        }



    }
}
