package com.custom.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/27 14:07
 * @Desc： 对时间或日期进行增加、减少、比较的操作类
 **/
public class DateTimeOperator {


    /**
    * 指定添加或减少多少时间或日期
    */
    public static String handleTimes(String time, String format, int num, DateTimeEnum unit, String returnFormat) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(unit.getField(), num);
        return DateTimeUtils.getFormatByDate(calendar.getTime(), returnFormat);
    }



}
