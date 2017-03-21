package com.evercare.app.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.DateInfo;
import com.evercare.app.util.DateTool;
import com.evercare.app.util.SpecialCalendarUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 作者：LXQ on 2016-10-28 10:36
 * 邮箱：842202389@qq.com
 * 日历gridview中的每一个item显示的textview
 */
public class WorkCalendarAdapter extends BaseAdapter {
    private boolean isLeapyear = false; // 是否为闰年
    public int daysOfMonth = 0; // 某月的天数
    private int dayOfWeek = 0; // 具体某一天是星期几
    private int lastDaysOfMonth = 0; // 上一个月的总天数
    private Context context;
    private ArrayList<DateInfo> daylist = new ArrayList<DateInfo>(); // 一个gridview中的日期存入此数组中42

    private SpecialCalendarUtil specialCalendarUtil = null;
    private String currentYear = "";
    private String currentMonth = "";
    private String currentDay = "";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private int currentFlag = -1; // 用于标记当天
    private int[] schDateTagFlag = null; // 存储当月所有的日程日期

    private String showYear = ""; // 用于在头部显示的年份
    private String showMonth = ""; // 用于在头部显示的月份
    private String animalsYear = "";
    private String leapMonth = ""; // 闰哪一个月
    private String cyclical = ""; // 天干地支
    public int ClickItemposition = -1;

    private View contentView;

    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private boolean isShowAllDate;

    public WorkCalendarAdapter(Context context, Resources rs, int jumpMonth, int jumpYear, int year_c, int month_c,
                               int day_c, boolean isShowAllDate) {
        this.context = context;
        specialCalendarUtil = new SpecialCalendarUtil();

        this.isShowAllDate = isShowAllDate;

        int stepYear = year_c + jumpYear;
        //this.type = type;
        this.year_c = year_c;
        this.month_c = month_c;
        this.day_c = day_c;

        int stepMonth = month_c + jumpMonth;
        if (stepMonth > 0) {
            // 往下一个月滑动
            if (stepMonth % 12 == 0) {
                stepYear = year_c + stepMonth / 12 - 1;
                stepMonth = 12;
            } else {
                stepYear = year_c + stepMonth / 12;
                stepMonth = stepMonth % 12;
            }
        } else {
            // 往上一个月滑动
            stepYear = year_c - 1 + stepMonth / 12;
            stepMonth = stepMonth % 12 + 12;
            if (stepMonth % 12 == 0) {

            }
        }

        currentYear = String.valueOf(stepYear); // 得到当前的年份
        currentMonth = String.valueOf(stepMonth); // 得到本月
        // （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
        currentDay = String.valueOf(day_c); // 得到当前日期是哪天

        getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
    }

    public WorkCalendarAdapter(Context context, Resources rs, int year, int month, int day) {
        this.context = context;
        specialCalendarUtil = new SpecialCalendarUtil();
        currentYear = String.valueOf(year);// 得到跳转到的年份
        currentMonth = String.valueOf(month); // 得到跳转到的月份
        currentDay = String.valueOf(day); // 得到跳转到的天
        getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return daylist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public int getTodayIndex() {
        for (int i = 0; i < daylist.size(); i++) {
            DateInfo item = daylist.get(i);
            if (item.getCurrentFlag()) {
                return i;
            }
        }
        return -1;
    }

    public int getSomeDayIndex(Date someday) {
        for (int i = 0; i < daylist.size(); i++) {
            DateInfo item = daylist.get(i);
            String itemStr = item.getDate();
            String dayStr = DateTool.dateToString(someday, "yyyy-MM-dd");
            if (TextUtils.equals(itemStr, dayStr)) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.workcalendar_item, null);
        }
        contentView = convertView;
        TextView txt_day_number = (TextView) convertView.findViewById(R.id.txt_day_number);
        RelativeLayout rl_work_num = (RelativeLayout) convertView.findViewById(R.id.rl_work_num);


        DateInfo item = daylist.get(position);
        String d = item.getDay() + "";
        //String dv = item.getLunarDay();
        txt_day_number.setText(d);

        if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
            // 当前月信息显示
            if (item.getClickble()) {
                item.setBackground(1);
            }

            if (position % 7 == 0 || position % 7 == 6) {
                // 当前月信息显示
                if (item.getClickble()) {
                    item.setBackground(1);
                }
            }
        }

