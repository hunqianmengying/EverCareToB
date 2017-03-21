package com.evercare.app.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.CustomerItemClickListener;

import java.util.List;

/**
 * 作者：LXQ on 2016-9-26 16:06
 * 邮箱：842202389@qq.com
 * 搜索案例和活动Adapter
 */

public class SearchItemAdatpter extends RecyclerView.Adapter<SearchItemAdatpter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<String> caseNameList;
    private CustomerItemClickListener itemClickListener;

    public SearchItemAdatpter(Context context, List<String> casenames, CustomerItemClickListener itemClickListener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        caseNameList = casenames;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_searchitem, parent, false);

        return new ViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = caseNameList.get(position);
        holder.txt_case_name.setText(name);

    }

    @Override
    public int getItemCount() {
        return caseNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_case_name;
        public ImageView img_time;
        private CustomerItemClickListener itemClickListener;

        public ViewHolder(View itemView, CustomerItemClickListener itemClickListener) {
            super(itemView);
            this.itemClickListener = itemClickListener;
            txt_case_name = (TextView) itemView.findViewById(R.id.txt_case_name);
            img_time = (ImageView) itemView.findViewById(R.id.img_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
