package com.evercare.app.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.ActivityItemInfo;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.util.Constant;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import static com.evercare.app.util.Constant.LOADING_MORE;
import static com.evercare.app.util.Constant.NO_LOAD_MORE;
import static com.evercare.app.util.Constant.PULLUP_LOAD_MORE;

/**
 * 作者：LXQ on 2016-9-26 16:06
 * 邮箱：842202389@qq.com
 * 活动信息类Adapter
 */
public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.HeaderViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<ActivityItemInfo> activityItemInfos;
    private CustomerItemClickListener itemClickListener;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_HEADER = 2;
    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    public ActivityListAdapter(Context context, List<ActivityItemInfo> list, CustomerItemClickListener itemClickListener) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        activityItemInfos = list;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TYPE_HEADER;
        }else if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public HeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View header = new View(context);
            header.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) context.getResources().getDimension(R.dimen.preference_header_height)));
            return new ActivityListAdapter.HeaderViewHolder(header);
        } else if (viewType == TYPE_ITEM) {
            View view = layoutInflater.inflate(R.layout.preferenceitem_layout, parent, false);
            return new ActivityListAdapter.ViewHolder(view, itemClickListener);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = layoutInflater.inflate(R.layout.load_more_footview_layout, parent, false);
            return new ActivityListAdapter.FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(HeaderViewHolder holder, int position) {
        if (holder instanceof ActivityListAdapter.ViewHolder) {
            ActivityItemInfo itemInfo = activityItemInfos.get(position - 1);
            if (holder instanceof ActivityListAdapter.ViewHolder) {
                Uri uri = Uri.parse(itemInfo.getImage());
                ((ActivityListAdapter.ViewHolder) holder).simpleimage.setImageURI(Constant.BASEURL_IMG +uri);
                ((ActivityListAdapter.ViewHolder) holder).txtContent.setText(itemInfo.getTitle());
            }
        } else if (holder instanceof ActivityListAdapter.FooterViewHolder) {
            ActivityListAdapter.FooterViewHolder footerViewHolder = (ActivityListAdapter.FooterViewHolder) holder;
            switch (mLoadMoreStatus) {
                case PULLUP_LOAD_MORE:
                    footerViewHolder.mPbLoad.setVisibility(View.VISIBLE);
                    footerViewHolder.mTvLoadText.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footerViewHolder.mPbLoad.setVisibility(View.VISIBLE);
                    footerViewHolder.mTvLoadText.setText("正加载更多...");
                    break;
                case NO_LOAD_MORE:
                    //隐藏加载更多
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);
                    footerViewHolder.mTvLoadText.setText("没有更多了...");
                    break;

            }
        }
    }

    @Override
    public int getItemCount() {
        return activityItemInfos.size() + 2;
    }

    public class ViewHolder extends HeaderViewHolder implements View.OnClickListener {

        public TextView txtContent;
        //public ImageView imgContent;
        public SimpleDraweeView simpleimage;
        private CustomerItemClickListener itemClickListener;

        public ViewHolder(View itemView, CustomerItemClickListener itemClickListener) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.txtContent);
            simpleimage = (SimpleDraweeView) itemView.findViewById(R.id.simpleimage);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition() - 1);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);

        }
    }


    public class FooterViewHolder extends ActivityListAdapter.HeaderViewHolder {

        private ProgressBar mPbLoad;

        private TextView mTvLoadText;

        private LinearLayout mLoadLayout;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mPbLoad = (ProgressBar) itemView.findViewById(R.id.pbLoad);
            mTvLoadText = (TextView) itemView.findViewById(R.id.tvLoadText);
            mLoadLayout = (LinearLayout) itemView.findViewById(R.id.loadLayout);
        }
    }


    public void addFooterItem(List<ActivityItemInfo> items) {
        activityItemInfos.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        mLoadMoreStatus = status;
        notifyDataSetChanged();
    }
}
