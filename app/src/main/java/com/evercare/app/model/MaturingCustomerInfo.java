package com.evercare.app.model;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 即将退回公海客户Model
 */
public class MaturingCustomerInfo {

    private List<MaturingItemInfo> tmpList;
    private List<MaturingItemInfo> privateList;


    public List<MaturingItemInfo> getTmplist() {
        return tmpList;
    }

    public void setTmplist(List<MaturingItemInfo> tmplist) {
        this.tmpList = tmplist;
    }

    public List<MaturingItemInfo> getPrivatelist() {
        return privateList;
    }

    public void setPrivatelist(List<MaturingItemInfo> privatelist) {
        this.privateList = privatelist;
    }
}
