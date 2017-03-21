package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.AlreadyOpenOrderInfo;
import com.evercare.app.model.CustomerItemClickListener;

import java.util.List;

/**
 * 作者：xlren on 2017-2-24 14:10
 * 邮箱：renxianliang@126.com
 * 今日已开单人员列表
 */
public class AlreadyOpenOrderAdapter extends RecyclerView.Adapter<AlreadyOpenOrderAdapter.ViewHolder> {
    private Context context;
    private List<AlreadyOpenOrderInfo> caseItemInfos;
    private CustomerItemClickListener itemClickListener;


    public AlreadyOpenOrderAdapter(Context context, List<AlreadyOpenOrderInfo> list, CustomerItemClickListener itemClickListener) {
        this.context = context;
        caseItemInfos = list;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public AlreadyOpenOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.already_open_order_item_layout, parent, false);
        return new ViewHolder(view, itemClickListener);
    }


    @Override
    public void onBindViewHolder(AlreadyOpenOrderAdapter.ViewHolder viewHolder, int position) {
        viewHolder.txtContent.setText(caseItemInfos.get(position).getCustom_name());
        viewHolder.txt_project_name.setText(caseItemInfos.get(position).getProject_class_name());
        switch (caseItemInfos.get(position).getType()) {
            case "1":
                viewHolder.txt_customer_type.setText("初诊");
                break;
            case "2":
                viewHolder.txt_customer_type.setText("复诊");
                break;
            case "3":
                viewHolder.txt_customer_type.setText("复查");
                break;
            case "4":
                viewHolder.txt_customer_type.setText("再消费");
                break;
            case "5":
                viewHolder.txt_customer_type.setText("其他");
                break;
            case "6":
                viewHolder.txt_customer_type.setText("治疗");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return caseItemInfos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtContent;
        public TextView txt_customer_type;
        public TextView txt_project_name;
        private CustomerItemClickListener itemClickListener;

        public ViewHolder(View itemView, CustomerItemClickListener clickListener) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.txtContent);
            txt_customer_type = (TextView) itemView.findViewById(R.id.txt_customer_type);
            txt_project_name = (TextView) itemView.findViewById(R.id.txt_project_name);
            this.itemClickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

}
