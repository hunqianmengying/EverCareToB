package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ditclear.swipelayout.SwipeDragLayout;
import com.evercare.app.R;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.TodayWorkInfo;
import com.evercare.app.model.TodayWorkItemInfo;


/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 生日客户Adapter
 */
public class TodayWorkNewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;

    private Context context;
    private TodayWorkInfo todayWorkInfo;
    private CustomerItemClickListener customerItemClickListener;
    private RelativeLayout.LayoutParams layoutParams;

    public TodayWorkNewAdapter(Context context, TodayWorkInfo todayWorkInfo, CustomerItemClickListener clickListener) {
        this.context = context;
        this.todayWorkInfo = todayWorkInfo;
        this.customerItemClickListener = clickListener;

        layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.today_task_item, parent, false);
        return new RecyclerViewHolder(view, customerItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof RecyclerViewHolder) {
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) viewHolder;

            int todayNum = todayWorkInfo.getDaydata() == null ? 0 : todayWorkInfo.getDaydata().size();
            int staleNum = todayWorkInfo.getStaledata() == null ? 0 : todayWorkInfo.getStaledata().size();
            int doneNum = todayWorkInfo.getFinishdata() == null ? 0 : todayWorkInfo.getFinishdata().size();
            TodayWorkItemInfo itemInfo = null;
            if (position < todayNum) {
                itemInfo = todayWorkInfo.getDaydata().get(position);
                itemInfo.setDate_state("今日");
                recyclerViewHolder.tv_time.setVisibility(View.GONE);
                recyclerViewHolder.tv_name.setLayoutParams(layoutParams);

            } else if (position < (todayNum + staleNum)) {
                itemInfo = todayWorkInfo.getStaledata().get(position - todayNum);
                itemInfo.setDate_state("已过期");
            } else if (position < (todayNum + staleNum + doneNum)) {
                itemInfo = todayWorkInfo.getFinishdata().get(position - todayNum - staleNum);
                itemInfo.setDate_state("已完成");
            }

            recyclerViewHolder.tv_name.setText(itemInfo.getCustom_name());
            recyclerViewHolder.tv_time.setText(itemInfo.getStart_time());
            recyclerViewHolder.tv_describe.setText(itemInfo.getDescribe());
            if (TextUtils.isEmpty(itemInfo.getProject_class_name())) {
                recyclerViewHolder.tv_project_name.setVisibility(View.GONE);
            } else {
                recyclerViewHolder.tv_project_name.setVisibility(View.VISIBLE);
                recyclerViewHolder.tv_project_name.setText(itemInfo.getProject_class_name());
            }

            if (position == 0) {
                recyclerViewHolder.tvStickyHeader.setVisibility(View.VISIBLE);
                recyclerViewHolder.tvStickyHeader.setText(itemInfo.getDate_state());
                recyclerViewHolder.itemView.setTag(FIRST_STICKY_VIEW);
            } else {
                TodayWorkItemInfo beforeItem = null;

                int temNum = position - 1;
                if (temNum < todayNum) {
                    beforeItem = todayWorkInfo.getDaydata().get(temNum);
                } else if (temNum < (todayNum + staleNum)) {
                    beforeItem = todayWorkInfo.getStaledata().get(temNum - todayNum);
                } else {
                    beforeItem = todayWorkInfo.getFinishdata().get(temNum - todayNum - staleNum);
                }

                if (!TextUtils.equals(itemInfo.getDate_state(), beforeItem.getDate_state())) {
                    recyclerViewHolder.tvStickyHeader.setVisibility(View.VISIBLE);
                    recyclerViewHolder.tvStickyHeader.setText(itemInfo.getDate_state());
                    recyclerViewHolder.itemView.setTag(HAS_STICKY_VIEW);
                } else {
                    recyclerViewHolder.tvStickyHeader.setVisibility(View.GONE);
                    recyclerViewHolder.itemView.setTag(NONE_STICKY_VIEW);
                }
            }
            recyclerViewHolder.itemView.setContentDescription(itemInfo.getDate_state());
        }
    }

    @Override
    public int getItemCount() {
        int todayNum = todayWorkInfo.getDaydata() == null ? 0 : todayWorkInfo.getDaydata().size();
        int staleNum = todayWorkInfo.getStaledata() == null ? 0 : todayWorkInfo.getStaledata().size();
        int doneNum = todayWorkInfo.getFinishdata() == null ? 0 : todayWorkInfo.getFinishdata().size();

        return todayNum + staleNum + doneNum;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvStickyHeader;
        public TextView tv_name;
        public TextView tv_time;
        public TextView tv_describe;
        public TextView tv_project_name;
        private CustomerItemClickListener customerItemClickListener;
        private SwipeDragLayout sample;
        private TextView txt_edit_time;
        private TextView txt_delete;

        private RelativeLayout rl_content_wrapper;

        public RecyclerViewHolder(View itemView, CustomerItemClickListener clickListener) {
            super(itemView);
            tvStickyHeader = (TextView) itemView.findViewById(R.id.tv_sticky_header_view);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_describe = (TextView) itemView.findViewById(R.id.tv_describe);
            tv_project_name = (TextView) itemView.findViewById(R.id.tv_project_name);
            sample = (SwipeDragLayout) itemView.findViewById(R.id.sample);
            txt_edit_time = (TextView) itemView.findViewById(R.id.txt_edit_time);
            txt_delete = (TextView) itemView.findViewById(R.id.txt_delete);
            rl_content_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_content_wrapper);
            this.customerItemClickListener = clickListener;
            tv_name.setOnClickListener(this);
            txt_delete.setOnClickListener(this);
            txt_edit_time.setOnClickListener(this);
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
