package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.PriceInfo;

import java.util.List;

/**
 * 作者：LXQ on 2016-9-13 11:28
 * 邮箱：842202389@qq.com
 * 价目表Adapter
 */
public class PriceSubListAdapter extends BaseRecyclerAdapter<PriceInfo.PackAge> {

    private List<PriceInfo.PackAge> datas;

    public PriceSubListAdapter(Context context, int itemLayoutId, List<PriceInfo.PackAge> datas) {
        super(context, itemLayoutId, datas);
        this.datas = datas;
    }

    @Override
    public void convert(BaseRecyclerHolder holder, PriceInfo.PackAge item, int position) {
        PriceInfo.PackAge packAge = datas.get(position);

        if (!TextUtils.isEmpty(packAge.getName())) {
            holder.setText(R.id.txt_price_name, packAge.getName());
        }

        if (!TextUtils.isEmpty(packAge.getPrice())) {
            holder.setText(R.id.txt_price, packAge.getPrice());
        }
    }
}
