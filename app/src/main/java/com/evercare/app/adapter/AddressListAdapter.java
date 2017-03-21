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
import com.evercare.app.model.PersonInfo;

import java.util.List;

/**
 * 作者：LXQ on 2016-9-13 11:28
 * 邮箱：842202389@qq.com
 * 通讯录Adapter
 */
public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {

    private Context context;
    private List<PersonInfo> persons;
    private LayoutInflater inflater;
    private CustomerItemClickListener customerItemClickListener;
    private final String NO_OVERLAPPING = "0";//0无1所属2交叉
    private final String SUBORDINATE = "1";//0无1所属2交叉
    private final String OVERLAPPING = "2";//0无1所属2交叉
    private String tag;

    public AddressListAdapter(Context context, String tag,List<PersonInfo> personInfoList, CustomerItemClickListener clickListener) {
        this.context = context;
        persons = personInfoList;
        customerItemClickListener = clickListener;
        inflater = LayoutInflater.from(context);
        this.tag = tag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = inflater.inflate(R.layout.personitem_layout, parent, false);
        return new ViewHolder(item, customerItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PersonInfo personInfo = persons.get(position);
        holder.txt_name.setText(personInfo.getName());
        holder.txt_phone.setText(personInfo.getMobile());

        if (NO_OVERLAPPING.equals(personInfo.getOverlap())) {
            holder.consultrecord_type.setText("");
        } else if (SUBORDINATE.equals(personInfo.getOverlap())) {
            holder.consultrecord_type.setText("所属");
            holder.consultrecord_type.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else if (OVERLAPPING.equals(personInfo.getOverlap())) {
            holder.consultrecord_type.setText("交叉");
            holder.consultrecord_type.setTextColor(context.getResources().getColor(R.color.paget_sliding_text));
        }
        int selection = personInfo.getFirstLetter().charAt(0);
        int positionForSelection = getPositionForSelection(selection);
        if(tag.equals("SearchCustomerActivity")){
            holder.txt_letter.setVisibility(View.GONE);
        }else{
            if (position == positionForSelection) {
                holder.txt_letter.setVisibility(View.VISIBLE);
                holder.txt_letter.setText(personInfo.getFirstLetter());
            } else {
                holder.txt_letter.setVisibility(View.GONE);
            }
        }
    }


    public int getPositionForSelection(int selection) {
        for (int i = 0; i < persons.size(); i++) {
            String Fpinyin = persons.get(i).getFirstLetter();
            char first = Fpinyin.toUpperCase().charAt(0);
            if (first == selection) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView txt_letter;
        public TextView txt_name;
        //public ImageView img_header;
        public TextView consultrecord_type;
        TextView txt_phone;
        private CustomerItemClickListener customerItemClickListener;

        public ViewHolder(View itemView, CustomerItemClickListener clickListener) {
            super(itemView);
            txt_letter = (TextView) itemView.findViewById(R.id.txt_letter);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_phone = (TextView) itemView.findViewById(R.id.txt_phone);
            consultrecord_type = (TextView) itemView.findViewById(R.id.consultrecord_type);
            customerItemClickListener = clickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (customerItemClickListener != null) {
                customerItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (customerItemClickListener != null) {
                customerItemClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }

}
