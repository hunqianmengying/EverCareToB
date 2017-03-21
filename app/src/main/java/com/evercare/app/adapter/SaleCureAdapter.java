package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.OrderProjectInfo;
import com.evercare.app.model.RecordItemInfo;
import com.evercare.app.model.SaleCureInfo;
import com.evercare.app.model.SaleCureItemInfo;
import com.evercare.app.util.DateTool;

import java.util.ArrayList;
import java.util.List;

import static com.evercare.app.util.Constant.LOADING_MORE;
import static com.evercare.app.util.Constant.NO_LOAD_MORE;
import static com.evercare.app.util.Constant.PULLUP_LOAD_MORE;

/**
 * 作者：LXQ on 2016-11-28 14:50
 * 邮箱：842202389@qq.com
 * 消费记录Adapter
 */
public class SaleCureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;


    private List<SaleCureInfo> datas;
    private Context context;
    private LayoutInflater inflater;


    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    public SaleCureAdapter(Context context, List<SaleCureInfo> data) {
        this.context = context;
        this.datas = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.sale_cure_item, parent, false);
            return new SaleRecordItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = inflater.inflate(R.layout.load_more_footview_layout, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SaleRecordItemViewHolder) {
            SaleCureInfo saleCureInfo = datas.get(position);
            SaleRecordItemViewHolder holder = (SaleRecordItemViewHolder) viewHolder;
            if (!TextUtils.isEmpty(saleCureInfo.getCounselor_name())) {


                String htmlText = "<font color=#444444>" + saleCureInfo.getProduct_name() + "</font>"
                        + "&nbsp&nbsp&nbsp" + "<font color=#777777 ><small>" + saleCureInfo.getCounselor_name() + "</small></font>";
                holder.txt_project_name.setText(Html.fromHtml(htmlText));
            } else {
                holder.txt_project_name.setText(saleCureInfo.getProduct_name());
            }
            try {
                holder.txt_time.setText(DateTool.dateToString(DateTool.stringTodate(saleCureInfo.getPay_date(), "yyyy-MM-dd"), "yyyy-MM-dd"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.txt_account.setText(saleCureInfo.getAccount());
            //holder.txt_counselor_name.setText(saleCureInfo.getCounselor_name());
            holder.txt_doctor_name.setText(saleCureInfo.getDoctor_name());
            holder.txt_nurse_name.setText(saleCureInfo.getNurse_name());
            holder.txt_buy_num.setText(saleCureInfo.getBuy_num());
            switch (saleCureInfo.getType()) {
                case "1":
                    holder.txt_type.setText("产品、药品收款");
                    break;
                case "2":
                    holder.txt_type.setText("治疗");
                    break;
                case "3":
                    holder.txt_type.setText("换卡费用");
                    break;
                case "4":
                    holder.txt_type.setText("项目收款");
                    break;
                case "5":
                    holder.txt_type.setText("辅助项目收款");
                    break;
                case "6":
                    holder.txt_type.setText("内部用料");
                    break;
            }
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


    public void initData(List<SaleCureInfo> items) {
        datas.clear();
        datas.addAll(items);
        notifyDataSetChanged();
    }


    public void addFooterItem(List<SaleCureInfo> items) {
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

    public class SaleRecordItemViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_project_name;
        public TextView txt_type;
        public TextView txt_buy_num;
        //public TextView txt_counselor_name;
        private TextView txt_doctor_name;
        private TextView txt_nurse_name;
        private TextView txt_time;
        public TextView txt_account;

        public SaleRecordItemViewHolder(View itemView) {
            super(itemView);
            txt_project_name = (TextView) itemView.findViewById(R.id.txt_project_name);
            txt_type = (TextView) itemView.findViewById(R.id.txt_type);
            txt_buy_num = (TextView) itemView.findViewById(R.id.txt_buy_num);
            //txt_counselor_name = (TextView) itemView.findViewById(R.id.txt_counselor_name);
            txt_doctor_name = (TextView) itemView.findViewById(R.id.txt_doctor_name);
            txt_nurse_name = (TextView) itemView.findViewById(R.id.txt_nurse_name);
            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            txt_account = (TextView) itemView.findViewById(R.id.txt_account);
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
