package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.DiagnoseConnectionInfo;
import com.evercare.app.model.NewsItemInfo;

import java.util.List;

import static com.evercare.app.util.Constant.LOADING_MORE;
import static com.evercare.app.util.Constant.NO_LOAD_MORE;
import static com.evercare.app.util.Constant.PULLUP_LOAD_MORE;


/**
 * 作者：LXQ on 2016-10-27 14:37
 * 邮箱：842202389@qq.com
 * Home页消息Adapter
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private LayoutInflater inflater;
    private List<NewsItemInfo> contentList;
    private Context context;
    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    public NewsAdapter(Context context, List<NewsItemInfo> list) {
        inflater = LayoutInflater.from(context);
        contentList = list;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.news_item, parent, false);
            return new ViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = inflater.inflate(R.layout.load_more_footview_layout, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ViewHolder) {
            final ViewHolder holder = (ViewHolder) viewHolder;
            NewsItemInfo consultationVisitInfo = contentList.get(position);
            holder.txt_content.setText(consultationVisitInfo.getMessage());
            holder.txt_time.setText(consultationVisitInfo.getCreate_time());
            holder.txt_title.setText(consultationVisitInfo.getTitle());
            if ("0".equals(consultationVisitInfo.getType())) {
                holder.txt_type.setText("电话预约");
            } else {
                holder.txt_type.setText("现场咨询");
            }

            if (TextUtils.equals(consultationVisitInfo.getStatus(), "1")) {
                holder.red_point.setVisibility(View.INVISIBLE);
            } else {
                holder.red_point.setVisibility(View.VISIBLE);
            }

            //解决滑动展开错位
            if (consultationVisitInfo.isSelected) {
                holder.txt_content.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                holder.txt_content.setLines(2);
            } else {
                holder.txt_content.setEllipsize(null); // 展开
                holder.txt_content.setSingleLine(false);
            }
            //判断是否设置了监听器
            if (mOnItemClickListener != null) {
                //为ItemView设置监听器
                holder.txt_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.onItemClick(holder.itemView, position); // 2
                    }
                });
                //为ItemView设置监听器
                holder.txt_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.onItemClick(holder.itemView, position); // 2
                    }
                });
                holder.txt_turn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.onItemClick(v, position); // 2
                    }
                });
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

    public void addFooterItem(List<NewsItemInfo> items) {
        contentList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contentList == null ? 0 : contentList.size() + 1;
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

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_content;
        TextView txt_title;
        TextView txt_time;
        TextView txt_type;
        TextView txt_turn;

        private View red_point;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_content = (TextView) itemView.findViewById(R.id.txt_content);
            txt_title = (TextView) itemView.findViewById(R.id.txt_title);
            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            txt_type = (TextView) itemView.findViewById(R.id.txt_type);
            txt_turn = (TextView) itemView.findViewById(R.id.txt_turn);
            red_point = itemView.findViewById(R.id.red_point);
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
