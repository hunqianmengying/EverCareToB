package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-11-28 14:25
 * 邮箱：842202389@qq.com
 * 消费.治疗itemInfo
 */
public class RecordItemInfo {
    private String pay_date;
    private String type_name;
    private String price;
    private String discount;

    public String getPay_date() {
        return pay_date;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
