package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.TodayWorkInfo;
import com.evercare.app.model.TodayWorkItemInfo;
import com.evercare.app.model.WorkCalendarInfo;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 今日工作日历数据Adapter
 */
public class MonthWorkAdapter extends BaseRecyclerAdapter<TodayWorkItemInfo> {

    private List<TodayWorkItemInfo> data;

    public MonthWorkAdapter(Context context, int itemLayoutId, List<TodayWorkItemInfo> datas) {
        super(context, itemLayoutId, datas);
        this.data = datas;
    }

    @Override
    public void convert(BaseRecyclerHolder holder, TodayWorkItemInfo item, int position) {
        TodayWorkItemInfo todayWorkItemInfo = data.get(position);

        holder.setText(R.id.tv_name, todayWorkItemInfo.getCustom_name());
        holder.setText(R.id.tv_describe, todayWorkItemInfo.getDescribe());
        if (TextUtils.isEmpty(todayWorkItemInfo.getProject_class_name())) {
            holder.setVisible(R.id.tv_project_name, false);
        } else {
            holder.setVisible(R.id.tv_project_name, true);
            holder.setText(R.id.tv_project_name, todayWorkItemInfo.getProject_class_name());
        }
    }

    public void clearData() {
        if (this.data != null && this.data.size() > 0) {
            this.data.clear();
            notifyDataSetChanged();
        }
    }
}
