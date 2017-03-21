package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-11-15 10:21
 * 邮箱：842202389@qq.com
 * 首页参数类
 */
public class HomeModelInfo {
    private String birthdaynum;//生日客户数
    private String newnum;//新分客户数
    private String openordernum;//今日可开单数
    private String expiringnum;//即将到期回公海数
    private String worknum;   //今日工作数
    private String activitynum;//活动回访数数
    private String otheractivitynum;//其他回访数
    private String sale_total;//销售额
    private String sale_percent;//销售额百分比
    private String new_total;//新客成交率
    private String new_percent;//
    private String old_total;//老客成交率
    private String old_percent;//

    private String answer_total;//随诊客户

    public String getAnswer_total() {
        return answer_total;
    }

    public void setAnswer_total(String answer_total) {
        this.answer_total = answer_total;
    }

    public String getSale_total() {
        return sale_total;
    }

    public void setSale_total(String sale_total) {
        this.sale_total = sale_total;
    }

    public String getSale_percent() {
        return sale_percent;
    }

    public void setSale_percent(String sale_percent) {
        this.sale_percent = sale_percent;
    }

    public String getNew_total() {
        return new_total;
    }

    public void setNew_total(String new_total) {
        this.new_total = new_total;
    }

    public String getNew_percent() {
        return new_percent;
    }

    public void setNew_percent(String new_percent) {
        this.new_percent = new_percent;
    }

    public String getOld_total() {
        return old_total;
    }

    public void setOld_total(String old_total) {
        this.old_total = old_total;
    }

    public String getOld_percent() {
        return old_percent;
    }

    public void setOld_percent(String old_percent) {
        this.old_percent = old_percent;
    }

    public String getWorknum() {
        return worknum;
    }

    public void setWorknum(String worknum) {
        this.worknum = worknum;
    }

    public String getActivitynum() {
        return activitynum;
    }

    public void setActivitynum(String activitynum) {
        this.activitynum = activitynum;
    }

    public String getOtheractivitynum() {
        return otheractivitynum;
    }

    public void setOtheractivitynum(String otheractivitynum) {
        this.otheractivitynum = otheractivitynum;
    }

    public String getBirthdaynum() {
        return birthdaynum;
    }

    public void setBirthdaynum(String birthdaynum) {
        this.birthdaynum = birthdaynum;
    }

    public String getNewnum() {
        return newnum;
    }

    public void setNewnum(String newnum) {
        this.newnum = newnum;
    }

    public String getOpenordernum() {
        return openordernum;
    }

    public void setOpenordernum(String openordernum) {
        this.openordernum = openordernum;
    }

    public String getExpiringnum() {
        return expiringnum;
    }

    public void setExpiringnum(String expiringnum) {
        this.expiringnum = expiringnum;
    }
}
