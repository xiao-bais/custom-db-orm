package com.custom.comm.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/15
 * @Description 时间跟日期处理工具类
 */
public class DateTimeUtils {

    public static final String yyyy = "yyyy";
    public static final String yyyyMM_ = "yyyy-MM";
    public static final String yyyyMMdd_ = "yyyy-MM-dd";
    public static final String yyyyMMddHHmm_ = "yyyy-MM-dd HH:mm";
    public static final String yyyyMMdd_HHms_ = "yyyy-MM-dd HH:mm:ss";

    private static final int oneDay_1 = 86400;
    private static final int oneDay_2 = 86399;


    /**
     * 根据格式化日期 返回Date类型
     */
    public static Date getDateByFormatDate(String format, String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(date);
    }

    public static Date getDateByFormatDate(String date) throws ParseException {
        return getDateByFormatDate(yyyyMMdd_, date);
    }

    /**
     * 根据时间戳，返回Date类型
     */
    public static Date getDateByTimeStamp(int times) {
        return new Date(times * 1000L);
    }

    /**
     * 根据Date类型，返回格式化日期
     */
    public static String getFormatByDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 根据Date类型，返回格式化日期
     */
    public static String getFormatByDate(Date date) {
        return getFormatByDate(date, yyyyMMdd_);
    }

    /**
     * 根据时间戳，返回格式化日期
     */
    public static String getFormatByTimeStamp(int times, String format) {
        Date date = getDateByTimeStamp(times);
        return getFormatByDate(date, format);
    }

    public static String getFormatByTimeStamp(int times) {
        if (times == 0) return null;
        return getFormatByTimeStamp(times, yyyyMMdd_);
    }

    public static int getTimeStampByFormatDate(String format, String date) throws ParseException {
        Date thisDate = getDateByFormatDate(format, date);
        return (int) (thisDate.getTime() / 1000L);
    }

    public static int getTimeStampByFormatDate(String date) throws ParseException {
        return getTimeStampByFormatDate(yyyyMMdd_, date);
    }

    public static String getThisYear() {
        return getFormatByDate(new Date(), yyyy);
    }

    public static String getThisMonth(String format) {
        return getFormatByDate(new Date(), format);
    }

    public static String getThisMonth() {
        return getFormatByDate(new Date(), yyyyMM_);
    }

    public static String getThisDay(String format) {
        return getFormatByDate(new Date(), format);
    }

    public static String getThisDay() {
        return getThisDay(yyyyMMdd_);
    }

    public static int getThisTime() {
        return (int) (new Date().getTime() / 1000L);
    }

    /**
     * 获取今日开始时间戳
     */
    public static int getThisDayStartTimestamp() throws ParseException {
        return getTimeStampByFormatDate(getThisDay());
    }

    /**
     * 获取今日结束时间戳
     */
    public static int getThisDayEndTimestamp() throws ParseException {
        return getThisDayStartTimestamp() + oneDay_2;
    }

    /**
     * 获取某个月的开始时间戳1
     */
    public static int getMonthStartTimeStamp(String month, String format) throws ParseException {
        Date thisMonthDate = getDateByFormatDate(format, month);
       return (int) (thisMonthDate.getTime() / 1000L);
    }

    /**
     * 获取某个月的开始时间戳2
     */
    public static int getMonthStartTimeStamp(String month) throws ParseException {
        return getMonthStartTimeStamp(month, yyyyMM_);
    }

    /**
     * 获取某个月的结束时间戳1
     */
    public static int getMonthEndTimeStamp(String month, String format) throws ParseException {
        Date thisMonthDate = getDateByFormatDate(format, month);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(thisMonthDate);
        calendar.add(Calendar.MONTH, 1);
        Date endMonthDate = calendar.getTime();
        return (int) (endMonthDate.getTime() / 1000L) - 1;
    }

    /**
     * 获取某个月的结束时间戳1
     */
    public static int getMonthEndTimeStamp(String month) throws ParseException {
        return getMonthEndTimeStamp(month, yyyyMM_);
    }

