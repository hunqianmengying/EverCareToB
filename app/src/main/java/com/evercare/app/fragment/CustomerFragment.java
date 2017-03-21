package com.evercare.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evercare.app.Activity.BaseFragmentAdapter;
import com.evercare.app.Activity.SearchCustomerActivity;
import com.evercare.app.R;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.util.Constant;
import com.evercare.app.view.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：xlren on 2016/8/29 13:23
 * 邮箱：renxianliang@126.com
 * 客户列表
 */

public class CustomerFragment extends Fragment implements CustomerItemClickListener {
    protected static final String TAG = "CustomerFragment";

    private ViewPager customer_viewpager;
    private TextView txt_center;

    private ImageView img_right;
    private String type = Constant.TEMPORARY;

    private List<Fragment> fragmentList;

    private String[] mTitles = new String[]{
            "临时库", "私有库", "关联客户"
    };

    PagerSlidingTabStrip tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        initViews(view);
        initData();
        return view;
    }

    /**
     * 初始化View
     */
    private void initViews(View view) {
        txt_center = (TextView) view.findViewById(R.id.center_text);
        customer_viewpager = (ViewPager) view.findViewById(R.id.customer_viewpager);

        img_right = (ImageView) view.findViewById(R.id.right_image);
        img_right.setImageResource(R.drawable.ic_search_customer);
        img_right.setVisibility(View.VISIBLE);
        img_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == Constant.TEMPORARY) {
                    MobclickAgent.onEvent(getContext(), "search_tem_customer");
                } else if (type == Constant.PRIVATE_SEA) {
                    MobclickAgent.onEvent(getContext(), "search_private_customer");
                } else if (type == Constant.RELATECUSTOMER) {

                }
                Intent intent = new Intent(getContext(), SearchCustomerActivity.class);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        tabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);

    }

    private void initData() {
        txt_center.setText("客户");
        final PrivateBaseFragment privateBaseFragment = new PrivateBaseFragment();
        final RelateCustomerFragment relateCustomerFragment = new RelateCustomerFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(new TempBaseFragment());
        fragmentList.add(privateBaseFragment);
        fragmentList.add(relateCustomerFragment);

        BaseFragmentAdapter adapter =
                new BaseFragmentAdapter(getChildFragmentManager(), fragmentList, mTitles);

        try {
            customer_viewpager.setAdapter(adapter);

            customer_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        type = Constant.TEMPORARY;
                    } else if (position == 1) {
                        type = Constant.PRIVATE_SEA;
                        privateBaseFragment.initData();
                    } else {
                        type = Constant.RELATECUSTOMER;
                        relateCustomerFragment.initData();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        tabLayout.setViewPager(customer_viewpager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClick(View view, int postion) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
