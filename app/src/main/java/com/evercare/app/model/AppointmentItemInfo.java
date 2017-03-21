package com.evercare.app.model;

import java.io.Serializable;

/**
 * 作者：LXQ on 2016-10-24 17:15
 * 邮箱：842202389@qq.com
 * 预约信息类
 */
public class AppointmentItemInfo implements Serializable {
    private int page_count;//总页数
    private String customer_name;//客户姓名
    private String appointment_time;//预约时间
    private String appointment_status;//预约状态（0用户预约1待审核2已预约）
    private String appointment_project;//预约治疗项目
    private String appointment_id;//预约项目id

    public boolean isOpen;

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public void setAppointment_time(String appointment_time) {
        this.appointment_time = appointment_time;
    }

    public String getAppointment_status() {
        return appointment_status;
    }

    public void setAppointment_status(String appointment_status) {
        this.appointment_status = appointment_status;
    }

    public String getAppointment_project() {
        return appointment_project;
    }

    public void setAppointment_project(String appointment_project) {
        this.appointment_project = appointment_project;
    }

    public String getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(String appointment_id) {
        this.appointment_id = appointment_id;
    }
}
