package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.OrderProjectInfo;
import com.evercare.app.model.PriceInfo;
import com.evercare.app.model.SaleCureItemInfo;
import com.evercare.app.util.DateTool;

import java.util.List;

import static com.evercare.app.util.Constant.LOADING_MORE;
import static com.evercare.app.util.Constant.NO_LOAD_MORE;
import static com.evercare.app.util.Constant.PULLUP_LOAD_MORE;

/**
 * 作者：LXQ on 2016-11-28 14:50
 * 邮箱：842202389@qq.com
 * 订购项目表Adapter
 */
public class OrderProjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;


    private List<OrderProjectInfo> datas;
    private Context context;
    private LayoutInflater inflater;
    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        mLoadMoreStatus = status;
        notifyDataSetChanged();
    }

    public OrderProjectAdapter(Context context, List<OrderProjectInfo> datas) {
        this.datas = datas;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void initData(List<OrderProjectInfo> items) {
        datas.clear();
        datas.addAll(items);
        notifyDataSetChanged();
    }


    public void addFooterItem(List<OrderProjectInfo> items) {
        datas.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.order_project_item, parent, false);
            return new OrderProjectItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = inflater.inflate(R.layout.load_more_footview_layout, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof OrderProjectItemViewHolder) {
            OrderProjectInfo orderProjectInfo = datas.get(position);
            OrderProjectItemViewHolder holder = (OrderProjectItemViewHolder) viewHolder;

            holder.txt_project_name.setText(orderProjectInfo.getProduct_name());
            try {
                holder.txt_time.setText(DateTool.dateToString(DateTool.stringTodate(orderProjectInfo.getBuy_time(), "yyyy-MM-dd"), "yyyy-MM-dd"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.txt_buy_num.setText(orderProjectInfo.getBuy_num());
            holder.txt_residue_num.setText(orderProjectInfo.getResidue_num());
            holder.txt_pay.setText(orderProjectInfo.getPay());
        } else if (viewHolder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
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
        return datas == null ? 0 : datas.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public class OrderProjectItemViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_project_name;
        public TextView txt_time;
        public TextView txt_buy_num;
        public TextView txt_residue_num;
        public TextView txt_pay;

        public OrderProjectItemViewHolder(View itemView) {
            super(itemView);
            txt_project_name = (TextView) itemView.findViewById(R.id.txt_project_name);
            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            txt_buy_num = (TextView) itemView.findViewById(R.id.txt_buy_num);
            txt_residue_num = (TextView) itemView.findViewById(R.id.txt_residue_num);
            txt_pay = (TextView) itemView.findViewById(R.id.txt_pay);
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
}
