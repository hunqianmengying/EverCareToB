package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 生日ItemModel
 */
public class BirthdayInfo {
    private String custom_id;
    private String name;
    private String mobile;
    private String birthday;
    private String status;//1:未沟通    2：已沟通

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String date_state;

    public String getConnect_state() {
        return connect_state;
    }

    public void setConnect_state(String connect_state) {
        this.connect_state = connect_state;
    }

    private String connect_state;


    public BirthdayInfo() {

    }

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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

}
