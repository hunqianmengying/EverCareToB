package com.evercare.app.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.evercare.app.R;
import com.evercare.app.model.OpenOrderPriceInfo;
import com.evercare.app.model.PriceInfo;

import java.util.List;

/**
 * 作者：xlren on 2017-3-1 15:11
 * 邮箱：renxianliang@126.com
 * 今日已开单详情套餐
 */
public class OpenOrderPriceSubListAdapter extends BaseRecyclerAdapter<OpenOrderPriceInfo.Prodect.PackAge> {

    private List<OpenOrderPriceInfo.Prodect.PackAge> datas;

    public OpenOrderPriceSubListAdapter(Context context, int itemLayoutId, List<OpenOrderPriceInfo.Prodect.PackAge> datas) {
        super(context, itemLayoutId, datas);
        this.datas = datas;
    }

    @Override
    public void convert(BaseRecyclerHolder holder, OpenOrderPriceInfo.Prodect.PackAge item, int position) {
        OpenOrderPriceInfo.Prodect.PackAge packAge = datas.get(position);

        if (!TextUtils.isEmpty(packAge.getProduct_name())) {
            holder.setText(R.id.txt_price_name, packAge.getProduct_name());
        }

        if (!TextUtils.isEmpty(packAge.getProduct_price())) {
            holder.setText(R.id.txt_price, packAge.getProduct_price());
        }
    }
}
