package com.evercare.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.FirstOpenOrderPriceListAdapter;
import com.evercare.app.adapter.OpenOrderPriceListAdapter;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.OpenOrderPriceInfo;
import com.evercare.app.model.PriceInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：xlren on 2017-2-27 13:56
 * 邮箱：renxianliang@126.com
 * 今日已开单列表详情
 */
public class OpenOrderPriceListActivity extends Activity {

    private RecyclerView rlv_price_list;
    private TextView txt_left;
    private TextView txt_center;
    private View view_bottom_line;
    private Context context;
    private ProgressBar loading_progress;
    private View no_value_view;
    private TextView txt_title_info;
    private TextView no_result;

    private FirstOpenOrderPriceListAdapter priceListAdapter;
    private SwipeRefreshLayout refreshLayout;

    private String custom_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_order_activity_pricelist);
        context = this;
        Intent intent = getIntent();
        custom_id = intent.getStringExtra("custom_id");
        initView();
        showProgress(true);
        initData();
        onRefresh();
    }


    private void initView() {
        no_value_view = findViewById(R.id.no_value_view);
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        rlv_price_list = (RecyclerView) findViewById(R.id.rlv_price_list);
        txt_left = (TextView) findViewById(R.id.left_text);
        txt_left.setVisibility(View.VISIBLE);
        txt_left.setText("返回");
        txt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txt_center = (TextView) findViewById(R.id.center_text);
        txt_center.setText("今日已开单详情");
        txt_title_info.setText("今日已开单详情");
        no_result = (TextView) findViewById(R.id.no_result);
        view_bottom_line = findViewById(R.id.bottom_line);
        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
        view_bottom_line.setVisibility(View.INVISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(OpenOrderPriceListActivity.this, LinearLayoutManager.VERTICAL, false);
        rlv_price_list.setLayoutManager(layoutManager);
    }

    private void initData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("custom_id", custom_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.SALESORDER_DETAIL, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data")) && !"[]".equals(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonListResult<OpenOrderPriceInfo>>() {
                                }.getType();
                                JsonListResult<OpenOrderPriceInfo> jsonResult = gson.fromJson(response.toString(), type);
                                String code = jsonResult.getCode();
                                List<OpenOrderPriceInfo> priceInfos = jsonResult.getData();

                                if (TextUtils.equals(code, Result.SUCCESS) && priceInfos.size() > 0) {
                                        no_value_view.setVisibility(View.GONE);

                                        priceListAdapter = new FirstOpenOrderPriceListAdapter(OpenOrderPriceListActivity.this, priceInfos);

                                        rlv_price_list.setAdapter(priceListAdapter);

                                        rlv_price_list.setVisibility(View.VISIBLE);


                                }  else {
                                    no_value_view.setVisibility(View.VISIBLE);
                                    no_result.setText("为获取到");
                                    rlv_price_list.setVisibility(View.GONE);
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                no_value_view.setVisibility(View.VISIBLE);
                                no_result.setText("未获取到");
                                rlv_price_list.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgress(false);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                            errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);

                        loading_progress.setVisibility(View.GONE);
                        no_result.setText("未获取到");
                        refreshLayout.setRefreshing(false);
                        showProgress(false);
                        Toast.makeText(context, getResources().getString(R.string.net_work_error), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);

                        loading_progress.setVisibility(View.GONE);
                        no_result.setText("未获取到");
                        refreshLayout.setRefreshing(false);
                        showProgress(false);
                        Toast.makeText(context, getResources().getString(R.string.net_work_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    private void onRefresh() {
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
                        initData();
                    }
                }, Constant.ON_REFRESH);
            }
        });
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
