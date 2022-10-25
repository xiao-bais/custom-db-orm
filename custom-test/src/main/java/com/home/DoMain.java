package com.home;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.custom.comm.utils.CustomUtil;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        Map<Integer, Object> resMap = new HashMap<>();
        resMap.put(88, 123);
        resMap.put(99, "zhangsan");
        resMap.put(111, new BigDecimal("99.89"));
        resMap.put(222, new Date());
        String jsonString = JSON.toJSONString(resMap);
        try {

            Map<Integer, Integer> toMap = CustomUtil.jsonParseToMap(jsonString, Integer.class, Integer.class);
            System.out.println("toMap = " + toMap);
        }catch (JSONException e) {
            if (e.getCause() instanceof NumberFormatException) {
                System.out.println("gbb");
            }
            System.out.println("bbb");
        }

        System.out.println("aaaa");


//        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
//        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
//        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();



//        HmErpOrderPO hmErpOrderPO1 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65521);
//        HmErpOrderPO hmErpOrderPO2 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65522);
//        HmErpOrderPO hmErpOrderPO3 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65523);
//
//        jdbcDao.selectList(Conditions.lambdaQuery(Student.class).eq(Student::getNickName, "aaaad"));
//
//        HmErpOrderPO hmErpOrderPO4 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65524);
//        HmErpOrderPO hmErpOrderPO5 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65525);
//        HmErpOrderPO hmErpOrderPO6 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65526);

//        System.out.printf("该订单【%s】, 套餐售价【%d】元", hmErpOrderPO1.getOrderNum(), hmErpOrderPO1.getActivAmount());


    }


}
