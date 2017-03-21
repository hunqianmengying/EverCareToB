package com.evercare.app.model;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-28 14:22
 * 邮箱：842202389@qq.com
 * 消费治疗医院Item
 */
public class SaleCureItemInfo {
    private String product_name;
    private String residue_num;
    private String counselor_name;
    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private List<RecordItemInfo> record;


    public boolean isExpended = true;


    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getResidue_num() {
        return residue_num;
    }

    public void setResidue_num(String residue_num) {
        this.residue_num = residue_num;
    }

    public String getCounselor_name() {
        return counselor_name;
    }

    public void setCounselor_name(String counselor_name) {
        this.counselor_name = counselor_name;
    }

    public List<RecordItemInfo> getRecord() {
        return record;
    }

    public void setRecord(List<RecordItemInfo> record) {
        this.record = record;
    }
}
