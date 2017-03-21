package com.evercare.app.model;

import java.io.Serializable;

/**
 * 作者：LXQ on 2016-11-24 17:09
 * 邮箱：842202389@qq.com
 * 客户意愿
 */
public class ReasonInfo   implements Serializable {
    private String attribute_value;
    private String value;

    public String getAttribute_value() {
        return attribute_value;
    }

    public void setAttribute_value(String attribute_value) {
        this.attribute_value = attribute_value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