    /**
     * 获取某个月的开始日期1
     */
    public static String getMonthStartDay(String month, String format, String returnFormat) throws ParseException {
        Date thisMonthStartDay = getDateByFormatDate(format, month);
        return getFormatByDate(thisMonthStartDay, returnFormat);
    }

    /**
     * 获取某个月的开始日期2
     */
    public static String getMonthStartDay(String month, String format) throws ParseException {
        Date thisMonthStartDay = getDateByFormatDate(format, month);
        return getFormatByDate(thisMonthStartDay, yyyyMMdd_);
    }

    /**
     * 获取某个月的结束日期1
     */
    public static String getMonthEndDay(String month, String format, String returnFormat) throws ParseException {
        Date thisMonthDate = getDateByFormatDate(format, month);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(thisMonthDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        Date endMonthEndDay = calendar.getTime();
        return getFormatByDate(endMonthEndDay, returnFormat);
    }

    /**
     * 获取某个月的结束日期2
     */
    public static String getMonthEndDay(String month, String format) throws ParseException {
        return getMonthEndDay(month, format, yyyyMMdd_);
    }

    /**
     * 获取指定月份上月开始时间戳1
     */
    public static int getLastMonthStartTimestamp(String month, String format) throws ParseException{
        Date thisMonthDate = getDateByFormatDate(format, month);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(thisMonthDate);
        calendar.add(Calendar.MONTH, -1);
        Date lastMonthStartDate = calendar.getTime();
        return (int) (lastMonthStartDate.getTime() / 1000);
    }

    /**
     * 获取指定月份上月开始时间戳2
     */
    public static int getLastMonthStartTimestamp(String month) throws ParseException{
        return getLastMonthStartTimestamp(month, yyyyMM_);
    }

    /**
     * 获取指定月份上月结束时间戳1
     */
    public static int getLastMonthEndTimestamp(String month, String format) throws ParseException{
        Date thisMonthDate = getDateByFormatDate(format, month);
        return (int) (thisMonthDate.getTime() / 1000) - 1;
    }

    /**
     * 获取指定月份上月结束时间戳2
     */
    public static int getLastMonthEndTimestamp(String month) throws ParseException{
        return getLastMonthEndTimestamp(month, yyyyMM_);
    }

    /**
    * 获取一个月的每一天1 (返回时间戳)
    */
    public static List<Integer> getTimeStampMonthEveryDayBy(String month, String format) throws ParseException {
        List<Integer> list = new ArrayList<>();

        int startTime = getMonthStartTimeStamp(month, format);
        int endTime = getMonthEndTimeStamp(month, format);
        for (int i = startTime; i <= endTime; i += oneDay_1) {
            list.add(i);
        }
        return list;
    }

    /**
     * 获取一个月的每一天2 (返回时间戳)
     */
    public static List<Integer> getTimeStampMonthEveryDayBy(String month) throws ParseException {
        return getTimeStampMonthEveryDayBy(month, yyyyMM_);
    }

    /**
     * 获取一个月的每一天1 (返回格式化日期)
     */
    public static List<String> getFormatDateMonthEveryDayBy(String month, String format, String returnFormat) throws ParseException {
        List<String> list = new ArrayList<>();

        int startTime = getMonthStartTimeStamp(month, format);
        int endTime = getMonthEndTimeStamp(month, format);
        for (int i = startTime; i <= endTime; i += oneDay_1) {
            list.add(getFormatByTimeStamp(i, returnFormat));
        }
        return list;
    }

    /**
     * 获取一个月的每一天2 (返回格式化日期)
     */
    public static List<String> getFormatDateMonthEveryDayBy(String month, String returnFormat) throws ParseException {
        return getFormatDateMonthEveryDayBy(month, yyyyMM_, returnFormat);
    }


    public static void main(String[] args) throws ParseException {
        List<String> everyDayBy = getFormatDateMonthEveryDayBy("2021-07", "yyyyMMdd");
        System.out.println("everyDayBy = " + everyDayBy);
    }







}
