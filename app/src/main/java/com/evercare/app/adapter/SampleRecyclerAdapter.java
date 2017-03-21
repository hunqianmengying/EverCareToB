package com.evercare.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ditclear.swipelayout.SwipeDragLayout;
import com.evercare.app.R;
import com.evercare.app.model.AppointmentItemInfo;
import com.evercare.app.model.ChildEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：LXQ on 2016-9-9 11:09
 * 邮箱：842202389@qq.com
 * 我的预约Item
 */
public class SampleRecyclerAdapter extends RecyclerView.Adapter<SampleRecyclerAdapter.ViewHolder> {

    private final int TYPE_AGREE = 1;
    private final int TYPE_CANCLE = 2;
    private final int TYPE_DELETE = 3;

    private List<AppointmentItemInfo> dataList;

    private Map<String, String> statueMap = new HashMap<String, String>();

    public SampleRecyclerAdapter(List<AppointmentItemInfo> list) {
        dataList = list;
        statueMap.put("0", "用户预约");
        statueMap.put("1", "待审核");
        statueMap.put("2", "已预约");
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final AppointmentItemInfo entity = dataList.get(position);

        holder.date_text.setText(entity.getAppointment_time());
        holder.name_text.setText(entity.getCustomer_name());
        holder.product_text.setText(entity.getAppointment_project());


        holder.state_text.setText(statueMap.get(entity.getAppointment_status()));

        //必须是false,否则...
        if (entity.isOpen) {
            holder.swipeLayout.smoothOpen(false);
        } else {
            holder.swipeLayout.smoothClose(false);
        }

        holder.swipeLayout.addListener(new SwipeDragLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeDragLayout layout) {
            }

            @Override
            public void onStartClose(SwipeDragLayout layout) {
            }

            @Override
            public void onUpdate(SwipeDragLayout layout, float offset) {
            }

            @Override
            public void onOpened(SwipeDragLayout layout) {
                entity.isOpen = true;
            }

            @Override
            public void onClosed(SwipeDragLayout layout) {
                entity.isOpen = false;
            }

            @Override
            public void onCancel(SwipeDragLayout layout) {
            }

            @Override
            public void onClick(SwipeDragLayout layout) {
            }
        });

        holder.cancle_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnSwipeClickListener != null) {
                    mOnSwipeClickListener.onChildClick(position, TYPE_CANCLE);
                }
            }
        });

        holder.agree_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnSwipeClickListener != null) {
                    mOnSwipeClickListener.onChildClick(position, TYPE_AGREE);
                }
            }
        });
        holder.delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnSwipeClickListener != null) {
                    mOnSwipeClickListener.onChildClick(position, TYPE_DELETE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView agree_text;
        private final TextView cancle_text;
        private final TextView delete_text;

        private final TextView name_text;
        private final TextView date_text;
        private final TextView product_text;
        private final TextView state_text;


        public final SwipeDragLayout swipeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            agree_text = (TextView) itemView.findViewById(R.id.agree_text);
            cancle_text = (TextView) itemView.findViewById(R.id.cancle_text);
            delete_text = (TextView) itemView.findViewById(R.id.delete_text);
            name_text = (TextView) itemView.findViewById(R.id.name_text);
            date_text = (TextView) itemView.findViewById(R.id.date_text);
            product_text = (TextView) itemView.findViewById(R.id.product_text);
            state_text = (TextView) itemView.findViewById(R.id.state_text);


            swipeLayout = (SwipeDragLayout) itemView.findViewById(R.id.sample);

        }
    }


    private OnSwipeClickListener mOnSwipeClickListener;

    public void setOnSwipeClickListener(OnSwipeClickListener onSwipeClickListener) {
        mOnSwipeClickListener = onSwipeClickListener;
    }

    public interface OnSwipeClickListener {
        public boolean onChildClick(int position, int type);
    }
}
