package com.evercare.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evercare.app.Activity.AlreadyOpenOrderActivity;
import com.evercare.app.Activity.AppointmentActivity;
import com.evercare.app.Activity.SettingActivity;
import com.evercare.app.R;
import com.evercare.app.util.BitmapUtil;
import com.evercare.app.util.Constant;
import com.evercare.app.util.FrescoUtils;
import com.evercare.app.util.PrefUtils;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;

/**
 * 作者：xlren on 2016/8/29 13:23
 * 邮箱：renxianliang@126.com
 * 我的
 */

public class MeFragment extends Fragment {

    private TextView txtName;
    private TextView txtPhone;
    private TextView center_text;

    private RelativeLayout view_appointment;
    private RelativeLayout view_setting;
    private SimpleDraweeView personalLayout;
    private SimpleDraweeView image_touxiang;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        this.context = getContext();
        //logoutButton = (Button) view.findViewById(R.id.btn_logout_register);
        view_appointment = (RelativeLayout) view.findViewById(R.id.view_appointment);
        view_setting = (RelativeLayout) view.findViewById(R.id.view_setting);
        txtName = (TextView) view.findViewById(R.id.txt_name);
        //txtPhone = (TextView) view.findViewById(R.id.txtPhone);
        center_text = (TextView) view.findViewById(R.id.center_text);
        personalLayout = (SimpleDraweeView) view.findViewById(R.id.personalLayout);
        image_touxiang = (SimpleDraweeView) view.findViewById(R.id.image_touxiang);
        initData();
        return view;
    }

    private void initData() {
        center_text.setText("我的");
        if (TextUtils.isEmpty(PrefUtils.getDefaults("userName", context))) {
            txtName.setText("咨询师");
        } else {
            txtName.setText(PrefUtils.getDefaults("userName", context));
        }

        //圆形小头像设置
        String url = Constant.BASEURL_IMG+PrefUtils.getDefaults("portraits", context);
        image_touxiang.setImageURI(url);
        RoundingParams rp = new RoundingParams();
        rp.setRoundAsCircle(true);//设置圆形
        rp.setBorder(getResources().getColor(R.color.white), 10);//设置边框
        GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources())
                .setFailureImage(R.drawable.default_photo)
                .setRoundingParams(rp)
                .setFadeDuration(5000).build();
        image_touxiang.setHierarchy(hierarchy);
        //判断头像是否可用
        boolean isCacheInDisk = Fresco.getImagePipelineFactory().getMainDiskStorageCache().hasKey(new SimpleCacheKey(url));
        if (isCacheInDisk) {
            //高斯模糊
            FrescoUtils.loadUrlInBlur(url, personalLayout, 120, 40, context, 1, 1);
        } else {
            //模糊默认头像
            Drawable dradable = BitmapUtil.BoxBlurFilter(BitmapUtil.drawable2Bitmap(getResources().getDrawable(R.drawable.default_phpto_bg)));
            personalLayout.setImageDrawable(dradable);
        }

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlreadyOpenOrderActivity.class);
                startActivity(intent);
            }
        });

        view_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(context, "setting_page");
                Intent intent = new Intent(context, SettingActivity.class);
                startActivity(intent);
            }
        });
    }
}
