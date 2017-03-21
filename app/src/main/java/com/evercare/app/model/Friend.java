package com.evercare.app.model;

/**
 * 作者：xlren on 2016-11-29 17:34
 * 邮箱：renxianliang@126.com
 * 好友
 */
public class Friend  {

    private String spelling;

    private String firstLetter;
    private String rongyun_id;
    private String name;
    private String custom_id;

    public String getPortraits() {
        return portraits;
    }

    public void setPortraits(String portraits) {
        this.portraits = portraits;
    }

    private String portraits;

    public String getRongyun_id() {
        return rongyun_id;
    }

    public void setRongyun_id(String rongyun_id) {
        this.rongyun_id = rongyun_id;
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
}
