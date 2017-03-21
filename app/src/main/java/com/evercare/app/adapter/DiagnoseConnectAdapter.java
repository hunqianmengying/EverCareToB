package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.DiagnoseConnectionInfo;

import java.util.List;

import static com.evercare.app.util.Constant.LOADING_MORE;
import static com.evercare.app.util.Constant.NO_LOAD_MORE;
import static com.evercare.app.util.Constant.PULLUP_LOAD_MORE;

/**
 * 作者：LXQ on 2016-11-11 15:57
 * 邮箱：842202389@qq.com
 * 随诊回话Adapter
 */
public class DiagnoseConnectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context context;
    private List<DiagnoseConnectionInfo> connectionInfoList;
    private LayoutInflater inflater;
    private GridViewToViewPagerListener listener;


    private ReplyAndReadListener replyAndReadListener;
    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    public DiagnoseConnectAdapter(Context context, List<DiagnoseConnectionInfo> list, GridViewToViewPagerListener listener, ReplyAndReadListener replyListener) {
        this.context = context;
        this.connectionInfoList = list;
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.replyAndReadListener = replyListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.connectionitem, parent, false);
            return new DiagonseViewHolder(view, listener, replyAndReadListener);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = inflater.inflate(R.layout.load_more_footview_layout, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;

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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DiagonseViewHolder) {
            DiagonseViewHolder diagonseViewHolder = (DiagonseViewHolder) holder;
            diagonseViewHolder.edt_review.setText(connectionInfoList.get(position).getReply());
            diagonseViewHolder.txt_name.setText(connectionInfoList.get(position).getCustom_name());
            diagonseViewHolder.txt_date.setText(connectionInfoList.get(position).getCreate_time());
            diagonseViewHolder.txt_connectdetail.setText(connectionInfoList.get(position).getContent());
            if(TextUtils.isEmpty(connectionInfoList.get(position).getImages())){
                diagonseViewHolder.grv_images.setVisibility(View.GONE);
            }else{
                diagonseViewHolder.grv_images.setVisibility(View.VISIBLE);
            }
            diagonseViewHolder.grv_images.setAdapter(new ImageLoaderAdapter(context, connectionInfoList.get(position).getImages().split(",")));
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
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
        return connectionInfoList == null ? 0 : connectionInfoList.size()+1;
    }

    public class DiagonseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txt_connectdetail;
        private TextView txt_name;
        private GridView grv_images;
        private TextView txt_date;
        private EditText edt_review;
        private TextView txt_cancel;
        private TextView txt_review;

        private RelativeLayout rlv_review;
        private ReplyAndReadListener readListener;

        public DiagonseViewHolder(View itemView, final GridViewToViewPagerListener listener, ReplyAndReadListener readListener) {
            super(itemView);
            txt_connectdetail = (TextView) itemView.findViewById(R.id.txt_connectdetail);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            grv_images = (GridView) itemView.findViewById(R.id.grv_images);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            edt_review = (EditText) itemView.findViewById(R.id.edt_review);
            txt_cancel = (TextView) itemView.findViewById(R.id.txt_cancel);
            txt_review = (TextView) itemView.findViewById(R.id.txt_review);
            rlv_review = (RelativeLayout) itemView.findViewById(R.id.rlv_review);

            txt_connectdetail.setOnClickListener(this);
            txt_cancel.setOnClickListener(this);
            txt_review.setOnClickListener(this);

            grv_images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (listener != null) {
                        listener.onImageClickListener(getAdapterPosition(), position);
                    }
                }
            });
            this.readListener = readListener;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_connectdetail:
                    readListener.onItemOperationListener(true, getAdapterPosition(), "");
                    rlv_review.setVisibility(View.VISIBLE);
                    break;
                case R.id.txt_cancel:
                    rlv_review.setVisibility(View.GONE);
                    break;
                case R.id.txt_review:
                    readListener.onItemOperationListener(false, getAdapterPosition(), edt_review.getText().toString());
                    rlv_review.setVisibility(View.GONE);
                    break;
            }
        }
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

    public interface GridViewToViewPagerListener {
        public void onImageClickListener(int itemIndex, int imageIndex);
    }

    public interface ReplyAndReadListener {
        public void onItemOperationListener(boolean isRead, int index, String reply);
    }

    public void addFooterItem(List<DiagnoseConnectionInfo>  items) {
        connectionInfoList.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * 更新加载更多状态
     * @param status
     */
    public void changeMoreStatus(int status){
        mLoadMoreStatus=status;
        notifyDataSetChanged();
    }
}
