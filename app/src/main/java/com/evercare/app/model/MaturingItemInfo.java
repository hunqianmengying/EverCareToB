package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 即将到期回公海ItemModel
 */

public class MaturingItemInfo {
    private String custom_id;
    private String name;
    private String mobile;

    private String consult_time;
    private String waiting_status;
    private String status;
    private String day;

    private String date_state;

    public String getDate_state() {
        return date_state;
    }

    public void setDate_state(String date_state) {
        this.date_state = date_state;
    }

    public String getCustom_id() {
        return custom_id;
    }

    public void setCustom_id(String custom_id) {
        this.custom_id = custom_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getConsult_time() {
        return consult_time;
    }

    public void setConsult_time(String consult_time) {
        this.consult_time = consult_time;
    }

    public String getWaiting_status() {
        return waiting_status;
    }

    public void setWaiting_status(String waiting_status) {
        this.waiting_status = waiting_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
