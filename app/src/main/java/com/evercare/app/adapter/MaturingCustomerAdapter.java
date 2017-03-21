package com.evercare.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.BirthdayInfo;
import com.evercare.app.model.CustomerBirthdayInfo;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.MaturingCustomerInfo;
import com.evercare.app.model.MaturingItemInfo;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 即将到期回公海客户Adapter
 */
public class MaturingCustomerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;

    private Context context;
    private MaturingCustomerInfo maturingCustomerInfo;
    private CustomerItemClickListener customerItemClickListener;

    public MaturingCustomerAdapter(Context context, MaturingCustomerInfo customerInfo, CustomerItemClickListener clickListener) {
        this.context = context;
        this.maturingCustomerInfo = customerInfo;
        this.customerItemClickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_birthday_item, parent, false);
        return new RecyclerViewHolder(view, customerItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof RecyclerViewHolder) {
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) viewHolder;

            int todayNum = maturingCustomerInfo.getTmplist() == null ? 0 : maturingCustomerInfo.getTmplist().size();
            int weekNum = maturingCustomerInfo.getPrivatelist() == null ? 0 : maturingCustomerInfo.getPrivatelist().size();

            MaturingItemInfo maturingItemInfo = null;
            if (position < todayNum) {
                maturingItemInfo = maturingCustomerInfo.getTmplist().get(position);
                maturingItemInfo.setDate_state("临时库");
            } else if (position < (todayNum + weekNum)) {
                maturingItemInfo = maturingCustomerInfo.getPrivatelist().get(position - todayNum);
                maturingItemInfo.setDate_state("私海");
            }

            recyclerViewHolder.tv_name.setText(maturingItemInfo.getName());
            switch (maturingItemInfo.getStatus()) {
                case "1":
                    recyclerViewHolder.tv_follow_status.setText("未跟进&未消费");
                    break;
                case "2":
                    recyclerViewHolder.tv_follow_status.setText("未跟进");
                    break;
                case "3":
                    recyclerViewHolder.tv_follow_status.setText("未消费");
                    break;
            }
            recyclerViewHolder.tv_phone.setText(maturingItemInfo.getMobile());
            recyclerViewHolder.tv_day_num.setVisibility(View.VISIBLE);
            recyclerViewHolder.tv_day_num.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            recyclerViewHolder.tv_day_num.setText("还剩：" + maturingItemInfo.getDay() + "天");

            if (position == 0) {
                recyclerViewHolder.tvStickyHeader.setVisibility(View.VISIBLE);
                recyclerViewHolder.tvStickyHeader.setText(maturingItemInfo.getDate_state());
                recyclerViewHolder.itemView.setTag(FIRST_STICKY_VIEW);
            } else {
                MaturingItemInfo beforeItem = null;

                int temNum = position - 1;
                if (temNum < todayNum) {
                    beforeItem = maturingCustomerInfo.getTmplist().get(temNum);
                } else if (temNum < (todayNum + weekNum)) {
                    beforeItem = maturingCustomerInfo.getPrivatelist().get(temNum - todayNum);
                }

                if (!TextUtils.equals(maturingItemInfo.getDate_state(), beforeItem.getDate_state())) {
                    recyclerViewHolder.tvStickyHeader.setVisibility(View.VISIBLE);
                    recyclerViewHolder.tvStickyHeader.setText(maturingItemInfo.getDate_state());
                    recyclerViewHolder.itemView.setTag(HAS_STICKY_VIEW);
                } else {
                    recyclerViewHolder.tvStickyHeader.setVisibility(View.GONE);
                    recyclerViewHolder.itemView.setTag(NONE_STICKY_VIEW);
                }
            }
            recyclerViewHolder.itemView.setContentDescription(maturingItemInfo.getDate_state());
        }
    }

    @Override
    public int getItemCount() {
        int todayNum = maturingCustomerInfo.getTmplist() == null ? 0 : maturingCustomerInfo.getTmplist().size();
        int weekNum = maturingCustomerInfo.getPrivatelist() == null ? 0 : maturingCustomerInfo.getPrivatelist().size();

        return todayNum + weekNum;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvStickyHeader;
        public TextView tv_name;
        public TextView tv_phone;
        public TextView tv_follow_status;
        public TextView tv_day_num;
        private CustomerItemClickListener customerItemClickListener;

        public RecyclerViewHolder(View itemView, CustomerItemClickListener clickListener) {
            super(itemView);
            tvStickyHeader = (TextView) itemView.findViewById(R.id.tv_sticky_header_view);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
            tv_follow_status = (TextView) itemView.findViewById(R.id.tv_birthday);
            tv_day_num = (TextView) itemView.findViewById(R.id.tv_state);
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
