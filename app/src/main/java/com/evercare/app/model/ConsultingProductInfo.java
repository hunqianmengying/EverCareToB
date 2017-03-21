package com.evercare.app.model;

/**
 * 作者：xlren on 2016-12-6 16:10
 * 邮箱：renxianliang@126.com
 * 关注项目列表
 */
public class ConsultingProductInfo {
    private String id;
    private String business_id;
    private String statecode;
    private String statuscode;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getStatecode() {
        return statecode;
    }

    public void setStatecode(String statecode) {
        this.statecode = statecode;
    }

    public String getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(String statuscode) {
        this.statuscode = statuscode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
