package com.evercare.app.model;

import java.util.List;

/**
 * 作者：xlren on 2016-11-7 10:48
 * 邮箱：renxianliang@126.com
 * 咨询/回访
 */
public class ConsultReviewInfo {

    private List<OrgsItemInfo> orgs;

    private List<ConsultReviewItemInfo> list;


    public List<OrgsItemInfo> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<OrgsItemInfo> orgs) {
        this.orgs = orgs;
    }

    public List<ConsultReviewItemInfo> getList() {
        return list;
    }

    public void setList(List<ConsultReviewItemInfo> list) {
        this.list = list;
    }
}
