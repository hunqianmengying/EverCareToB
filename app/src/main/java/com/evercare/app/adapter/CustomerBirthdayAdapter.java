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
import com.evercare.app.model.BirthdayInfo;
import com.evercare.app.model.CustomerBirthdayInfo;
import com.evercare.app.model.CustomerItemClickListener;

import java.util.List;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 生日客户Adapter
 */
public class CustomerBirthdayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;

    private Context context;
    private CustomerBirthdayInfo customerBirthdayInfo;
    private CustomerItemClickListener customerItemClickListener;
    private RelativeLayout.LayoutParams layoutParams;

    public CustomerBirthdayAdapter(Context context, CustomerBirthdayInfo birthdayInfo, CustomerItemClickListener clickListener) {
        this.context = context;
        this.customerBirthdayInfo = birthdayInfo;
        this.customerItemClickListener = clickListener;
        layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
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

            int todayNum = customerBirthdayInfo.getBirthday() == null ? 0 : customerBirthdayInfo.getBirthday().size();
            int weekNum = customerBirthdayInfo.getBirthweek() == null ? 0 : customerBirthdayInfo.getBirthweek().size();

            BirthdayInfo birthdayInfo = null;
            if (position < todayNum) {
                birthdayInfo = customerBirthdayInfo.getBirthday().get(position);
                birthdayInfo.setDate_state("今日生日");
            } else if (position < (todayNum + weekNum)) {
                birthdayInfo = customerBirthdayInfo.getBirthweek().get(position - todayNum);
                birthdayInfo.setDate_state("本周生日");
            }

            recyclerViewHolder.tv_name.setText(birthdayInfo.getName());
            recyclerViewHolder.tv_birthday.setText(birthdayInfo.getBirthday());
            RelativeLayout.LayoutParams temLayoutParams = (RelativeLayout.LayoutParams)recyclerViewHolder.tv_birthday.getLayoutParams();
            temLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

            //recyclerViewHolder.tv_birthday.setLayoutParams(layoutParams);
            recyclerViewHolder.tv_phone.setText(birthdayInfo.getMobile());
            //PHP跟产品管管沟通过说去掉了
//            recyclerViewHolder.tv_state.setVisibility(TextUtils.equals(birthdayInfo.getConnect_state(), "1") ? View.INVISIBLE : View.VISIBLE);

            if (position == 0) {
                recyclerViewHolder.tvStickyHeader.setVisibility(View.VISIBLE);
                recyclerViewHolder.tvStickyHeader.setText(birthdayInfo.getDate_state());
                recyclerViewHolder.itemView.setTag(FIRST_STICKY_VIEW);
            } else {
                BirthdayInfo beforeItem = null;

                int temNum = position - 1;
                if (temNum < todayNum) {
                    beforeItem = customerBirthdayInfo.getBirthday().get(temNum);
                } else if (temNum < (todayNum + weekNum)) {
                    beforeItem = customerBirthdayInfo.getBirthweek().get(temNum - todayNum);
                }

                if (!TextUtils.equals(birthdayInfo.getDate_state(), beforeItem.getDate_state())) {
                    recyclerViewHolder.tvStickyHeader.setVisibility(View.VISIBLE);
                    recyclerViewHolder.tvStickyHeader.setText(birthdayInfo.getDate_state());
                    recyclerViewHolder.itemView.setTag(HAS_STICKY_VIEW);
                } else {
                    recyclerViewHolder.tvStickyHeader.setVisibility(View.GONE);
                    recyclerViewHolder.itemView.setTag(NONE_STICKY_VIEW);
                }
            }
            recyclerViewHolder.itemView.setContentDescription(birthdayInfo.getDate_state());
        }
    }

    @Override
    public int getItemCount() {
        int todayNum = customerBirthdayInfo.getBirthday() == null ? 0 : customerBirthdayInfo.getBirthday().size();
        int weekNum = customerBirthdayInfo.getBirthweek() == null ? 0 : customerBirthdayInfo.getBirthweek().size();

        return todayNum + weekNum;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvStickyHeader;
        public TextView tv_name;
        public TextView tv_phone;
        public TextView tv_birthday;
        public TextView tv_state;
        private CustomerItemClickListener customerItemClickListener;

        public RecyclerViewHolder(View itemView, CustomerItemClickListener clickListener) {
            super(itemView);
            tvStickyHeader = (TextView) itemView.findViewById(R.id.tv_sticky_header_view);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
            tv_birthday = (TextView) itemView.findViewById(R.id.tv_birthday);
            tv_state = (TextView) itemView.findViewById(R.id.tv_state);
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
