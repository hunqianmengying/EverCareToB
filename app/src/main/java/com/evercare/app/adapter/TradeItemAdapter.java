package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.AchievmentInfo;

import java.util.List;

import static com.evercare.app.util.Constant.LOADING_MORE;
import static com.evercare.app.util.Constant.NO_LOAD_MORE;
import static com.evercare.app.util.Constant.PULLUP_LOAD_MORE;


/**
 * 作者：LXQ on 2016-11-16 11:22
 * 邮箱：842202389@qq.com
 * 老客
 */
public class TradeItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<AchievmentInfo.SaleList> tradeItemInfoList;
    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_HEADER = 2;
    private LayoutInflater inflater;
    private AchievmentInfo mAchievmentInfo;
    private String mAdapterType;//日、周、月

    public TradeItemAdapter(Context context, List<AchievmentInfo.SaleList> datas, AchievmentInfo achievmentInfo, String adapterType) {
        this.tradeItemInfoList = datas;
        inflater = LayoutInflater.from(context);
        mAchievmentInfo = achievmentInfo;
        mAdapterType = adapterType;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.trade_rate_item, parent, false);
            return new TradeItemAdapter.TradeItemAViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = inflater.inflate(R.layout.load_more_footview_layout, parent, false);
            return new TradeItemAdapter.FooterViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            View itemView = inflater.inflate(R.layout.header_fragment_achievment, parent, false);
            return new TradeItemAdapter.HeaderViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TradeItemAdapter.TradeItemAViewHolder) {
            TradeItemAdapter.TradeItemAViewHolder dradeItemAViewHolder = (TradeItemAdapter.TradeItemAViewHolder) holder;
            dradeItemAViewHolder.txt_project_name.setText(tradeItemInfoList.get(position).getProduct_name());
            dradeItemAViewHolder.txt_project_price.setText(tradeItemInfoList.get(position).getPrice());
            dradeItemAViewHolder.txt_customer_name.setText(tradeItemInfoList.get(position).getCustom_name());
            dradeItemAViewHolder.txt_no_deal.setVisibility(View.GONE);
        } else if (holder instanceof TradeItemAdapter.FooterViewHolder) {
            TradeItemAdapter.FooterViewHolder footerViewHolder = (TradeItemAdapter.FooterViewHolder) holder;
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
        } else if (holder instanceof TradeItemAdapter.HeaderViewHolder) {
            TradeItemAdapter.HeaderViewHolder headerViewHolder = (TradeItemAdapter.HeaderViewHolder) holder;
            headerViewHolder.sales_volume.setText(mAchievmentInfo.getSale_count());
            headerViewHolder.received_volume.setText(mAchievmentInfo.getCash());
            if ("月".equals(mAdapterType)) {
                headerViewHolder.target_volume_tem.setVisibility(View.VISIBLE);
                headerViewHolder.target_volume.setVisibility(View.VISIBLE);
                headerViewHolder.target_volume.setText(mAchievmentInfo.getTarget());
            } else {
                headerViewHolder.target_volume_tem.setVisibility(View.GONE);
                headerViewHolder.target_volume.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return tradeItemInfoList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
       /* if (position == 0) {
            return TYPE_HEADER;
        } else*/
        if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public class TradeItemAViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_customer_name;
        private TextView txt_no_deal;
        private TextView txt_project_name;
        private TextView txt_project_price;

        public TradeItemAViewHolder(View itemView) {
            super(itemView);
            txt_customer_name = (TextView) itemView.findViewById(R.id.txt_customer_name);
            txt_project_name = (TextView) itemView.findViewById(R.id.txt_project_name);
            txt_project_price = (TextView) itemView.findViewById(R.id.txt_project_price);
            txt_no_deal = (TextView) itemView.findViewById(R.id.txt_no_deal);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

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

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView sales_volume;
        private TextView received_volume;
        private TextView target_volume_tem;
        private TextView target_volume;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            sales_volume = (TextView) itemView.findViewById(R.id.sales_volume);
            received_volume = (TextView) itemView.findViewById(R.id.received_volume);
            target_volume_tem = (TextView) itemView.findViewById(R.id.target_volume_tem);
            target_volume = (TextView) itemView.findViewById(R.id.target_volume);
        }
    }


    public void addFooterItem(List<AchievmentInfo.SaleList> items) {
        tradeItemInfoList.addAll(items);
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
