package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.TodayWorkInfo;
import com.evercare.app.model.TodayWorkItemInfo;
import com.evercare.app.util.BitmapUtil;
import com.evercare.app.util.DateTool;
import com.evercare.app.view.SlidingButtonView;

/**
 * 作者：LXQ on 2016-12-13 10:26
 * 邮箱：842202389@qq.com
 * 今日工作,活动回访,其他回访  ItemAdapter
 */
public class SlidingButtonAdapter extends RecyclerView.Adapter<SlidingButtonAdapter.MyViewHolder> implements SlidingButtonView.IonSlidingButtonListener {
    public static final int FIRST_STICKY_VIEW = 1;
    public static final int HAS_STICKY_VIEW = 2;
    public static final int NONE_STICKY_VIEW = 3;
    private TodayWorkInfo todayWorkInfo;
    private RelativeLayout.LayoutParams layoutParams;

    private RelativeLayout.LayoutParams layoutParams1;


    private Context mContext;

    private IonSlidingViewClickListener mIDeleteBtnClickListener;

    private SlidingButtonView mMenu = null;
    private LayoutInflater layoutInflater;
    private String module;

    public SlidingButtonAdapter(Context context, TodayWorkInfo todayWorkInfo, String module) {

        mContext = context;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) context;
        layoutInflater = LayoutInflater.from(context);
        this.todayWorkInfo = todayWorkInfo;
        this.module = module;
        layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        layoutParams1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public int getItemCount() {

        int todayNum = todayWorkInfo.getDaydata() == null ? 0 : todayWorkInfo.getDaydata().size();
        int staleNum = todayWorkInfo.getStaledata() == null ? 0 : todayWorkInfo.getStaledata().size();
        int doneNum = todayWorkInfo.getFinishdata() == null ? 0 : todayWorkInfo.getFinishdata().size();

        return todayNum + staleNum + doneNum;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.layout_slidingbutton_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        int todayNum = todayWorkInfo.getDaydata() == null ? 0 : todayWorkInfo.getDaydata().size();
        int staleNum = todayWorkInfo.getStaledata() == null ? 0 : todayWorkInfo.getStaledata().size();
        int doneNum = todayWorkInfo.getFinishdata() == null ? 0 : todayWorkInfo.getFinishdata().size();
        TodayWorkItemInfo itemInfo = null;

        if (position < todayNum) {
            itemInfo = todayWorkInfo.getDaydata().get(position);
            itemInfo.setDate_state("今日");

            holder.tv_time.setVisibility(View.GONE);
            holder.tv_name.setLayoutParams(layoutParams);
            holder.ic_arrow.setVisibility(View.VISIBLE);

        } else if (position < (todayNum + staleNum)) {
            itemInfo = todayWorkInfo.getStaledata().get(position - todayNum);
//            if (TextUtils.equals(module, "其他回访")) {
//                holder.ic_arrow.setVisibility(View.INVISIBLE);
//            } else {
//                holder.ic_arrow.setVisibility(View.VISIBLE);
//            }

            holder.tv_name.setLayoutParams(layoutParams1);

            holder.tv_time.setText(DateTool.getTimeState(itemInfo.getStart_time()));

            holder.tv_time.setVisibility(View.VISIBLE);

            itemInfo.setDate_state("已过期");
        } else if (position < (todayNum + staleNum + doneNum)) {
            itemInfo = todayWorkInfo.getFinishdata().get(position - todayNum - staleNum);
            itemInfo.setDate_state("已完成");
            holder.tv_name.setLayoutParams(layoutParams1);

            holder.ic_arrow.setVisibility(View.INVISIBLE);
            holder.tv_time.setVisibility(View.VISIBLE);
            holder.tv_time.setText(DateTool.getTimeState(itemInfo.getStart_time()));

            holder.slidingbutton_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        holder.tv_name.setText(itemInfo.getCustom_name());

        holder.tv_describe.setText(itemInfo.getDescribe());

        if (TextUtils.isEmpty(itemInfo.getProject_class_name())) {
            //holder.task_type.setLayoutParams(layoutParams);
            holder.tv_project_name.setVisibility(View.GONE);
        } else {
            holder.tv_project_name.setVisibility(View.VISIBLE);
            holder.tv_project_name.setText(itemInfo.getProject_class_name());
        }
        if (TextUtils.equals(module, "其他回访") || TextUtils.equals(module, "活动回访")) {
            holder.task_type.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams temLayoutParams = (RelativeLayout.LayoutParams) holder.task_type.getLayoutParams();
            temLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            // 1:未上门回访 2:未成交回访 3:术后回访 5:术后复查 6:术后调查 7:投诉回访 8:活动回访 10:注射回访 9:术前回访11:首次注射回访 12:治疗回访 13:首次治疗回访 4:其他
            switch (itemInfo.getTask_type()) {
                case "1":
                    holder.task_type.setText("未上门回访");
                    break;
                case "2":
                    holder.task_type.setText("未成交回访");
                    break;
                case "3":
                    holder.task_type.setText("术后回访");
                    break;
                case "4":
                    holder.task_type.setText("其他");
                    break;
                case "5":
                    holder.task_type.setText("术后复查");
                    break;
                case "6":
                    holder.task_type.setText("术后调查");
                    break;
                case "7":
                    holder.task_type.setText("投诉回访");
                    break;
                case "8":
                    holder.task_type.setText("活动回访");
                    break;
                case "9":
                    holder.task_type.setText("术前回访");
                    break;
                case "10":
                    holder.task_type.setText("注射回访");
                    break;
                case "11":
                    holder.task_type.setText("注射回访");
                    break;
                case "12":
                    holder.task_type.setText("治疗回访");
                    break;
                case "13":
                    holder.task_type.setText("首次治疗回访");
                    break;
                case "14":
                    holder.task_type.setText("客户满意度回访");
                    break;
                case "15":
                    holder.task_type.setText("市场活动邀约任务");
                    break;
            }
        } else {
            holder.task_type.setVisibility(View.GONE);
        }

        if (position == 0) {
            holder.tvStickyHeader.setVisibility(View.VISIBLE);
            holder.tvStickyHeader.setText(itemInfo.getDate_state());
            holder.itemView.setTag(FIRST_STICKY_VIEW);
        } else {
            TodayWorkItemInfo beforeItem = null;

            int temNum = position - 1;
            if (temNum < todayNum) {
                beforeItem = todayWorkInfo.getDaydata().get(temNum);
            } else if (temNum < (todayNum + staleNum)) {
                beforeItem = todayWorkInfo.getStaledata().get(temNum - todayNum);
            } else {
                beforeItem = todayWorkInfo.getFinishdata().get(temNum - todayNum - staleNum);
            }

            if (!TextUtils.equals(itemInfo.getDate_state(), beforeItem.getDate_state())) {
                holder.tvStickyHeader.setVisibility(View.VISIBLE);
                holder.tvStickyHeader.setText(itemInfo.getDate_state());
                holder.itemView.setTag(HAS_STICKY_VIEW);
            } else {
                holder.tvStickyHeader.setVisibility(View.GONE);
                holder.itemView.setTag(NONE_STICKY_VIEW);
            }
        }
        holder.itemView.setContentDescription(itemInfo.getDate_state());


        //设置内容布局的宽为屏幕宽度
        holder.rl_content_wrapper.getLayoutParams().width = BitmapUtil.getScreenWidth(mContext);

        holder.rl_content_wrapper.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否有删除菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                } else {
                    int n = holder.getLayoutPosition();
                    mIDeleteBtnClickListener.onItemClick(v, n);
                }
            }
        });

        holder.btn_Delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = holder.getLayoutPosition();
                mIDeleteBtnClickListener.onDeleteBtnCilck(v, n);
            }
        });

        if (TextUtils.equals(module, "活动回访") || TextUtils.equals("其他回访", module)) {
            holder.tv_edit_time.setVisibility(View.GONE);
        } else {
            holder.tv_edit_time.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = holder.getLayoutPosition();
                    mIDeleteBtnClickListener.onEditTimeBtnClick(v, n);
                }
            });
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStickyHeader;

        public TextView btn_Delete;
        public TextView tv_name;
        public TextView tv_describe;
        public TextView tv_time;
        public TextView tv_project_name;
        public RelativeLayout rl_content_wrapper;
        public SlidingButtonView slidingbutton_view;
        public TextView tv_edit_time;
        public ImageView ic_arrow;
        public TextView task_type;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvStickyHeader = (TextView) itemView.findViewById(R.id.tv_sticky_header_view);
            ic_arrow = (ImageView) itemView.findViewById(R.id.ic_arrow);

            btn_Delete = (TextView) itemView.findViewById(R.id.tv_delete);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_describe = (TextView) itemView.findViewById(R.id.tv_describe);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_edit_time = (TextView) itemView.findViewById(R.id.tv_edit_time);
            tv_project_name = (TextView) itemView.findViewById(R.id.tv_project_name);
            task_type = (TextView) itemView.findViewById(R.id.task_type);
            rl_content_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_content_wrapper);
            slidingbutton_view = (SlidingButtonView) itemView.findViewById(R.id.slidingbutton_view);
            slidingbutton_view.setSlidingButtonListener(SlidingButtonAdapter.this);
        }
    }

    public void addData(int position) {
        //mDatas.add(position, "添加项");
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        //mDatas.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 删除菜单打开信息接收
     */
    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    /**
     * 滑动或者点击了Item监听
     *
     * @param slidingButtonView
     */
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
        return false;
    }


    public interface IonSlidingViewClickListener {
        void onItemClick(View view, int position);

        void onDeleteBtnCilck(View view, int position);

        void onEditTimeBtnClick(View view, int position);
    }
}

