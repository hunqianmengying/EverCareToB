package com.evercare.app.model;

/**
 * 作者：LXQ on 2017-1-13 09:26
 * 邮箱：842202389@qq.com
 * 订购项目列表item
 */
public class OrderProjectInfo {

    private String pay; //已付金额
    private String product_name;//项目名称
    private String buy_time;//购买日期
    private String buy_num;//订购次数
    private String residue_num;//剩余次数

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getBuy_time() {
        return buy_time;
    }

    public void setBuy_time(String buy_time) {
        this.buy_time = buy_time;
    }

    public String getBuy_num() {
        return buy_num;
    }

    public void setBuy_num(String buy_num) {
        this.buy_num = buy_num;
    }

    public String getResidue_num() {
        return residue_num;
    }

    public void setResidue_num(String residue_num) {
        this.residue_num = residue_num;
    }
}
