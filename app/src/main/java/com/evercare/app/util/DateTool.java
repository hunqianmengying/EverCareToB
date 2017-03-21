package com.evercare.app.util;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * <pre>
 * 业务名:
 * 功能说明:  时间工具类
 * 编写日期:	2015年6月4日
 * 作者:	 xlren
 *
 * 历史记录
 * 1、修改日期：
 *    修改人：
 *    修改内容：
 * </pre>
 */
public class DateTool {

    private static DateTool instance = null;
    private static List<String> mFormatStr = null;
    private static final String[] WEEK_CN_NAME = {"周一", "周二", "周三", "周四",
            "周五", "周六", "周日"};

    private DateTool() {
    }

    public synchronized static DateTool getInstance() {
        if (instance == null) {
            instance = new DateTool();
        }
        return instance;
    }


    /**
     * 日期加时间"yyyy-MM-dd HH:mm:ss"
     */
    public static final String D_T_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日期"yyyy-MM-dd"
     */
    public static final String D_FORMAT = "yyyy-MM-dd";
    /**
     * 时间"HH:mm:ss"
     */
    public static final String T_FORMAT = "HH:mm:ss";
    /**
     * just year
     */
    public static final String YEAR = "yyyy";
    /**
     * just MONTH
     */
    public static final String MONTH = "MM";
    /**
     * just day
     */
    public static final String DAY = "dd";
    /**
     * one day millisecond
     */
    private static final long ONE_DAY = 86400000l;

    /**
     * log flag
     */
    private static final String TAG = "DateUtils";

    /**
     * 获取系统当前时间
     *
     * @param {@link #D_T_FORMAT}<br>
     *               {@link #D_FORMAT}<br>
     *               {@link #T_FORMAT}
     * @return String类型的时间表示如 2010-3-4 13:24:53秒，返回的格式由dateFormat来决定
     */
    public static String getCurrentTime(String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        String str = sdf.format(new Date());
        return str;
    }

    /**
     * 通过GMT获取当前时间，
     *
     * @param milliSecond 格林尼治标准时间可以通过{@link #getLongMilli()}获取。
     * @return 默认格式"yyyy-MM-dd HH:mm:ss"的日期字符串
     */
    public static String getCurrentTime(long milliSecond) {
        SimpleDateFormat sdf = new SimpleDateFormat(D_T_FORMAT);
        String str = sdf.format(new Date());
        return str;
    }

    /**
     * 获取GMT(格林尼治标准时间)1970年,1月 1日00:00:00这一刻之前或者是之后经历的毫秒数
     *
     * @return long
     */
    public static long getLongMilli() {
        return new Date().getTime();
    }

    /**
     * 获取星期
     *
     * @param sDate
     * @return
     */
    public static String getDayInWeek(String sDate, String format) {
        Date date = strToDate(sDate, format);
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        String s = df.format(date);
        return s;
    }

    /**
     * 获取星期
     *
     * @param Date
     * @return
     */
    public static String getDayInWeek(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        String s = df.format(date);
        return s;
    }

    /**
     * 返回月份之间的差。
     *
     * @param startYear
     * @param startMonth
     * @param endYear
     * @param endMonth
     * @return
     */
    public static int differMonth(String startYear, String startMonth,
                                  String endYear, String endMonth) {
        return (Integer.parseInt(endYear) - Integer.parseInt(startYear)) * 12
                + (Integer.parseInt(endMonth) - Integer.parseInt(startMonth));

    }

