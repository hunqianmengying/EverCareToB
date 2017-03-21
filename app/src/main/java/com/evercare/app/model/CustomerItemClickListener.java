package com.evercare.app.model;

import android.view.View;

/**
 * 作者：LXQ on 2016-9-14 13:56
 * 邮箱：842202389@qq.com
 * 客户通讯录Item点击,长按点击事件
 */
public interface CustomerItemClickListener {

    public void onItemClick(View view, int position);

    public void onItemLongClick(View view, int position);
}
