package com.evercare.app.model;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-18 15:08
 * 邮箱：842202389@qq.com
 * 今日工作中日历信息item
 */
public class WorkCalendarInfo {

    private List<MonthWorkItemInfo> monthwork;//今日工作日历数据

    private List<TodayWorkItemInfo> daydata;//今日数据

    public List<MonthWorkItemInfo> getMonthwork() {
        return monthwork;
    }

    public void setMonthwork(List<MonthWorkItemInfo> monthwork) {
        this.monthwork = monthwork;
    }

    public List<TodayWorkItemInfo> getDaydata() {
        return daydata;
    }

    public void setDaydata(List<TodayWorkItemInfo> daydata) {
        this.daydata = daydata;
    }
}
