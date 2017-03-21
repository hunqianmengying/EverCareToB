package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-11-7 16:30
 * 邮箱：842202389@qq.com
 * 今日开单Item
 */
public class OpenOrderItemInfo {

    private String business_id;
    private String business_cuid;
    private String project_class;

    private String custom_id;
    private String custom_name;//客户名称
    private String project_class_name;//项目类别
    private String type;// 1:初诊 2:复诊 3:复查 4:再消费 5:其他 6:治疗
    private String order_status;//开单状态1 开单 0不开单
    private String arrvie_time;//到诊时间
    private String id;

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getBusiness_cuid() {
        return business_cuid;
    }

    public void setBusiness_cuid(String business_cuid) {
        this.business_cuid = business_cuid;
    }

    public String getProject_class() {
        return project_class;
    }

    public void setProject_class(String project_class) {
        this.project_class = project_class;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String date_state;//今日   已完成任务

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

    public String getCustom_name() {
        return custom_name;
    }

    public void setCustom_name(String custom_name) {
        this.custom_name = custom_name;
    }

    public String getProject_class_name() {
        return project_class_name;
    }

    public void setProject_class_name(String project_class_name) {
        this.project_class_name = project_class_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getArrvie_time() {
        return arrvie_time;
    }

    public void setArrvie_time(String arrvie_time) {
        this.arrvie_time = arrvie_time;
    }
}
