package com.evercare.app.model;

import com.evercare.app.util.DateTool;

import java.io.Serializable;
import java.util.Date;

/**
 * 作者：LXQ on 2016-10-31 2016-10-31
 * 邮箱：842202389@qq.com
 * 时间信息类
 */
public class DateInfo implements Serializable {
    private Boolean isCurrentMonth = false;//是否是当月

    private Boolean targetId = false;// 可约日期(天)
    private Boolean currentFlag = false;// 标记当前日期
    private Boolean clickble = true;// 是否可点击
    private Boolean click = false;// 选中
    private int background = 0;//0.灰色1.黑色2.

    private String lunarDay;
    //private int consultNum = 0;//咨询预约数
    //private int doctorNum = 0;//治疗预约数
    //private int type = 0;//0.预约详情1.预约咨询2.预约治疗
    private int year = 0;
    private int day = 0;//每天
    private int Month = 0;
    private int clickdate = -1;

    private String date;

    public String getDate() {
        Date newdate = DateTool.strToDate(String.valueOf(year) + "-" + String.valueOf(Month) + "-" + String.valueOf(day), "yyyy-MM-dd");
        date = DateTool.dateToString(newdate, "yyyy-MM-dd");
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getClickdate() {
        return clickdate;
    }

    public void setClickdate(int clickdate) {
        this.clickdate = clickdate;
    }

    public Boolean getCurrentFlag() {
        return currentFlag;
    }

    public void setCurrentFlag(Boolean currentFlag) {
        this.currentFlag = currentFlag;
    }

    public Boolean getTargetId() {
        return targetId;
    }

    public void setTargetId(Boolean targetId) {
        this.targetId = targetId;
    }

    public Boolean getClickble() {
        return clickble;
    }

    public void setClickble(Boolean clickble) {
        this.clickble = clickble;
    }

    public Boolean getClick() {
        return click;
    }

    public void setClick(Boolean click) {
        this.click = click;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public String getLunarDay() {
        return lunarDay;
    }

    public void setLunarDay(String lunarDay) {
        this.lunarDay = lunarDay;
    }

    /*public int getConsultNum() {
        return consultNum;
    }

    public void setConsultNum(int consultNum) {
        this.consultNum = consultNum;
    }

    public int getDoctorNum() {
        return doctorNum;
    }

    public void setDoctorNum(int doctorNum) {
        this.doctorNum = doctorNum;
    }*/
    public Boolean getCurrentMonth() {
        return isCurrentMonth;
    }

    public void setCurrentMonth(Boolean currentMonth) {
        isCurrentMonth = currentMonth;
    }

/*    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }*/

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return Month;
    }

    public void setMonth(int month) {
        Month = month;
    }
}
