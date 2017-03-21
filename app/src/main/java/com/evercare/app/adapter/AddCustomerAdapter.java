package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.AddCustomerInfo;
import com.evercare.app.model.CustomerInfo;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.OpenOrderItemInfo;
import com.evercare.app.model.TodayOpenOrderInfo;
import com.evercare.app.util.BitmapUtil;
import com.evercare.app.view.SlidingButtonView;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 今日新分客户Adapter
 */
public class AddCustomerAdapter extends RecyclerView.Adapter<AddCustomerAdapter.RecyclerViewHolder> implements SlidingButtonView.IonSlidingButtonListener {
    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;

    private Context context;
    private SlidingButtonAdapter.IonSlidingViewClickListener mIDeleteBtnClickListener;
    private SlidingButtonView mMenu = null;

    private AddCustomerInfo addCustomerInfo;
    private LayoutInflater inflater;

    public AddCustomerAdapter(Context context, AddCustomerInfo customerInfo) {
        this.mIDeleteBtnClickListener = (SlidingButtonAdapter.IonSlidingViewClickListener) context;
        this.context = context;
        this.addCustomerInfo = customerInfo;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.addpersonitem, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder viewHolder, int position) {

        int todayNum = addCustomerInfo.getNewlist() == null ? 0 : addCustomerInfo.getNewlist().size();
        int weekNum = addCustomerInfo.getOldlist() == null ? 0 : addCustomerInfo.getOldlist().size();

        CustomerInfo customerInfo = null;
        if (position < todayNum) {
            customerInfo = addCustomerInfo.getNewlist().get(position);
            customerInfo.setDate_state("今日新增");
        } else if (position < (todayNum + weekNum)) {
            customerInfo = addCustomerInfo.getOldlist().get(position - todayNum);
            customerInfo.setDate_state("往期新增");
        }

        viewHolder.txt_customer_name.setText(customerInfo.getName());
        switch (customerInfo.getOverlap()) {
            case "1":
                viewHolder.txt_consultrecord_type.setText("所属");
                //recyclerViewHolder.txt_billing.setVisibility(View.INVISIBLE);
                break;
            case "2":
                viewHolder.txt_consultrecord_type.setText("交叉");
                break;
        }

        viewHolder.txt_phone.setText(customerInfo.getMobile());

        if (position == 0) {
            viewHolder.tvStickyHeader.setVisibility(View.VISIBLE);
            viewHolder.tvStickyHeader.setText(customerInfo.getDate_state());
            viewHolder.itemView.setTag(FIRST_STICKY_VIEW);
        } else {
            CustomerInfo beforeItem = null;

            int temNum = position - 1;
            if (temNum < todayNum) {
                beforeItem = addCustomerInfo.getNewlist().get(temNum);
            } else if (temNum < (todayNum + weekNum)) {
                beforeItem = addCustomerInfo.getOldlist().get(temNum - todayNum);
            }

            if (!TextUtils.equals(customerInfo.getDate_state(), beforeItem.getDate_state())) {
                viewHolder.tvStickyHeader.setVisibility(View.VISIBLE);
                viewHolder.tvStickyHeader.setText(customerInfo.getDate_state());
                viewHolder.itemView.setTag(HAS_STICKY_VIEW);
            } else {
                viewHolder.tvStickyHeader.setVisibility(View.GONE);
                viewHolder.itemView.setTag(NONE_STICKY_VIEW);
            }
        }
        viewHolder.itemView.setContentDescription(customerInfo.getDate_state());

        //设置内容布局的宽为屏幕宽度
        viewHolder.rl_content_wrapper.getLayoutParams().width = BitmapUtil.getScreenWidth(context);

        viewHolder.rl_content_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否有删除菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                } else {
                    int n = viewHolder.getLayoutPosition();
                    mIDeleteBtnClickListener.onItemClick(v, n);
                }
            }
        });

        viewHolder.tv_back_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = viewHolder.getLayoutPosition();
                mIDeleteBtnClickListener.onEditTimeBtnClick(v, n);
            }
        });
    }

    @Override
    public int getItemCount() {
        int todayNum = addCustomerInfo.getNewlist() == null ? 0 : addCustomerInfo.getNewlist().size();
        int weekNum = addCustomerInfo.getOldlist() == null ? 0 : addCustomerInfo.getOldlist().size();

        return todayNum + weekNum;
    }

    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if (menuIsOpen()) {
            if (mMenu != slidingButtonView) {
                closeMenu();
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;

    }

    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if (mMenu != null) {
            return true;
        }
        Log.i("asd", "mMenu为null");
        return false;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStickyHeader;
        public TextView txt_customer_name;
        public TextView txt_consultrecord_type;//所属   交叉
        public TextView txt_phone;
        public RelativeLayout rl_content_wrapper;
        public TextView tv_back_public;
        public SlidingButtonView slidingbutton_view;



        public RecyclerViewHolder(View itemView) {
            super(itemView);
            rl_content_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_content_wrapper);
            tv_back_public = (TextView) itemView.findViewById(R.id.tv_back_public);
            tvStickyHeader = (TextView) itemView.findViewById(R.id.txt_letter);
            txt_customer_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_consultrecord_type = (TextView) itemView.findViewById(R.id.consultrecord_type);
            txt_phone = (TextView) itemView.findViewById(R.id.txt_phone);
            slidingbutton_view = (SlidingButtonView) itemView.findViewById(R.id.slidingbutton_view);

            slidingbutton_view.setSlidingButtonListener(AddCustomerAdapter.this);
        }
    }
}