        if (isShowAllDate) {
            txt_day_number.setVisibility(View.VISIBLE);
            if (item.getBackground() == 0) {
                txt_day_number.setTextColor(ContextCompat.getColor(context, R.color.gray_b6));
                txt_day_number.setTag(R.id.tag_color, R.color.gray_b6);
            } else {
                txt_day_number.setTextColor(ContextCompat.getColor(context, R.color.header_text));
                txt_day_number.setTag(R.id.tag_color, R.color.header_text);
            }
        } else {
            if (item.getClickble()) {
                txt_day_number.setVisibility(View.VISIBLE);
            } else {
                txt_day_number.setVisibility(View.INVISIBLE);
            }
        }

        if (item.getCurrentFlag()) {
            txt_day_number.setTextColor(ContextCompat.getColor(context, R.color.white));
            txt_day_number.setBackgroundResource(R.drawable.txt_circle_bg);
            rl_work_num.setVisibility(View.VISIBLE);
        }

        txt_day_number.setTag(R.id.tag_click_able, item.getClickble());

        return convertView;
    }


    // 得到某年的某月的天数且这月的第一天是星期几

    public void getCalendar(int year, int month) {
        isLeapyear = specialCalendarUtil.isLeapYear(year); // 是否为闰年
        daysOfMonth = specialCalendarUtil.getDaysOfMonth(isLeapyear, month); // 某月的总天数
        dayOfWeek = specialCalendarUtil.getWeekdayOfMonth(year, month); // 某月第一天为星期几
        lastDaysOfMonth = specialCalendarUtil.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数

        getweek(year, month);
    }

    // 将一个月中的每一天的值添加入数组dayNuber中
    private void getweek(int year, int month) {
        int j = 1;

        // 得到当前月的所有日程日期(这些日期需要标记)
        for (int i = 0; i < 42; i++) {
            DateInfo item = new DateInfo();
            item.setYear(year);
            if (i < dayOfWeek) { // 前一个月
                int temp = lastDaysOfMonth - dayOfWeek + 1;
                item.setClickble(false);
                item.setDay(temp + i);
                item.setBackground(0);
                item.setMonth(month - 1);
            } else if (i < daysOfMonth + dayOfWeek) { // 本月
                int day = i - dayOfWeek + 1;

                item.setClickble(true);
                item.setDay(day);
                item.setMonth(month);

                // 对于当前月才去标记当前日期
                if ((year_c == year) && (month_c == month) && (day_c == day)) {
                    // 标记当前日期
                    currentFlag = i;
                    item.setCurrentFlag(true);
                } else {
                    item.setCurrentFlag(false);
                }
                setShowYear(String.valueOf(year));
                setShowMonth(String.valueOf(month));
            } else { // 下一个月
                item.setClickble(false);
                item.setDay(j);
                item.setBackground(0);
                item.setMonth(month + 1);
                j++;
            }
            daylist.add(item);
        }
    }

    /**
     * 点击每一个item时返回item中的日期
     *
     * @param position
     * @return
     */
    public DateInfo getDateByClickItem(int position) {
        ClickItemposition = position;
        return daylist.get(position);
    }

    /**
     * 在点击gridView时，得到这个月中第一天的位置
     *
     * @return
     */
    public int getStartPositon() {
        return dayOfWeek + 7;
    }

    /**
     * 在点击gridView时，得到这个月中最后一天的位置
     *
     * @return
     */
    public int getEndPosition() {
        return (dayOfWeek + daysOfMonth + 7) - 1;
    }

    public String getShowYear() {
        return showYear;
    }

    public void setShowYear(String showYear) {
        this.showYear = showYear;
    }

    public String getShowMonth() {
        return showMonth;
    }

    public void setShowMonth(String showMonth) {
        this.showMonth = showMonth;
    }

    public String getAnimalsYear() {
        return animalsYear;
    }

    public void setAnimalsYear(String animalsYear) {
        this.animalsYear = animalsYear;
    }

    public String getLeapMonth() {
        return leapMonth;
    }

    public void setLeapMonth(String leapMonth) {
        this.leapMonth = leapMonth;
    }

    public String getCyclical() {
        return cyclical;
    }

    public void setCyclical(String cyclical) {
        this.cyclical = cyclical;
    }


    public interface GridViewLoadedListener {
        public void loaded();
    }
}
