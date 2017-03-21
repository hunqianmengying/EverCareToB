package com.evercare.app.Activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.fragment.AchievementOnDayFragment;
import com.evercare.app.fragment.AchievementOnMonthFragment;
import com.evercare.app.fragment.AchievementOnWeekFragment;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.view.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：xlren on 2016-11-15 13:29
 * 邮箱：renxianliang@126.com
 * 首页业绩详情日、周、月
 */

public class AchievmentActivity extends FragmentActivity implements CustomerItemClickListener {
    protected static final String TAG = "FragmentActivity";
    private ViewPager customer_viewpager;
    private TextView txt_center;
    List<Fragment> mFragments;
    String[] mTitles = new String[]{
            "日", "周", "月"
    };

    private TextView left_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        initViews();
        initData();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        txt_center = (TextView) findViewById(R.id.center_text);
        left_text = (TextView) findViewById(R.id.left_text);
        customer_viewpager = (ViewPager) findViewById(R.id.customer_viewpager);
    }

    private void initData() {
        txt_center.setText("业绩");
        left_text.setVisibility(View.VISIBLE);
        left_text.setText("返回");
        left_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            mFragments = new ArrayList<>();
            mFragments.add(new AchievementOnDayFragment());
            mFragments.add(new AchievementOnWeekFragment());
            mFragments.add(new AchievementOnMonthFragment());
            BaseFragmentAdapter adapter =
                    new BaseFragmentAdapter(getSupportFragmentManager(), mFragments, mTitles);
            customer_viewpager.setAdapter(adapter);
            customer_viewpager.setOffscreenPageLimit(2);
            customer_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            PagerSlidingTabStrip tabLayout = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            tabLayout.setViewPager(customer_viewpager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemClick(View view, int postion) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }


}
