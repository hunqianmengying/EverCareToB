package com.evercare.app.model;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-25 10:52
 * 邮箱：842202389@qq.com
 * 新分客户
 */
public class AddCustomerInfo {

    private List<CustomerInfo> newlist;

    private List<CustomerInfo> oldlist;

    public List<CustomerInfo> getOldlist() {
        return oldlist;
    }

    public void setOldlist(List<CustomerInfo> oldlist) {
        this.oldlist = oldlist;
    }

    public List<CustomerInfo> getNewlist() {
        return newlist;
    }

    public void setNewlist(List<CustomerInfo> newlist) {
        this.newlist = newlist;
    }
}
