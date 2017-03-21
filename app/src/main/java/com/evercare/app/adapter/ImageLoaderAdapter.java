package com.evercare.app.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.evercare.app.R;
import com.evercare.app.util.Constant;
import com.evercare.app.view.NoScrollGridView;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import static com.evercare.app.R.id.image_touxiang;
import static com.umeng.socialize.a.j.as;

/**
 * 作者：LXQ on 2016-11-10 18:03
 * 邮箱：842202389@qq.com
 * Javadoc
 */
public class ImageLoaderAdapter extends BaseAdapter {

    private Context context;
    private String[] imageInfos;
    private LayoutInflater layoutInflater;

    public ImageLoaderAdapter(Context context, String[] list) {
        this.context = context;
        imageInfos = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageInfos == null ? 0 : imageInfos.length;
    }

    @Override
    public Object getItem(int position) {
        return imageInfos[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = imageInfos[position];
        int width = ((NoScrollGridView) (parent)).getColumnWidth();
        convertView = layoutInflater.inflate(R.layout.image_item, parent, false);
        SimpleDraweeView img = (SimpleDraweeView) convertView.findViewById(R.id.simpleimage);
        img.setLayoutParams(new ActionBar.LayoutParams(width, width));
        if(TextUtils.isEmpty(item)){
            img.setImageURI(Constant.BASEURL_IMG+item);
            img.setVisibility(View.GONE);
        }else{
            String  itemUrl = item.substring(0,item.lastIndexOf("."))+"_200x200"+item.substring(item.lastIndexOf("."),item.length());
            img.setImageURI(Constant.BASEURL_IMG+itemUrl);
        }

        GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(context.getResources())
                .setFailureImage(R.drawable.default_gray_bg)
                .build();
        img.setHierarchy(hierarchy);
        return convertView;
    }
}
