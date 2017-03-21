package com.evercare.app.model;

/**
 * 作者：LXQ on 2016-10-12 13:18
 * 邮箱：842202389@qq.com
 * Javadoc
 */
public class MenuItemInfo {
    public String getMenuDescription() {
        return menuDescription;
    }

    public void setMenuDescription(String menuDescription) {
        this.menuDescription = menuDescription;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    private String menuDescription;
    private int messageNumber;
}
