package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-9-13 10:51
 * 邮箱：842202389@qq.com
 * 客户信息类
 */
public class PersonInfo  {

    private String spelling;

    private String firstLetter;

    private String business_id;//客户源id

    private String name;
    private String custom_id;
    private String mobile;
    private String sex;
    private String age;
    private String birthday;
    private String custom_card;
    private String overlap;//0无1所属2交叉
    private String ocean;

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpelling() {
        return spelling;
    }

    public void setSpelling(String spelling) {
        this.spelling = spelling;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public String toString() {
        return "姓名：" + getName() + "   拼音：" + getSpelling() + "    首字母："
                + getFirstLetter();

    }
}
