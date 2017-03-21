package com.evercare.app.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
 * 作者：xlren on 2017-2-27 13:56
 * 邮箱：renxianliang@126.com
 * 今日已开单列表详情
 */
public class OpenOrderPriceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private List<OpenOrderPriceInfo.Prodect> datas;
    private Context context;

    private LayoutInflater inflater;
    private CustomerItemClickListener mItemClickListener;

    public OpenOrderPriceListAdapter(Context context, List<OpenOrderPriceInfo.Prodect> datas, CustomerItemClickListener itemClickListener) {
        this.datas = datas;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.open_order_price_item, parent, false);
            return new OpenOrderPriceListAdapter.PriceListViewHolder(view, mItemClickListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OpenOrderPriceListAdapter.PriceListViewHolder) {
            OpenOrderPriceListAdapter.PriceListViewHolder priceListViewHolder = (OpenOrderPriceListAdapter.PriceListViewHolder) holder;
            priceListViewHolder.txt_price.setText(datas.get(position).getProduct_price());
            priceListViewHolder.txt_price_name.setText(datas.get(position).getProduct_name());
            priceListViewHolder.count_text.setText("数量:"+datas.get(position).getQuantity());
            if(position+1 == datas.size()){
                priceListViewHolder.line1.setVisibility(View.GONE);
            }else{
                priceListViewHolder.line1.setVisibility(View.VISIBLE);
            }
            if(!TextUtils.isEmpty(datas.get(position).getDescription())){
                priceListViewHolder.description.setText("备注:"+datas.get(position).getDescription());
                priceListViewHolder.description.setVisibility(View.VISIBLE);
            }else{
                priceListViewHolder.description.setVisibility(View.GONE);
            }

            if (datas.get(position).isSelected) {
                priceListViewHolder.rlv_price_list.setVisibility(View.VISIBLE);
                priceListViewHolder.expend_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_up));
            } else {
                priceListViewHolder.rlv_price_list.setVisibility(View.GONE);
                priceListViewHolder.expend_view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
            }

            if (datas.get(position).getPackAge() != null && datas.get(position).getPackAge().size() > 0) {
                priceListViewHolder.expend_view.setVisibility(View.VISIBLE);
                OpenOrderPriceSubListAdapter adapter = new OpenOrderPriceSubListAdapter(context, R.layout.price_sub_item, datas.get(position).getPackAge());
                priceListViewHolder.rlv_price_list.setLayoutManager(new LinearLayoutManager(context));
                priceListViewHolder.rlv_price_list.setAdapter(adapter);
            } else {
                priceListViewHolder.expend_view.setVisibility(View.INVISIBLE);
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

    public class PriceListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txt_price;
        private TextView txt_price_name;
        private ImageView expend_view;
        private RecyclerView rlv_price_list;
        private TextView count_text;
        private TextView description;
        private View line1;
        private CustomerItemClickListener mItemClickListener;

        public PriceListViewHolder(View itemView, CustomerItemClickListener itemClickListener) {
            super(itemView);
            txt_price = (TextView) itemView.findViewById(R.id.txt_price);
            txt_price_name = (TextView) itemView.findViewById(R.id.txt_price_name);
            expend_view = (ImageView) itemView.findViewById(R.id.expend_view);
            count_text =(TextView) itemView.findViewById(R.id.count_text);
            description = (TextView) itemView.findViewById(R.id.description);
            line1 = itemView.findViewById(R.id.line1);
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








}
