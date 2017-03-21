package com.evercare.app.adapter;

import android.content.Context;

import com.evercare.app.R;
import com.evercare.app.model.RecordItemInfo;
import com.evercare.app.model.SaleCureItemInfo;
import com.evercare.app.util.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 作者：LXQ on 2016-11-28 14:50
 * 邮箱：842202389@qq.com
 * 消费/治疗item
 */
public class RecordAdapter extends BaseRecyclerAdapter<RecordItemInfo> {

    private List<RecordItemInfo> datas;

    public RecordAdapter(Context context, int itemLayoutId, List<RecordItemInfo> datas) {
        super(context, itemLayoutId, datas);
        this.datas = datas;
    }

    @Override
    public void convert(BaseRecyclerHolder holder, RecordItemInfo item, int position) {
        RecordItemInfo recordItemInfo = datas.get(position);

        holder.setText(R.id.txt_time, CommonUtil.stringToDate(recordItemInfo.getPay_date()));
        holder.setText(R.id.txt_type, recordItemInfo.getType_name());
        holder.setText(R.id.txt_price, recordItemInfo.getPrice());
    }

}
