package com.evercare.app.model;

/**
 * 作者：xlren on 2016-11-7 10:48
 * 邮箱：renxianliang@126.com
 * 咨询/回访  ItemModel
 */
public class ConsultReviewItemInfo {
    private String id;
    private String type;
    private String content;
    private String time;
    private String day;

    public boolean isSelected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
