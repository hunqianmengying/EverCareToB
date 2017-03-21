package com.evercare.app.model;

/**
 * 作者：xlren on 2017-2-24 14:04
 * 邮箱：renxianliang@126.com
 * 今日已开单人员列表
 */
public class AlreadyOpenOrderInfo {
    private String custom_id;
    private String custom_name;
    private String project_class_name;

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

    private String type;

}
