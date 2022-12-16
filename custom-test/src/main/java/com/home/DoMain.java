package com.home;

import com.custom.action.core.*;
import com.custom.comm.utils.CustomUtil;
import com.custom.jdbc.back.BackResult;
import com.home.customtest.entity.City;
import com.home.customtest.entity.Province;
import com.home.customtest.entity.TempOrderInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {


        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();

        String files = CustomUtil.loadFiles("/sql/orderInfo.sql");
        List<TempOrderInfo> tempOrderInfos = jdbcOpDao.selectListBySql(TempOrderInfo.class, files);

        List<City> cityList = jdbcDao.selectList(City.class, "");

        List<Province> provinces = jdbcDao.selectList(Province.class, "");

        List<String> list = Arrays.asList("北京", "上海", "天津", "重庆");
        for (City city : cityList) {
            String thisCity = city.getName().replace("市", "");
            long count = tempOrderInfos.stream().filter(op -> op.getAddress() != null && op.getAddress().contains(thisCity)).count();
            if (count != 0) {
                Optional<Province> first = provinces.stream().filter(x -> x.getId().equals(city.getProvinceId())).findFirst();
                if (first.isPresent()) {
                    String provinceName = first.get().getName();
                    if (list.contains(provinceName)) {
                        System.out.println(provinceName + "市-" + city.getName() + ", " + count);
                    }else {
                        System.out.println(provinceName +"省-"+ city.getName() + ", " + count);
                    }
                }
            }
        }


    }






}
