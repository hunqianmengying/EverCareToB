package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-11-17 10:43
 * 邮箱：842202389@qq.com
 * 今日工作Item
 */
public class TodayWorkItemInfo {

    private String id;//任务id
    private String custom_id;//客户id
    private String custom_name;//客户姓名
    private String project_class_name;//项目类别
    private String describe;//描述
    private String flag;//标识 1面诊 2跟进


    private String business_cuid;//客户id

    private String task_type;// 1:未上门回访 2:未成交回访 3:术后回访 5:术后复查 6:术后调查 7:投诉回访 8:活动回访 10:注射回访 9:术前回访11:首次注射回访 12:治疗回访 13:首次治疗回访 4:其他
    private String start_time;//任务开始时间

    private String date_state;//类型   今日任务，已过期任务，已完成任务
    private String business_id; //任务源id

    public String getBusiness_cuid() {
        return business_cuid;
    }

    public void setBusiness_cuid(String business_cuid) {
        this.business_cuid = business_cuid;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }
}
