package com.evercare.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.fragment.CustomerTradeRatesFragment;
import com.evercare.app.view.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：LXQ on 2016-11-16 10:15
 * 邮箱：842202389@qq.com
 * 新客成交率，老客成交率
 */
public class TradeRatesActivity extends FragmentActivity {
    private TextView txt_left;
    private TextView txt_center;
    private ViewPager vp_trade_rates;

    private String type;

    private PagerSlidingTabStrip layout_tab;

    private List<Fragment> fragmentList;

    private String[] titleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traderates);

        type = getIntent().getStringExtra("type");
        if (TextUtils.equals("新客成交率", type)) {
            titleList = new String[]{"初诊", "复诊"};
        } else {
            titleList = new String[]{"治疗", "再消费"};
        }

        initView();

        initData();

    }

    private void initView() {
        layout_tab = (PagerSlidingTabStrip) findViewById(R.id.layout_tab);

        txt_left = (TextView) findViewById(R.id.left_text);
        txt_center = (TextView) findViewById(R.id.center_text);
        txt_left.setText("返回");
        txt_left.setVisibility(View.VISIBLE);
        txt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_center.setText(type);

        vp_trade_rates = (ViewPager) findViewById(R.id.vp_trade_rates);
        vp_trade_rates.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
    }

    private void initData() {
        fragmentList = new ArrayList<>();
        //  1,初诊,2复诊,3治疗,4在消费
        if (TextUtils.equals(type, "新客成交率")) {
            fragmentList.add(CustomerTradeRatesFragment.newInstance("1"));
            fragmentList.add(CustomerTradeRatesFragment.newInstance("2"));

        } else if (TextUtils.equals(type, "老客成交率")) {
            fragmentList.add(CustomerTradeRatesFragment.newInstance("3"));
            fragmentList.add(CustomerTradeRatesFragment.newInstance("4"));
        }


        BaseFragmentAdapter adapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList, titleList);
        vp_trade_rates.setAdapter(adapter);

        layout_tab.setViewPager(vp_trade_rates);
    }
}
