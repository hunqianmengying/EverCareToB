package com.evercare.app.model;

import java.io.Serializable;

/**
 * 作者：xlren on 2016/8/30 14:38
 * 邮箱：renxianliang@126.com
 * 用户登录信息
 */
public class LoginInfo implements Serializable {
    private String counselor_id;
    private String userName;
    private String token;
    private String rongcloud_token;
    private String portraits;
    private String alias;

    public String getPush_alias_id() {
        return push_alias_id;
    }

    public void setPush_alias_id(String push_alias_id) {
        this.push_alias_id = push_alias_id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    private String push_alias_id;

    public String getPortraits() {
        return portraits;
    }

    public void setPortraits(String portraits) {
        this.portraits = portraits;
    }

    public String getCounselor_id() {
        return counselor_id;
    }

    public void setCounselor_id(String counselor_id) {
        this.counselor_id = counselor_id;
    }


    public String getRongcloud_token() {
        return rongcloud_token;
    }

    public void setRongcloud_token(String rongcloud_token) {
        this.rongcloud_token = rongcloud_token;
    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
