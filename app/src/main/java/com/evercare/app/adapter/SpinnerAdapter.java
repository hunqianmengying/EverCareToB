package com.evercare.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.TaskPlanItemInfo;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-17 13:22
 * 邮箱：842202389@qq.com
 * Javadoc
 */
public class SpinnerAdapter extends BaseAdapter {
    private List<TaskPlanItemInfo> taskPlanInfos;
    private LayoutInflater layoutInflater;

    public SpinnerAdapter(Context context, List<TaskPlanItemInfo> datas) {
        layoutInflater = LayoutInflater.from(context);
        taskPlanInfos = datas;
    }

    public void removeItem(int position) {
        if (taskPlanInfos != null && taskPlanInfos.size() > position) {
            taskPlanInfos.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return taskPlanInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return taskPlanInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.spinner_item, parent, false);
        TextView txt_spinner = (TextView) convertView.findViewById(R.id.txt_spinner);
        txt_spinner.setText(taskPlanInfos.get(position).getName());
        return convertView;
    }
}