    // 获得当前日期与本周一相差的天数
    private static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            return -6;
        } else {
            return 2 - dayOfWeek;
        }
    }

    /**
     * 获得相应周的周一的日期
     *
     * @param weeks
     * @return
     */
    public static String getMonday(int weeks) {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    /**
     * 获得相应周的周日的日期
     *
     * @param weeks
     * @return
     */
    public static String getSunday(int weeks) {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    /**
     * 通过阿拉伯数字对应的中文显示星期几
     *
     * @param dateNum
     * @return
     */
    public static String getDateWeekCnName(int dateNum) {
        String s = "";
        try {
            s = WEEK_CN_NAME[dateNum];
        } catch (Exception e) {
            // TODO: handle exception
        }
        return s;
    }

    /**
     * 获取指定的Date对象，中文显示的星期。
     *
     * @param dateNum
     * @return
     */
    public static String getDateWeekCnName(Date date) {
        String s = "";
        int dateNum = getDateWeekNum(date);
        try {
            s = WEEK_CN_NAME[dateNum - 1];
        } catch (Exception e) {
            // TODO: handle exception
        }
        return s;
    }

    /**
     * 获取当前指定date所对应的在此星期中是第几天
     *
     * @param date
     * @return
     */
    public static int getDateWeekNum(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    /**
     * 通过string如‘2011-1-1’，返回Date对象,如果传入的格式不正确将发生异常，将会返回当前日期的Date对象。
     *
     * @param str     '2011-1-1'
     * @param pattern 'yyyy-MM-dd'
     * @return
     */
    public static Date strToDate(String str, String pattern) {
        Date date = null;
        if (str != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            try {
                date = df.parse(str);
            } catch (ParseException e) {
                date = new Date();
            }
        }
        return date;
    }

    /**
     * 在原有的日期上面加i天
     *
     * @param date
     * @param i
     * @return
     */
    public static Date add(Date date, int i) {
        date = new Date(date.getTime() + i * ONE_DAY);
        return date;
    }

    /**
     * 返回相差分钟数
     *
     * @param endDate
     * @param startDate
     * @return
     */
    public static int differMinute(Date endDate, Date startDate) {
        if (endDate == null || startDate == null)
            return 0;

        Calendar c1 = Calendar.getInstance();
        c1.setTime(endDate);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(startDate);
        long l = c1.getTimeInMillis() - c2.getTimeInMillis();

        return (int) (l / (1000 * 60));
    }

    /**
     * 返回相差秒数数
     *
     * @param endDate
     * @param startDate
     * @return
     */
    public static int differSecond(Date endDate, Date startDate) {
        if (endDate == null || startDate == null)
            return 0;

        Calendar c1 = Calendar.getInstance();
        c1.setTime(endDate);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(startDate);
        long l = c1.getTimeInMillis() - c2.getTimeInMillis();

        return (int) (l / (1000));
    }

    public static int differSecond(String endString, String startString) {
        Date startData = strToDate(startString, D_T_FORMAT);
        Date endData = strToDate(endString, D_T_FORMAT);
        return differSecond(endData, startData);
    }

    /**
     * 求日期之差 ，驶入指定格式的日期显示形式，这里只能输入yyyy-MM-dd的日期形式<br>
     * 比如：<b>compareDate("2011-2-1", "2011-3-1");</b>
     *
     * @param sDate1
     * @param sDate2
     * @return 返回求差后的天数
     */
    public static int differDate(String sDate1, String sDate2) {

        Date date1 = null;
        Date date2 = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            date1 = dateFormat.parse(sDate1);
            date2 = dateFormat.parse(sDate2);
        } catch (ParseException e) {

        }

        long dif = 0;
        if (date2.after(date1))
            dif = (date2.getTime() - date1.getTime()) / 1000 / 60 / 60 / 24;
        else
            dif = (date1.getTime() - date2.getTime()) / 1000 / 60 / 60 / 24;

        return (int) dif;
    }

    /**
     * 加1天
     *
     * @param date
     * @return
     */
    public static Date addOneDay(Date date) {
        return add(date, 1);
    }

    /**
     * 减1天
     *
     * @param date
     * @return
     */
    public static Date subOneDay(Date date) {
        return add(date, -1);
    }

    /**
     * @param date
     * @param count 负数
     * @return
     */
    public static Date subDay(Date date, int count) {
        return add(date, -count);
    }

    /**
     * @param date
     * @param dateFormat
     * @return
     */
    public static String dateToString(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String str = sdf.format(date);
        return str;
    }

    public static String dateToString(Long date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String str = sdf.format(date);
        return str;
    }


    public static Long stringToLong(String str) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        long timeStart = sdf.parse(str).getTime() / 1000;
        return timeStart;
    }

    public static String timeStamp2Date(String timestampString, String formats) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new SimpleDateFormat(formats)
                .format(new Date(timestamp));
        return date;
    }

    public static Date stringTodate(String str, String dateFormat)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = sdf.parse(str);
        return date;
    }

    public static String strYmd(String str, String dataFormat)
            throws ParseException {
        Date date = stringTodate(str, dataFormat);
        String dataStr = dateToString(date, dataFormat);
        return dataStr;
    }

    /**
     * 评论时间(单位秒)
     *
     * @return time
     */
    public static String timeComments(Long nowTime, Long sendTime) {
        String time = null;
        long value = nowTime - sendTime;
        if (value < 60) {// 小于一分钟
            time = "刚刚";
        } else if (value < 60 * 60) {// 小于一个小时
            time = value / 60 + "分钟前";
        } else if (value < 60 * 60 * 24) {// 小于一天
            time = value / 60 / 60 + "小时前";
        } else if (value < 60 * 60 * 24 * 5) {// 小于5天
            time = value / 60 / 60 / 24 + "天前";
        } else {
            time = dateToString(sendTime * 1000, "yyyy-MM-dd HH:mm");
        }
        return time;
    }

    /*
     * 获取两个字符串时间差的天数
     */
    public static int getDays(String minDays, String maxDays)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long minLong = sdf.parse(minDays).getTime() / 1000;
        int minInDays = (int) (minLong / 60 / 60 / 24);
        long maxLong = sdf.parse(maxDays).getTime() / 1000;
        int maxInDays = (int) (maxLong / 60 / 60 / 24);
        int days = maxInDays - minInDays;
        return days;
    }

    public static String converterTime(String time) {

        Date date = new Date();
        time = time + " " + dateToString(date, "HH:mm:ss");
        return time;
    }


    /**
     * 根据时间字符串返回long时间
     *
     * @return
     * @throws ParseException
     */
    public static long getLong(String str) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long longTime = sdf.parse(str).getTime() / 1000;
        return longTime;
    }

    public static String getTimeState(String time) {
//        Date timeDate = null;
//        try {
//            timeDate = stringTodate(time, "yyyy-MM-dd");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////
////        if (timeDate == null) {
////            try {
////                timeDate = stringTodate(time, "yyyy-MM-dd");
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
//        if (timeDate != null) {
//            Date today = new Date();
//            Calendar calendar = new GregorianCalendar();
//            calendar.setTime(today);
//
//            calendar.add(Calendar.DATE, -1);
//            Date yesterday = calendar.getTime();
//
//            calendar.add(Calendar.DATE, -1);
//            Date dayBeforeYesterday = calendar.getTime();
//
//            if (timeDate.before(today) && timeDate.after(yesterday)) {
//                return "昨天";
//            } else if (timeDate.before(yesterday) && timeDate.after(dayBeforeYesterday)) {
//                return "前天";
//            }
//        }
//
        int days = differDate(time, getCurrentTime("yyyy-MM-dd"));
        if (days == 0) {
            return "今天";
        } else if (days == 1) {
            return "昨天";
        } else if (days == 2) {
            return "前天";
        }
        return time;
    }
}
