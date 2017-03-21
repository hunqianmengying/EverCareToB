package com.evercare.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.ConsultingProductInfo;
import com.evercare.app.model.CustomerItemClickListener;

import java.util.List;

/**
 * 作者：xlren on 2016-12-6 16:19
 * 邮箱：renxianliang@126.com
 * 关注项目列表
 */
public class SearchProjectAdapter extends RecyclerView.Adapter<SearchProjectAdapter.ViewHolder> {
    private Context context;
    private List<ConsultingProductInfo> caseItemInfos;
    private CustomerItemClickListener itemClickListener;


    public SearchProjectAdapter(Context context, List<ConsultingProductInfo> list, CustomerItemClickListener itemClickListener) {
        this.context = context;
        caseItemInfos = list;
        this.itemClickListener = itemClickListener;
    }



    @Override
    public SearchProjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_porject_item_layout, parent, false);
        return new ViewHolder(view, itemClickListener);
    }



    @Override
    public void onBindViewHolder(SearchProjectAdapter.ViewHolder viewHolder, int position) {
        viewHolder.txtContent.setText(caseItemInfos.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return caseItemInfos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtContent;

        private CustomerItemClickListener itemClickListener;

        public ViewHolder(View itemView, CustomerItemClickListener clickListener) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.txtContent);
            this.itemClickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

}
