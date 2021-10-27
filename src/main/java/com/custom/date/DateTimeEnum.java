package com.custom.date;

import java.util.Calendar;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/27 14:20
 * @Desc：时间日期处理单位
 **/
public enum DateTimeEnum {

    /**
    * 分钟
    */
    MINUTES(1, Calendar.MINUTE),

    /**
    * 小时
    */
    HOUR(2, Calendar.HOUR),

    /**
    * 天
    */
    DAY(3,  Calendar.DATE),

    /**
    * 周
    */
    WEEK(4, Calendar.WEEK_OF_YEAR),

    /**
    * 月
    */
    MONTH(5, Calendar.MONTH),

    /**
    * 年
    */
    YEAR(6, Calendar.YEAR);

    private int unit;

    private int field;

    DateTimeEnum(int unit, int field) {
        this.unit = unit;
        this.field = field;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }
}
