package com.seer.operation.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhang sir
 * @date 2019-5-19
 */
public class Times {
    public static final Long EIGHT_HOURS_TIMESTAMPS = 1000 * 60 * 60 * 8L;
    public static final Long DAY_TIMESTAMPS = 1000 * 60 * 60 * 24L;

    public static String formatToDate(Long times) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(times);
        return simpleDateFormat.format(date);
    }

    /**
     * 加八小时后的时间戳
     *
     * @param tDate
     * @return
     */
    public static Long formatTDateToEastTimes(String tDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(tDate);
        } catch (ParseException e) {
            return null;
        }
        Long times = date.getTime() + EIGHT_HOURS_TIMESTAMPS;
        return times;
    }

    /**
     * 加八小时后对应日期的零点时间戳
     *
     * @param tDate
     * @return
     */
    public static Long formatTDateToEastZeroTimes(String tDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = formatter.parse(tDate);
            Long times = date.getTime() + EIGHT_HOURS_TIMESTAMPS;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(times);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDateByTimes(Long times) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(times);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        StringBuffer stringBuffer = new StringBuffer();
        if (month < 10 && day < 10) {
            stringBuffer = stringBuffer.append(year).append("-").append("0").append(month).append("-").append("0").append(day);
        } else if (month < 10) {
            stringBuffer = stringBuffer.append(year).append("-").append("0").append(month).append("-").append(day);
        } else if (day < 10) {
            stringBuffer = stringBuffer.append(year).append("-").append(month).append("-").append("0").append(day);
        } else {
            stringBuffer = stringBuffer.append(year).append("-").append(month).append("-").append(day);
        }
        return stringBuffer.toString();
    }

    /**
     * 获取指定日期的0.0.0.000点时间戳
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Long getTimesZeroByDay(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定日期的23.59.59.999点时间戳
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Long getTimesEndByDay(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    /**
     * 获取时间戳当天0.0.0.000点时间戳
     *
     * @return
     */
    public static Long getTimesDayZero(Long time) {
        Calendar cal = Calendar.getInstance();
        if (null == time) {
            cal.setTimeInMillis(System.currentTimeMillis());
        } else {
            cal.setTimeInMillis(time);
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取当天23.59.59.999时间戳
     *
     * @return
     */
    public static Long getTimesDayEnd() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定年份指定月数的第一天0点时间戳
     *
     * @return
     */
    public static Long getTimesFirstDayMonth(int year, int month) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();

    }

    /**
     * 获取指定年份指定月份的最后一天24点时间戳
     *
     * @return
     */
    public static Long getTimesLastDayMonth(int year, int month, int lastDay) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();

    }

    /**
     * 获取指定年份指定月数的最后一天
     *
     * @param currentMonth
     * @return
     */
    public static int getLastDayMonth(int year, int currentMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, currentMonth, 1);
        calendar.add(Calendar.DATE, -1);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }
}
