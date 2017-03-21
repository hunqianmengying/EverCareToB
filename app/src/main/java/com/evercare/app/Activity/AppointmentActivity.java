package com.evercare.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.SampleRecyclerAdapter;
import com.evercare.app.model.AppointmentItemInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-9-9 10:56
 * 邮箱：842202389@qq.com
 * 预约面板
 */
public class AppointmentActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private SampleRecyclerAdapter adapter;
    private TextView center_text;

    private List<AppointmentItemInfo> appointmentItemInfos;

    private ProgressBar loading_progress;
    private TextView left_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_appointment);
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {

        left_text = (TextView) findViewById(R.id.left_text);
        left_text.setVisibility(View.VISIBLE);
        left_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        left_text.setText("返回");

        center_text = (TextView) findViewById(R.id.center_text);
        center_text.setText("我的预约");

        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        refreshLayout.setProgressViewOffset(true, 80, 320);
        refreshLayout.setProgressViewEndTarget(true, 130);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        appointmentItemInfos.clear();
                        for (int i = 0; i < 20; i++) {
                            AppointmentItemInfo item = new AppointmentItemInfo();
                            item.setPage_count(1);
                            item.setAppointment_id("232212");
                            item.setAppointment_project("双眼");
                            item.setAppointment_status("" + (i % 3));
                            item.setPage_count(1);
                            item.setAppointment_time("2016-11-21");
                            item.setCustomer_name("李思" + i);
                            appointmentItemInfos.add(item);
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(1);
                    }
                }).start();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        getData();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    refreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    private void getData() {
        showProgress(true);

        RequestParams params = new RequestParams();
        params.put("uid", 111);
        params.put("page", 0);
        params.put("row", 10);

        HttpUtils.get(Constant.GET_USER_APPOINTMENT, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Gson gson = new Gson();
                Type type = new TypeToken<JsonListResult<AppointmentItemInfo>>() {
                }.getType();
                JsonListResult<AppointmentItemInfo> jsonResult = gson.fromJson(new String(responseBody), type);
                String status = jsonResult.getStatus();

                if (TextUtils.equals(status, Result.SUCCESS)) {
                    appointmentItemInfos = jsonResult.getData();
                    initAdapter();
                } else if (Result.SIGNATURE_ERROR.equals(statusCode)) {
                    Toast.makeText(AppointmentActivity.this, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AppointmentActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                showProgress(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                appointmentItemInfos = new ArrayList<AppointmentItemInfo>();
                for (int i = 0; i < 10; i++) {
                    AppointmentItemInfo item = new AppointmentItemInfo();
                    item.setPage_count(1);
                    item.setAppointment_id("232");
                    item.setAppointment_project("丰胸");
                    item.setAppointment_status("" + (i % 3));
                    item.setPage_count(1);
                    item.setAppointment_time("2016-09-21");
                    item.setCustomer_name("张三丰" + i);
                    appointmentItemInfos.add(item);
                    initAdapter();
                    showProgress(false);
                }
            }
        });
    }

    private void initAdapter() {
        if (appointmentItemInfos != null && appointmentItemInfos.size() > 0) {
            adapter = new SampleRecyclerAdapter(appointmentItemInfos);
            adapter.setOnSwipeClickListener(new SampleRecyclerAdapter.OnSwipeClickListener() {

                @Override
                public boolean onChildClick(int position, int type) {
                    RequestParams params = new RequestParams();
                    params.put("appointment_id", appointmentItemInfos.get(position).getAppointment_id());

                    switch (type) {
                        case 1://TYPE_AGREE
                            Toast.makeText(AppointmentActivity.this, "同意！", Toast.LENGTH_SHORT).show();
                            HttpUtils.post(Constant.AGREE_APPOINTMENT, params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                }
                            });

                            break;
                        case 2://TYPE_CANCLE
                            Toast.makeText(AppointmentActivity.this, "取消！", Toast.LENGTH_SHORT).show();
                            HttpUtils.post(Constant.CANCLE_APPOINTMENT, params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                }
                            });
                            break;
                        case 3:
                            Toast.makeText(AppointmentActivity.this, "删除！", Toast.LENGTH_SHORT).show();
                            HttpUtils.post(Constant.DELETE_APPOINTMENT, params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    //lists.remove(position);
                                    //adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                }
                            });

                            break;//TYPE_DELETE
                    }

                    return false;
                }
            });

            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });

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
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

