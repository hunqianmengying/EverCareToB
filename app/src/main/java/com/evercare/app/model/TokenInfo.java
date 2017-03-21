package com.evercare.app.model;

import java.io.Serializable;

/**
 * 作者：xlren on 2016-10-19 11:30
 * 邮箱：renxianliang@126.com
 * 获取token
 */

public class TokenInfo implements Serializable {
    private String token;//设备唯一标识
    private String expires;//有效期
    private String uid;//用户id没有登录默认为0

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}