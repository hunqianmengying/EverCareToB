package com.evercare.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import com.evercare.app.adapter.MaturingCustomerAdapter;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.MaturingCustomerInfo;
import com.evercare.app.model.MaturingItemInfo;
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
 * 即将到期客户界面
 */
public class MaturingCustomerActivity extends BaseActivity {

    private RecyclerView rlv_customer_birthday;
    private TextView tvStickyHeaderView;
    private TextView txt_left;
    private TextView txt_center;

    private SwipeRefreshLayout refreshLayout;
    private Context context;

    private MaturingCustomerAdapter adapter;
    private MaturingCustomerInfo customerBirthdayInfo;

    private View loading_progress;
    private View no_value_view;
    private TextView txt_title_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerbirthday);
        context = MaturingCustomerActivity.this;
        initView();
    }


    private void initView() {

        no_value_view = findViewById(R.id.no_value_view);
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

        loading_progress = findViewById(R.id.loading_progress);
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
                        getData();
                    }
                }, Constant.ON_REFRESH);
            }
        });


        txt_center.setText("即将到期客户");
        txt_title_info.setText("即将到期客户");

        assert rlv_customer_birthday != null;
        assert tvStickyHeaderView != null;

        rlv_customer_birthday.setLayoutManager(new LinearLayoutManager(this));
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

        showProgress(true);
        getData();
    }


    private void getData() {
        final JSONObject jsonObject = new JSONObject();
        HttpUtils.postWithJson(context, Constant.CUSTOM_EXPIRING, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<MaturingCustomerInfo>>() {
                                }.getType();
                                JsonResult<MaturingCustomerInfo> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                customerBirthdayInfo = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && customerBirthdayInfo != null) {
                                    int num1 = customerBirthdayInfo.getPrivatelist() == null ? 0 : customerBirthdayInfo.getPrivatelist().size();
                                    int num2 = customerBirthdayInfo.getTmplist() == null ? 0 : customerBirthdayInfo.getTmplist().size();

                                    if ((num1 + num2) > 0) {
                                        adapter = new MaturingCustomerAdapter(MaturingCustomerActivity.this, customerBirthdayInfo, new CustomerItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent intent = new Intent(MaturingCustomerActivity.this, CustomerInfoViewPagerActivity.class);

                                                int todayNum = customerBirthdayInfo.getTmplist() == null ? 0 : customerBirthdayInfo.getTmplist().size();
                                                int weekNum = customerBirthdayInfo.getPrivatelist() == null ? 0 : customerBirthdayInfo.getPrivatelist().size();

                                                MaturingItemInfo birthdayInfo = null;
                                                if (position < todayNum) {
                                                    birthdayInfo = customerBirthdayInfo.getTmplist().get(position);
                                                } else if (position < (todayNum + weekNum)) {
                                                    birthdayInfo = customerBirthdayInfo.getPrivatelist().get(position - todayNum);
                                                }

                                                intent.putExtra("custom_id", birthdayInfo.getCustom_id());
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onItemLongClick(View view, int position) {

                                            }
                                        });
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
                        showProgress(false);
                        setHeaderText();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        showProgress(false);
                        no_value_view.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);
                        tvStickyHeaderView.setText("");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        showProgress(false);
                        no_value_view.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);
                        tvStickyHeaderView.setText("");
                    }
                }
        );
    }

    private void setHeaderText() {
        if (customerBirthdayInfo != null) {
            if (customerBirthdayInfo.getTmplist() != null && customerBirthdayInfo.getTmplist().size() > 0) {
                tvStickyHeaderView.setText("临时库");
            } else if (customerBirthdayInfo.getPrivatelist() != null && customerBirthdayInfo.getPrivatelist().size() > 0) {
                tvStickyHeaderView.setText("私海");
            } else {
                tvStickyHeaderView.setText("");
            }
        } else {
            tvStickyHeaderView.setText("");
        }
    }

    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            loading_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            loading_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loading_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            loading_progress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
