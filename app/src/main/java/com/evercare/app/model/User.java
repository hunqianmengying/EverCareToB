package com.evercare.app.model;

import java.io.Serializable;

/**
 * 作者：xlren on 2016/8/29 13:24
 * 邮箱：renxianliang@126.com
 * TODO
 */

public class User implements Serializable{
    private String uid;//用户ID

    private String mechanism_id;//所属机构id

    private String mobile;//手机号码

    private String nickname;//昵称

    private String user_img;//用户头像

    private String email;//邮箱

    private String birthday;//生日

    private String gender;//性别（0-未知，1-男，2-女）

    private Integer aqqge;//QQ

    private String last_login_time;//最后登录时间

    private String create_time;//注册时间

    private String status;//状态（-1-删除，0-禁用，1-正常，2-异常）
    private String days;//连续登录天数
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMechanism_id() {
        return mechanism_id;
    }

    public void setMechanism_id(String mechanism_id) {
        this.mechanism_id = mechanism_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAqqge() {
        return aqqge;
    }

    public void setAqqge(Integer aqqge) {
        this.aqqge = aqqge;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

}