package com.evercare.app.model;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-7 16:30
 * 邮箱：842202389@qq.com
 * 今日可开单Item
 */
public class TodayOpenOrderInfo {
    private List<OpenOrderItemInfo> openorder;//今天可开单数据
    private List<OpenOrderItemInfo> finshopenorder;//已完成任务

    public List<OpenOrderItemInfo> getFinshopenorder() {
        return finshopenorder;
    }

    public void setFinshopenorder(List<OpenOrderItemInfo> finshopenorder) {
        this.finshopenorder = finshopenorder;
    }

    public List<OpenOrderItemInfo> getOpenorder() {
        return openorder;
    }

    public void setOpenorder(List<OpenOrderItemInfo> openorder) {
        this.openorder = openorder;
    }
}
