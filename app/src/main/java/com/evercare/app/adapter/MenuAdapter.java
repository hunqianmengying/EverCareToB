package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.MenuItemInfo;

import java.util.List;

/**
 * 作者：LXQ on 2016-10-12 14:38
 * 邮箱：842202389@qq.com
 * 菜单栏Adapter，包括按钮样式和事件
 */
public class MenuAdapter extends BaseAdapter {
    private Context context;
    private List<MenuItemInfo> menuItemInfos;
    private LayoutInflater inflater;

    public MenuAdapter(Context context, List<MenuItemInfo> menuItemInfoList) {
        this.context = context;
        this.menuItemInfos = menuItemInfoList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return menuItemInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItemInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MenuViewHolder holder = null;
        MenuItemInfo menuItemInfo = menuItemInfos.get(position);

        if (convertView == null) {
            holder = new MenuViewHolder();
            convertView = inflater.inflate(R.layout.menuitem_layout, null);

            holder.txt_message_number = (TextView) convertView.findViewById(R.id.txt_message_number);
            holder.txt_menu_description = (TextView) convertView.findViewById(R.id.txt_menu_description);
            convertView.setTag(holder);

        } else {
            holder = (MenuViewHolder) convertView.getTag();
        }

        if (menuItemInfo.getMessageNumber() > 999) {
            holder.txt_message_number.setText("999+");
        } else {
            holder.txt_message_number.setText("" + menuItemInfo.getMessageNumber());
        }
        holder.txt_menu_description.setText(menuItemInfo.getMenuDescription());

        return convertView;
    }


    public class MenuViewHolder {
        public TextView txt_message_number;
        public TextView txt_menu_description;
    }
}
