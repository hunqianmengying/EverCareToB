package com.evercare.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：LXQ on 2016-11-24 17:01
 * 邮箱：842202389@qq.com
 * 今日工作详情页Model
 */
public class TaskDetailInfo implements Serializable {

    private String project_class;// 项目类别

    private String custom_card;//客户卡号

    private String business_cuid;//客户源id

    private String id;//id
    private String custom_id;//客户id
    private String custom_name;//客户姓名
    private String project_class_name;//项目类别
    private String state;
    private String task_type;
    private String type;
    private String start_time;//任务开始
    private String mobile;//客户电话
    private String business_id;//任务源id

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;//
    private String remark;

    public String getBusiness_cuid() {
        return business_cuid;
    }

    public void setBusiness_cuid(String business_cuid) {
        this.business_cuid = business_cuid;
    }

    public String getCustom_card() {
        return custom_card;
    }

    public void setCustom_card(String custom_card) {
        this.custom_card = custom_card;
    }

    public String getProject_class() {
        return project_class;
    }

    public void setProject_class(String project_class) {
        this.project_class = project_class;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    private List<ReasonInfo> customer_reasons;//客户意愿
    private List<String> customer_config_reasons;//自定义客户意愿

    public List<String> getCustomer_config_reasons() {
        return customer_config_reasons;
    }

    public void setCustomer_config_reasons(List<String> customer_config_reasons) {
        this.customer_config_reasons = customer_config_reasons;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<ReasonInfo> getCustomer_reasons() {
        return customer_reasons;
    }

    public void setCustomer_reasons(List<ReasonInfo> customer_reasons) {
        this.customer_reasons = customer_reasons;
    }
}
