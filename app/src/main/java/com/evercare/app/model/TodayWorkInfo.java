package com.evercare.app.model;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-17 10:43
 * 邮箱：842202389@qq.com
 * 今日工作Item
 */
public class TodayWorkInfo {

    private List<TodayWorkItemInfo> daydata;//今日数据
    private List<TodayWorkItemInfo> staledata;//已过期数据
    private List<TodayWorkItemInfo> finishdata;//已完成数据

    public List<TodayWorkItemInfo> getDaydata() {
        return daydata;
    }

    public void setDaydata(List<TodayWorkItemInfo> daydata) {
        this.daydata = daydata;
    }

    public List<TodayWorkItemInfo> getStaledata() {
        return staledata;
    }

    public void setStaledata(List<TodayWorkItemInfo> staledata) {
        this.staledata = staledata;
    }

    public List<TodayWorkItemInfo> getFinishdata() {
        return finishdata;
    }

    public void setFinishdata(List<TodayWorkItemInfo> finishdata) {
        this.finishdata = finishdata;
    }
}
