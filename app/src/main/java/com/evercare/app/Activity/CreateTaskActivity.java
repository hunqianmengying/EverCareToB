package com.evercare.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.fragment.AddRecordFragment;
import com.evercare.app.fragment.CreateTaskFragment;
import com.evercare.app.view.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：LXQ on 2016-10-28 10:36
 * 邮箱：842202389@qq.com
 * 创建任务界面,场景和咨询
 */
public class CreateTaskActivity extends FragmentActivity {
    private TextView txt_center;
    private TextView txt_left;

    private PagerSlidingTabStrip layout_tab;

    private ViewPager vp_createInfo;

    private String[] titleList = new String[]{"场景", "咨询"};

    private List<Fragment> fragmentList;

    private String business_cuid;
    private String custom_card_id;
    private String custom_name;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(CreateTaskActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(CreateTaskActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createtask);

        business_cuid = getIntent().getStringExtra("business_cuid");
        custom_card_id = getIntent().getStringExtra("custom_card_id");
        custom_name = getIntent().getStringExtra("custom_name");
        initView();
        initData();
    }


    private void initView() {
        txt_center = (TextView) findViewById(R.id.center_text);
        txt_left = (TextView) findViewById(R.id.left_text);
        txt_left.setText("返回");

        txt_left.setVisibility(View.VISIBLE);
        txt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_center.setText("创建");

        layout_tab = (PagerSlidingTabStrip) findViewById(R.id.layout_tab);
        vp_createInfo = (ViewPager) findViewById(R.id.vp_createInfo);
    }

    private void initData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(CreateTaskFragment.newInstance(business_cuid, custom_card_id, custom_name));
        fragmentList.add(AddRecordFragment.newInstance(business_cuid, custom_name));
        BaseFragmentAdapter adapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList, titleList);
        vp_createInfo.setAdapter(adapter);
        layout_tab.setViewPager(vp_createInfo);
    }
}
