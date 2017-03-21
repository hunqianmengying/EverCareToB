package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-9-13 10:51
 * 邮箱：842202389@qq.com
 * 客户详细信息类
 */
public class PersonDetailInfo {
    private String custom_id;

    private String name;
    private String mobile;
    private String sex;
    private String age;
    private String birthday;
    private String custom_card;
    private String overlap;//0无1所属2交叉
    private String ocean;
    private String exit_accout;

    private String rongcloud_id;


    private String remark;//客户备注


    private String consume_amount;//收银总金额?􀯛􁰂􁷐
    private String remaining; //􁶼?预收剩余?􀛃􀖟
    private String happiness;   //幸福宝剩余􁐰􀨪􀛃􀖟
    private String doctor;//末次治疗医生?􀽺􁀜􁋌􀜅􁊞
    private String first_consume_time;//0000-00-00 00:00:00",末次消费时间􀽺􁁾􁩇􀷸􁳵
    private String last_cure_time;//末次治疗时间
    private String LastFollowDate;//最近跟单日期

    public String getExit_open() {
        return exit_open;
    }

    public void setExit_open(String exit_open) {
        this.exit_open = exit_open;
    }

    private String exit_open;//是否显示开单按钮0不显示1显示

    public String getRecord_code() {
        return record_code;
    }

    public void setRecord_code(String record_code) {
        this.record_code = record_code;
    }

    private String record_code;//病案号
    public String getRongcloud_id() {
        return rongcloud_id;
    }

    public void setRongcloud_id(String rongcloud_id) {
        this.rongcloud_id = rongcloud_id;
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

    private String business_id;//客户原id


    public String getConsume_amount() {
        return consume_amount;
    }

    public void setConsume_amount(String consume_amount) {
        this.consume_amount = consume_amount;
    }

    public String getRemaining() {
        return remaining;
    }

    public void setRemaining(String remaining) {
        this.remaining = remaining;
    }

    public String getHappiness() {
        return happiness;
    }

    public void setHappiness(String happiness) {
        this.happiness = happiness;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getFirst_consume_time() {
        return first_consume_time;
    }

    public void setFirst_consume_time(String first_consume_time) {
        this.first_consume_time = first_consume_time;
    }

    public String getLast_cure_time() {
        return last_cure_time;
    }

    public void setLast_cure_time(String last_cure_time) {
        this.last_cure_time = last_cure_time;
    }

    public String getLastFollowDate() {
        return LastFollowDate;
    }

    public void setLastFollowDate(String lastFollowDate) {
        LastFollowDate = lastFollowDate;
    }

    public String getExit_accout() {
        return exit_accout;
    }

    public void setExit_accout(String exit_accout) {
        this.exit_accout = exit_accout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustom_id() {
        return custom_id;
    }

    public void setCustom_id(String custom_id) {
        this.custom_id = custom_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCustom_card() {
        return custom_card;
    }

    public void setCustom_card(String custom_card) {
        this.custom_card = custom_card;
    }

    public String getOverlap() {
        return overlap;
    }

    public void setOverlap(String overlap) {
        this.overlap = overlap;
    }

    public String getOcean() {
        return ocean;
    }

    public void setOcean(String ocean) {
        this.ocean = ocean;
    }
}
