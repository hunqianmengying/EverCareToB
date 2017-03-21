package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evercare.app.R;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-2 16:55
 * 邮箱：842202389@qq.com
 * 最近搜索的客户名称
 */
public class RecentCustomerAdapter extends RecyclerView.Adapter<RecentCustomerAdapter.ViewHolder> {
    private Context context;
    private List<String> personList;
    private LayoutInflater inflater;

    public RecentCustomerAdapter(Context context, List<String> list) {
        this.context = context;
        personList = list;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public RecentCustomerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recent_customer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecentCustomerAdapter.ViewHolder holder, int position) {
        holder.txt_recent_person.setText(personList.get(position));
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_recent_person;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_recent_person = (TextView) itemView.findViewById(R.id.txt_recent_person);

        }
    }
}
