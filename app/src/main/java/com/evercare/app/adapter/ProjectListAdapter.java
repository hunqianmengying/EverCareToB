package com.evercare.app.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.ProjectInfoItem;

import java.util.List;

/**
 * 作者：LXQ on 2016-12-6 16:40
 * 邮箱：842202389@qq.com
 * 订单列表Adapter
 */
public class ProjectListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProjectInfoItem> datas;
    private Context context;
    private LayoutInflater inflater;
    private CustomerItemClickListener clickListener;

    public ProjectListAdapter(Context context, List<ProjectInfoItem> data, CustomerItemClickListener listener) {
        this.datas = data;
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        this.clickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= datas.size()) {
            return 0;//返回FooterView
        } else {
            return 1;//返回正常的ItemView
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.project_item, parent, false);
            return new NormalViewHolder(view, clickListener);
        } else {
            View view = inflater.inflate(R.layout.project_footerview, parent, false);
            return new FooterViewHolder(view, clickListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= datas.size()) {

        } else {
            if (holder instanceof NormalViewHolder) {
                ProjectInfoItem item = datas.get(position);
                ((NormalViewHolder) holder).img_delete_project.setVisibility(item.isShowImg() ? View.VISIBLE : View.GONE);
                ((NormalViewHolder) holder).txt_project_name.setText(item.getProject_name());
                ((NormalViewHolder) holder).txt_project_price.setText(item.getPricePerUnit());
                ((NormalViewHolder) holder).txt_project_num.setText(String.valueOf(item.getQuantity()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }


    public class NormalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CustomerItemClickListener clickListener;
        public ImageView img_delete_project;
        public TextView txt_project_name;
        public TextView txt_project_price;
        public TextView txt_project_num;

        public NormalViewHolder(View itemView, CustomerItemClickListener listener) {
            super(itemView);
            this.clickListener = listener;
            img_delete_project = (ImageView) itemView.findViewById(R.id.img_delete_project);
            txt_project_name = (TextView) itemView.findViewById(R.id.txt_project_name);
            txt_project_price = (TextView) itemView.findViewById(R.id.txt_project_price);
            txt_project_num = (TextView) itemView.findViewById(R.id.txt_project_num);
            //txt_project_price.setOnClickListener(this);
            //txt_project_num.setOnClickListener(this);
            img_delete_project.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        private CustomerItemClickListener clickListener;
        private Button btn_footer;

        public FooterViewHolder(View itemView, CustomerItemClickListener listener) {
            super(itemView);
            this.clickListener = listener;
            btn_footer = (Button) itemView.findViewById(R.id.btn_footer);
            btn_footer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}
