package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-11-16 11:19
 * 邮箱：842202389@qq.com
 * 老客新客成交记录Item
 */
public class TradeItemInfo {

   /* public TradeItemInfo(String name, String project, String price, String is_no_deal) {
        this.customer_name = name;
        this.project_name = project;
        this.project_price = price;
        this.is_no_deal = is_no_deal;
    }*/


    private String customer_name;
    private String project_name;
    private String project_price;
    private String is_no_deal;//1:未成交   2：成交

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getProject_price() {
        return project_price;
    }

    public void setProject_price(String project_price) {
        this.project_price = project_price;
    }

    public String getIs_no_deal() {
        return is_no_deal;
    }

    public void setIs_no_deal(String is_no_deal) {
        this.is_no_deal = is_no_deal;
    }
}
