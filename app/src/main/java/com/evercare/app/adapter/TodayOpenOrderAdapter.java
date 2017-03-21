package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.OpenOrderItemInfo;
import com.evercare.app.model.TodayOpenOrderInfo;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 今日可开单Adapter
 */
public class TodayOpenOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;

    private Context context;
    private TodayOpenOrderInfo maturingCustomerInfo;
    private CustomerItemClickListener customerItemClickListener;

    public TodayOpenOrderAdapter(Context context, TodayOpenOrderInfo customerInfo, CustomerItemClickListener clickListener) {
        this.context = context;
        this.maturingCustomerInfo = customerInfo;
        this.customerItemClickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.diagnosis_item, parent, false);
        return new RecyclerViewHolder(view, customerItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof RecyclerViewHolder) {
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) viewHolder;

            int todayNum = maturingCustomerInfo.getOpenorder() == null ? 0 : maturingCustomerInfo.getOpenorder().size();
            int weekNum = maturingCustomerInfo.getFinshopenorder() == null ? 0 : maturingCustomerInfo.getFinshopenorder().size();

            OpenOrderItemInfo openOrderItemInfo = null;
            if (position < todayNum) {
                openOrderItemInfo = maturingCustomerInfo.getOpenorder().get(position);
                openOrderItemInfo.setDate_state("今日");
            } else if (position < (todayNum + weekNum)) {
                openOrderItemInfo = maturingCustomerInfo.getFinshopenorder().get(position - todayNum);
                openOrderItemInfo.setDate_state("已完成任务");
            }

            recyclerViewHolder.txt_customer_name.setText(openOrderItemInfo.getCustom_name());
            switch (openOrderItemInfo.getType()) {
                case "1":
                    recyclerViewHolder.txt_customer_type.setText("初诊");
                    recyclerViewHolder.txt_billing.setVisibility(View.INVISIBLE);
                    break;
                case "2":
                    recyclerViewHolder.txt_customer_type.setText("复诊");
                    break;
                case "3":
                    recyclerViewHolder.txt_customer_type.setText("复查");
                    break;
                case "4":
                    recyclerViewHolder.txt_customer_type.setText("再消费");
                    break;
                case "5":
                    recyclerViewHolder.txt_customer_type.setText("其他");
                    break;
                case "6":
                    recyclerViewHolder.txt_customer_type.setText("治疗");
                    break;
            }

            if (!TextUtils.isEmpty(openOrderItemInfo.getProject_class_name())) {
                recyclerViewHolder.txt_project_name.setText(openOrderItemInfo.getProject_class_name());
                recyclerViewHolder.txt_project_name.setVisibility(View.VISIBLE);
            } else {
                recyclerViewHolder.txt_project_name.setVisibility(View.INVISIBLE);
            }

            if (position == 0) {
                recyclerViewHolder.tvStickyHeader.setVisibility(View.VISIBLE);
                recyclerViewHolder.tvStickyHeader.setText(openOrderItemInfo.getDate_state());
                recyclerViewHolder.itemView.setTag(FIRST_STICKY_VIEW);
            } else {
                OpenOrderItemInfo beforeItem = null;

                int temNum = position - 1;
                if (temNum < todayNum) {
                    beforeItem = maturingCustomerInfo.getOpenorder().get(temNum);
                } else if (temNum < (todayNum + weekNum)) {
                    beforeItem = maturingCustomerInfo.getFinshopenorder().get(temNum - todayNum);
                }

                if (!TextUtils.equals(openOrderItemInfo.getDate_state(), beforeItem.getDate_state())) {
                    recyclerViewHolder.tvStickyHeader.setVisibility(View.VISIBLE);
                    recyclerViewHolder.tvStickyHeader.setText(openOrderItemInfo.getDate_state());
                    recyclerViewHolder.itemView.setTag(HAS_STICKY_VIEW);
                } else {
                    recyclerViewHolder.tvStickyHeader.setVisibility(View.GONE);
                    recyclerViewHolder.itemView.setTag(NONE_STICKY_VIEW);
                }
            }
            recyclerViewHolder.itemView.setContentDescription(openOrderItemInfo.getDate_state());
        }
    }

    @Override
    public int getItemCount() {
        int todayNum = maturingCustomerInfo.getOpenorder() == null ? 0 : maturingCustomerInfo.getOpenorder().size();
        int weekNum = maturingCustomerInfo.getFinshopenorder() == null ? 0 : maturingCustomerInfo.getFinshopenorder().size();

        return todayNum + weekNum;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvStickyHeader;
        public TextView txt_customer_name;
        public TextView txt_customer_type;//初诊/复诊
        public TextView txt_billing;//是否开单
        public TextView txt_project_name;//项目名称
        private CustomerItemClickListener customerItemClickListener;

        public RecyclerViewHolder(View itemView, CustomerItemClickListener clickListener) {
            super(itemView);
            tvStickyHeader = (TextView) itemView.findViewById(R.id.tv_sticky_header_view);
            txt_customer_name = (TextView) itemView.findViewById(R.id.txt_customer_name);
            txt_customer_type = (TextView) itemView.findViewById(R.id.txt_customer_type);
            txt_billing = (TextView) itemView.findViewById(R.id.txt_billing);
            txt_billing.setOnClickListener(this);
            txt_project_name = (TextView) itemView.findViewById(R.id.txt_project_name);
            this.customerItemClickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (customerItemClickListener != null) {
                customerItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

}
