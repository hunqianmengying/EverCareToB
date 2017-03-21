package com.evercare.app.model;


/**
 * 作者：LXQ on 2016-11-28 14:19
 * 邮箱：842202389@qq.com
 * 消费记录
 */
public class SaleCureInfo {
    private String pay_date; //付款时间
    private String counselor_name;//咨询师姓名
    private String product_name;//项目名称
    private String account;//销售金额
    private String type;//业务类型 0：全部（不传）  1：产品、药品收款，2：治疗，3：换卡费用，4：项目收款，5：辅助项目收款，6：内部用料


    private String doctor_name;//医生
    private String nurse_name;//护士

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getNurse_name() {
        return nurse_name;
    }

    public void setNurse_name(String nurse_name) {
        this.nurse_name = nurse_name;
    }

    private String buy_num;//购买次数

    public String getPay_date() {
        return pay_date;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public String getCounselor_name() {
        return counselor_name;
    }

    public void setCounselor_name(String counselor_name) {
        this.counselor_name = counselor_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getBuy_num() {
        return buy_num;
    }

    public void setBuy_num(String buy_num) {
        this.buy_num = buy_num;
    }
}