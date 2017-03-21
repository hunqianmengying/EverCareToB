package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-11-28 14:22
 * 邮箱：842202389@qq.com
 * 消费治疗医院Item
 */
public class OrgsItemInfo {
    private String id;
    private String name;
    private String parent_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
}
