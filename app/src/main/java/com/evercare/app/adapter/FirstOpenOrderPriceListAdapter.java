package com.evercare.app.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.OpenOrderPriceInfo;

import java.util.List;


/**
 * 作者：xlren on 2017-3-1 15:35
 * 邮箱：renxianliang@126.com
 * 今日已开单最外层显示
 */
public class FirstOpenOrderPriceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private List<OpenOrderPriceInfo> datas;
    private List<OpenOrderPriceInfo.Prodect> priceInfoList;
    private Context context;
    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;
    private LayoutInflater inflater;

    public FirstOpenOrderPriceListAdapter(Context context, List<OpenOrderPriceInfo> datas) {
        this.datas = datas;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.first_fllor_open_order_price_item, parent, false);
            return new FirstOpenOrderPriceListAdapter.PriceListViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FirstOpenOrderPriceListAdapter.PriceListViewHolder) {
            FirstOpenOrderPriceListAdapter.PriceListViewHolder priceListViewHolder = (FirstOpenOrderPriceListAdapter.PriceListViewHolder) holder;
            priceListViewHolder.data_time.setText(datas.get(position).getData_time());
            if (datas.get(position).getProduct() != null && datas.get(position).getProduct().size() > 0) {
                priceInfoList = datas.get(position).getProduct();
                OpenOrderPriceListAdapter adapter = new OpenOrderPriceListAdapter(context, priceInfoList, new CustomerItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            if (datas.get(position).getProduct().get(position).isSelected) {
                                priceInfoList.get(position).isSelected = false;
                                view.findViewById(R.id.rlv_price_list).setVisibility(View.GONE);
                                ImageView imageView = (ImageView) view.findViewById(R.id.expend_view);
                                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
                            } else {
                                if (priceInfoList.get(position).getPackAge() != null && priceInfoList.get(position).getPackAge().size() > 0) {
                                    priceInfoList.get(position).isSelected = true;
                                    view.findViewById(R.id.rlv_price_list).setVisibility(View.VISIBLE);
                                    ImageView imageView = (ImageView) view.findViewById(R.id.expend_view);
                                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_up));
                                }
                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                priceListViewHolder.rlv_price_list.setLayoutManager(new LinearLayoutManager(context));
                priceListViewHolder.rlv_price_list.setAdapter(adapter);
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;

    }

    public class PriceListViewHolder extends RecyclerView.ViewHolder {

        private TextView data_time;

        private RecyclerView rlv_price_list;


        public PriceListViewHolder(View itemView) {
            super(itemView);
            data_time = (TextView) itemView.findViewById(R.id.data_time);
            rlv_price_list = (RecyclerView) itemView.findViewById(R.id.rlv_price_list);
        }


    }





}
