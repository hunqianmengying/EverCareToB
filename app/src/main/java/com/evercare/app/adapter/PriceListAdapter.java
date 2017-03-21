package com.evercare.app.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.PriceInfo;

import java.util.List;

import static com.evercare.app.util.Constant.LOADING_MORE;
import static com.evercare.app.util.Constant.NO_LOAD_MORE;
import static com.evercare.app.util.Constant.PULLUP_LOAD_MORE;

/**
 * 作者：LXQ on 2016-9-13 11:28
 * 邮箱：842202389@qq.com
 * 价目表Adapter
 */
public class PriceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private List<PriceInfo> datas;
    private Context context;
    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;
    private LayoutInflater inflater;
    private CustomerItemClickListener mItemClickListener;

    public PriceListAdapter(Context context, List<PriceInfo> datas, CustomerItemClickListener itemClickListener) {
        this.datas = datas;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.price_item, parent, false);
            return new PriceListAdapter.PriceListViewHolder(view, mItemClickListener);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = inflater.inflate(R.layout.load_more_footview_layout, parent, false);
            return new PriceListAdapter.FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PriceListAdapter.PriceListViewHolder) {
            PriceListAdapter.PriceListViewHolder priceListViewHolder = (PriceListAdapter.PriceListViewHolder) holder;
            priceListViewHolder.txt_price.setText(datas.get(position).getPrice());
            priceListViewHolder.txt_price_name.setText(datas.get(position).getName());
            if (datas.get(position).isSelected) {
                priceListViewHolder.rlv_price_list.setVisibility(View.VISIBLE);
                priceListViewHolder.expend_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_up));
            } else {
                priceListViewHolder.rlv_price_list.setVisibility(View.GONE);
                priceListViewHolder.expend_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
            }

            if (datas.get(position).getPackAge() != null && datas.get(position).getPackAge().size() > 0) {
                priceListViewHolder.expend_view.setVisibility(View.VISIBLE);
                PriceSubListAdapter adapter = new PriceSubListAdapter(context, R.layout.price_sub_item, datas.get(position).getPackAge());
                priceListViewHolder.rlv_price_list.setLayoutManager(new LinearLayoutManager(context));
                priceListViewHolder.rlv_price_list.setAdapter(adapter);
                //priceListViewHolder.rlv_price_list.setVisibility(View.VISIBLE);


            } else {
                priceListViewHolder.expend_view.setVisibility(View.INVISIBLE);
                //priceListViewHolder.rlv_price_list.setVisibility(View.GONE);
            }
        } else if (holder instanceof PriceListAdapter.FooterViewHolder) {
            PriceListAdapter.FooterViewHolder footerViewHolder = (PriceListAdapter.FooterViewHolder) holder;
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

    public class PriceListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txt_price;
        private TextView txt_price_name;
        private ImageView expend_view;
        private RecyclerView rlv_price_list;
        private CustomerItemClickListener mItemClickListener;

        public PriceListViewHolder(View itemView, CustomerItemClickListener itemClickListener) {
            super(itemView);
            txt_price = (TextView) itemView.findViewById(R.id.txt_price);
            txt_price_name = (TextView) itemView.findViewById(R.id.txt_price_name);
            expend_view = (ImageView) itemView.findViewById(R.id.expend_view);
            expend_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rlv_price_list.getVisibility() == View.VISIBLE) {
                        rlv_price_list.setVisibility(View.GONE);
                        expend_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
                    } else {
                        rlv_price_list.setVisibility(View.VISIBLE);
                        expend_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_up));
                    }
                }
            });
            rlv_price_list = (RecyclerView) itemView.findViewById(R.id.rlv_price_list);
            this.mItemClickListener = itemClickListener;
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getAdapterPosition());
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

    public void addFooterItem(List<PriceInfo> items) {
        datas.addAll(items);
        notifyDataSetChanged();
    }


    public void initData(List<PriceInfo> items) {
        datas.clear();
        datas.addAll(items);
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
