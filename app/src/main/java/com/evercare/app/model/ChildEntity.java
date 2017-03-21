package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-10-31 2016-10-31
 * 邮箱：842202389@qq.com
 * 横向拖到控件信息类
 */
public class ChildEntity {
    private String childTitle;

    public boolean isOpen;

    public ChildEntity(String childTitle) {
        this.childTitle = childTitle;
    }

    public String getChildTitle() {
        return childTitle;
    }

}
