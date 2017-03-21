package com.evercare.app.Entity;

import java.util.List;

/**
 * 作者：xlren on 2016/8/29 13:22
 * 邮箱：renxianliang@126.com
 * Gson集合解析
 */

public class JsonListResult<T> extends Result {
    private static final long serialVersionUID = 7880907731807860636L;

    /**
     * 数据
     */
    private List<T> data;


    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public JsonListResult() {
        super();
    }
}
