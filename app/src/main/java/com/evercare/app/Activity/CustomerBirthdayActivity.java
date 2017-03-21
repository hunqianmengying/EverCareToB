package com.evercare.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.CustomerBirthdayAdapter;
import com.evercare.app.model.BirthdayInfo;
import com.evercare.app.model.CustomerBirthdayInfo;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 生日客户界面
 */
public class CustomerBirthdayActivity extends BaseActivity {

    private RecyclerView rlv_customer_birthday;
    private TextView tvStickyHeaderView;
    private TextView txt_left;
    private TextView txt_center;

    private SwipeRefreshLayout refreshLayout;
    private Context context;

    private CustomerBirthdayAdapter adapter;
    private CustomerBirthdayInfo customerBirthdayInfo;

    private View loading_progress;

    private View no_value_view;
    private TextView txt_title_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerbirthday);
        initView();
    }

    private void initView() {

        no_value_view = findViewById(R.id.no_value_view);
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);

        loading_progress = (View) findViewById(R.id.loading_progress);

        rlv_customer_birthday = (RecyclerView) findViewById(R.id.rlv_customer_birthday);
        tvStickyHeaderView = (TextView) findViewById(R.id.tv_sticky_header_view);
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

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);

        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        refreshLayout.setProgressViewOffset(true, 80, 320);
        refreshLayout.setProgressViewEndTarget(true, 130);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getBirthdayData();
                    }
                }, Constant.ON_REFRESH);
            }
        });


        txt_center.setText("生日客户");
        txt_title_info.setText("生日客户");

        assert rlv_customer_birthday != null;
        assert tvStickyHeaderView != null;

        rlv_customer_birthday.setLayoutManager(new LinearLayoutManager(this));
        //rlv_customer_birthday.setAdapter(adapter);
        rlv_customer_birthday.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Get the sticky information from the topmost view of the screen.
                View stickyInfoView = recyclerView.findChildViewUnder(
                        tvStickyHeaderView.getMeasuredWidth() / 2, 5);

                if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {
                    tvStickyHeaderView.setText(String.valueOf(stickyInfoView.getContentDescription()));
                }

                // Get the sticky view's translationY by the first view below the sticky's height.
                View transInfoView = recyclerView.findChildViewUnder(
                        tvStickyHeaderView.getMeasuredWidth() / 2, tvStickyHeaderView.getMeasuredHeight() + 1);

                if (transInfoView != null && transInfoView.getTag() != null) {
                    int transViewStatus = (int) transInfoView.getTag();
                    int dealtY = transInfoView.getTop() - tvStickyHeaderView.getMeasuredHeight();
                    if (transViewStatus == CustomerBirthdayAdapter.HAS_STICKY_VIEW) {
                        // If the first view below the sticky's height scroll off the screen,
                        // then recovery the sticky view's translationY.
                        if (transInfoView.getTop() > 0) {
                            tvStickyHeaderView.setTranslationY(dealtY);
                        } else {
                            tvStickyHeaderView.setTranslationY(0);
                        }
                    } else if (transViewStatus == CustomerBirthdayAdapter.NONE_STICKY_VIEW) {
                        tvStickyHeaderView.setTranslationY(0);
                    }
                }
            }
        });

        loading_progress.setVisibility(View.VISIBLE);

        getBirthdayData();
    }


    private void getBirthdayData() {

        context = CustomerBirthdayActivity.this;
        final JSONObject jsonObject = new JSONObject();

        HttpUtils.postWithJson(context, Constant.CUSTOM_BIRTHDAY, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<CustomerBirthdayInfo>>() {
                                }.getType();
                                JsonResult<CustomerBirthdayInfo> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                customerBirthdayInfo = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && customerBirthdayInfo != null) {

                                    int todayNum = customerBirthdayInfo.getBirthday() == null ? 0 : customerBirthdayInfo.getBirthday().size();
                                    int weekNum = customerBirthdayInfo.getBirthweek() == null ? 0 : customerBirthdayInfo.getBirthweek().size();
                                    if ((todayNum + weekNum) > 0) {
                                        adapter = new CustomerBirthdayAdapter(CustomerBirthdayActivity.this, customerBirthdayInfo, new CustomerItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent intent = new Intent(CustomerBirthdayActivity.this, CustomerInfoViewPagerActivity.class);
                                                customerBirthdayInfo.getBirthweek();

                                                int todayNum = customerBirthdayInfo.getBirthday() == null ? 0 : customerBirthdayInfo.getBirthday().size();
                                                int weekNum = customerBirthdayInfo.getBirthweek() == null ? 0 : customerBirthdayInfo.getBirthweek().size();

                                                BirthdayInfo birthdayInfo = null;
                                                if (position < todayNum) {
                                                    birthdayInfo = customerBirthdayInfo.getBirthday().get(position);
                                                } else if (position < (todayNum + weekNum)) {
                                                    birthdayInfo = customerBirthdayInfo.getBirthweek().get(position - todayNum);
                                                }

                                                intent.putExtra("custom_id", birthdayInfo.getCustom_id());
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onItemLongClick(View view, int position) {

                                            }
                                        });
                                        rlv_customer_birthday.setAdapter(adapter);
                                        setHeaderText();
                                    } else {
                                        no_value_view.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    no_value_view.setVisibility(View.VISIBLE);
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                no_value_view.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        loading_progress.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        loading_progress.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        loading_progress.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);

                    }
                }
        );
    }

    private void setHeaderText() {
        if (customerBirthdayInfo.getBirthday() != null && customerBirthdayInfo.getBirthday().size() > 0) {
            tvStickyHeaderView.setText("今日生日");
        } else if (customerBirthdayInfo.getBirthweek() != null && customerBirthdayInfo.getBirthweek().size() > 0) {
            tvStickyHeaderView.setText("本周生日");
        }
    }
}
