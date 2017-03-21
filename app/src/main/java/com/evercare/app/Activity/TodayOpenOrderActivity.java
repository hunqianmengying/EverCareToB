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
import com.evercare.app.adapter.TodayOpenOrderAdapter;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.OpenOrderItemInfo;
import com.evercare.app.model.TodayOpenOrderInfo;
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
 * 今日可开单界面
 */
public class TodayOpenOrderActivity extends BaseActivity {

    private RecyclerView rlv_customer_birthday;
    private TextView tvStickyHeaderView;
    private TextView txt_left;
    private TextView txt_center;

    private SwipeRefreshLayout refreshLayout;
    private Context context;

    private TodayOpenOrderAdapter adapter;
    private TodayOpenOrderInfo customerBirthdayInfo;

    private View loading_progress;

    private View no_value_view;
    private TextView txt_title_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerbirthday);
        context = TodayOpenOrderActivity.this;

        initView();
    }

    private void initView() {

        no_value_view = findViewById(R.id.no_value_view);

        no_value_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.setRefreshing(true);
                getData();
            }
        });
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);

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
        txt_center.setText("今日可开单");
        txt_title_info.setText("今日可开单");
        loading_progress = findViewById(R.id.loading_progress);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);

        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        refreshLayout.setProgressViewOffset(true, 80, 320);
        refreshLayout.setProgressViewEndTarget(true, 130);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                },  Constant.ON_REFRESH);
            }
        });
        //refreshLayout.setRefreshing(true);
       /* refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //getData();
                        //refreshLayout.setRefreshing(false);
                    }
                }).start();
            }
        });*/

        assert rlv_customer_birthday != null;
        assert tvStickyHeaderView != null;

        rlv_customer_birthday.setLayoutManager(new LinearLayoutManager(this));
        //DividerItemDecoration itemDecoration = new DividerItemDecoration(TodayOpenOrderActivity.this,LinearLayoutManager.VERTICAL);
        //rlv_customer_birthday.addItemDecoration(itemDecoration);

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


    }

    private void getData() {
        final JSONObject jsonObject = new JSONObject();
        HttpUtils.postWithJson(context, Constant.ARRIVETRIAGE_INDEX, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<TodayOpenOrderInfo>>() {
                                }.getType();
                                JsonResult<TodayOpenOrderInfo> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                customerBirthdayInfo = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && customerBirthdayInfo != null) {

                                    if ((customerBirthdayInfo.getOpenorder() != null && customerBirthdayInfo.getOpenorder().size() > 0) ||
                                            (customerBirthdayInfo.getFinshopenorder() != null && customerBirthdayInfo.getFinshopenorder().size() > 0)) {

                                        adapter = new TodayOpenOrderAdapter(TodayOpenOrderActivity.this, customerBirthdayInfo, new CustomerItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {

                                                int todayNum = customerBirthdayInfo.getOpenorder() == null ? 0 : customerBirthdayInfo.getOpenorder().size();
                                                int weekNum = customerBirthdayInfo.getFinshopenorder() == null ? 0 : customerBirthdayInfo.getFinshopenorder().size();

                                                OpenOrderItemInfo birthdayInfo = null;
                                                if (position < todayNum) {
                                                    birthdayInfo = customerBirthdayInfo.getOpenorder().get(position);
                                                } else if (position < (todayNum + weekNum)) {
                                                    birthdayInfo = customerBirthdayInfo.getFinshopenorder().get(position - todayNum);
                                                }
                                                if (view.getId() == R.id.txt_billing) {
                                                    Intent intent = new Intent(TodayOpenOrderActivity.this, PriceListActivity.class);
                                                    intent.putExtra("business_id", birthdayInfo.getBusiness_id());
                                                    intent.putExtra("business_cuid", birthdayInfo.getBusiness_cuid());
                                                    intent.putExtra("isSelectProject", true);
                                                    intent.putExtra("business_project_id", birthdayInfo.getProject_class());
                                                    intent.putExtra("custom_name", birthdayInfo.getCustom_name());
                                                    if (position < todayNum) {
                                                        intent.putExtra("startFlag", true);
                                                    }
                                                    startActivity(intent);
                                                } else {
                                                    Intent intent = new Intent(TodayOpenOrderActivity.this, OpenOrderActivity.class);
                                                    intent.putExtra("type", "今日");
                                                    intent.putExtra("module", "今日可开单");
                                                    intent.putExtra("id", birthdayInfo.getId());
                                                    intent.putExtra("business_id", birthdayInfo.getBusiness_id());
                                                    intent.putExtra("custom_id", birthdayInfo.getCustom_id());
                                                    intent.putExtra("statusType", birthdayInfo.getType());
                                                    intent.putExtra("business_cuid", birthdayInfo.getBusiness_cuid());
                                                    if (position < todayNum) {
                                                        intent.putExtra("startFlag", true);
                                                    }
                                                    startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onItemLongClick(View view, int position) {

                                            }
                                        });
                                        setHeaderText();
                                        rlv_customer_birthday.setAdapter(adapter);
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
                        refreshLayout.setRefreshing(false);
                        loading_progress.setVisibility(View.GONE);
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
        if (customerBirthdayInfo != null) {
            if (customerBirthdayInfo.getOpenorder() != null && customerBirthdayInfo.getOpenorder().size() > 0) {
                tvStickyHeaderView.setText("今日");
            } else if (customerBirthdayInfo.getFinshopenorder() != null && customerBirthdayInfo.getFinshopenorder().size() > 0) {
                tvStickyHeaderView.setText("已完成任务");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}
