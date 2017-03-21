package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-11-25 10:52
 * 邮箱：842202389@qq.com
 * 客户信息
 */
public class CustomerInfo {

    private String custom_id;//客户id
    private String name;//客户名称
    private String overlap;// 0无1所属2交叉
    private String mobile;//电话
    private String business_id;//客户源id

    private String date_state;

    public String getOcean() {
        return ocean;
    }

    public void setOcean(String ocean) {
        this.ocean = ocean;
    }

    private String ocean;


    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
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

    public String getOverlap() {
        return overlap;
    }

    public void setOverlap(String overlap) {
        this.overlap = overlap;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
