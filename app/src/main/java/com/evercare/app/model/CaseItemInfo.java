package com.evercare.app.model;

import java.io.Serializable;

/**
 * 作者：LXQ on 2016-9-26 16:27
 * 邮箱：842202389@qq.com
 * 案例信息类
 */
public class CaseItemInfo implements Serializable {
    public String getCase_name() {
        return case_name;
    }

    public void setCase_name(String case_name) {
        this.case_name = case_name;
    }

    public String getCase_img() {
        return case_img;
    }

    public void setCase_img(String case_img) {
        this.case_img = case_img;
    }

    public String getCase_url() {
        return case_url;
    }

    public void setCase_url(String case_url) {
        this.case_url = case_url;
    }

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }

    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }

    private int page_count;//总页数
    private String case_id;//案例id
    private String case_name;//案例名称
    private String case_img;//案例图片
    private String case_url;//案例详情连接

}
