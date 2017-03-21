package com.evercare.app.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.evercare.app.R;
import com.evercare.app.model.AchievmentInfo;
import com.evercare.app.model.TradeItemInfo;
import com.evercare.app.model.TradeRatesInfo;

import java.util.List;


/**
 * 作者：LXQ on 2016-11-16 11:22
 * 邮箱：842202389@qq.com
 * Javadoc
 */
public class CustomerTradeItemAdapter extends BaseRecyclerAdapter<TradeRatesInfo.DealTradeInfo> {

    private List<TradeRatesInfo.DealTradeInfo> tradeItemInfoList;

    public CustomerTradeItemAdapter(Context context, int itemLayoutId, List<TradeRatesInfo.DealTradeInfo> datas) {
        super(context, itemLayoutId, datas);
        this.tradeItemInfoList = datas;
    }

    @Override
    public void convert(BaseRecyclerHolder holder, TradeRatesInfo.DealTradeInfo item, int position) {
        TradeRatesInfo.DealTradeInfo tradeItemInfo = tradeItemInfoList.get(position);
        holder.setText(R.id.txt_customer_name, tradeItemInfo.getCustom_name());
        holder.setText(R.id.txt_time, tradeItemInfo.getDay());

        if (tradeItemInfo.getIs_consumption() != 1) {
            holder.setVisible(R.id.txt_project_name, false);
            holder.setVisible(R.id.txt_project_price, false);
            holder.setVisible(R.id.txt_unit, false);
            holder.setVisible(R.id.txt_no_deal, true);
        } else {
            holder.setVisible(R.id.txt_no_deal, false);
            holder.setVisible(R.id.txt_project_name, true);
            holder.setVisible(R.id.txt_project_price, true);
            holder.setVisible(R.id.txt_unit, true);
            holder.setText(R.id.txt_project_name, tradeItemInfo.getProduct_name());
            holder.setText(R.id.txt_project_price, tradeItemInfo.getPrice());
        }
    }
}
