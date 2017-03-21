package com.evercare.app.fragment;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.evercare.app.Activity.BaseFragment;
import com.evercare.app.Activity.LoginActivity;
import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.CustomerTradeItemAdapter;
import com.evercare.app.model.TradeRatesInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.DividerItemDecoration;
import com.evercare.app.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-11-16 10:50
 * 邮箱：842202389@qq.com
 * 新客成交率/老客成交率Fragment
 */
public class CustomerTradeRatesFragment extends BaseFragment {
    private TextView txt_arrive_num;
    private TextView txt_deal_num;
    private TextView txt_trade_rate_num;

    private RecyclerView rlv_trade_list;

    private List<String> list;

    private Spinner sp_data_type;

    private ArrayAdapter sp_adapter;

    private String time_type = "1"; // 1:昨日,2:本周,3:当月

    private String arriver_type = "1";  //  1,初诊,2复诊,3治疗,4在消费

    private Context context;

    private TradeRatesInfo tradeRatesInfo;

    private ProgressBar loading_progress;

    private View no_value_view;
    private TextView txt_title_info;

    private RelativeLayout rlv_trade;
    private RecyclerViewHeader recyclerViewHeader;

    private SwipeRefreshLayout refreshLayout;

    public static CustomerTradeRatesFragment newInstance(String arriver_type) {
        CustomerTradeRatesFragment fragment = new CustomerTradeRatesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("arriver_type", arriver_type);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected void initView(View view) {

        context = getContext();

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
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
                }, Constant.ON_REFRESH);
            }
        });


        rlv_trade = (RelativeLayout) view.findViewById(R.id.rlv_trade);
        no_value_view = view.findViewById(R.id.no_value_view);
        no_value_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.setRefreshing(true);
                getData();
            }
        });
        txt_title_info = (TextView) view.findViewById(R.id.txt_title_info);

        arriver_type = getArguments().getString("arriver_type");
        switch (arriver_type) {
            case "1":
                txt_title_info.setText("新客初诊");
                break;
            case "2":
                txt_title_info.setText("新客复诊");
                break;
            case "3":
                txt_title_info.setText("老客治疗");
                break;
            case "4":
                txt_title_info.setText("老客再消费");
                break;
        }


        loading_progress = (ProgressBar) view.findViewById(R.id.loading_progress);

        sp_data_type = (Spinner) view.findViewById(R.id.sp_data_type);

        txt_arrive_num = (TextView) view.findViewById(R.id.txt_arrive_num);
        txt_deal_num = (TextView) view.findViewById(R.id.txt_deal_num);
        txt_trade_rate_num = (TextView) view.findViewById(R.id.txt_trade_rate_num);

        rlv_trade_list = (RecyclerView) view.findViewById(R.id.rlv_trade_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rlv_trade_list.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL);
        rlv_trade_list.addItemDecoration(itemDecoration);
        recyclerViewHeader = (RecyclerViewHeader) view.findViewById(R.id.recyclerview_header);
        recyclerViewHeader.attachTo(rlv_trade_list, true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_traderate;
    }

    @Override
    public void fetchData() {

    }

    protected void initData() {

        list = new ArrayList<>();
        list.add("天");
        list.add("周");
        list.add("月");

        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        sp_adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, list);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        sp_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        sp_data_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                time_type = String.valueOf(position + 1);
                loading_progress.setVisibility(View.VISIBLE);
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //第四步：将适配器添加到下拉列表上
        sp_data_type.setAdapter(sp_adapter);
    }


    private void getData() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("arriver_type", arriver_type);
            jsonObject.put("type", time_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.postWithJson(context, Constant.ACHIEVEMENT_TRANSACTION, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonResult<TradeRatesInfo>>() {
                        }.getType();
                        JsonResult<TradeRatesInfo> jsonResult = gson.fromJson(response.toString(), type);
                        String code = jsonResult.getCode();
                        tradeRatesInfo = jsonResult.getData();
                        if (TextUtils.equals(code, Result.SUCCESS) && tradeRatesInfo != null) {
                            txt_arrive_num.setText(String.valueOf(tradeRatesInfo.getTotal()));
                            txt_deal_num.setText(String.valueOf(tradeRatesInfo.getConsumption_total()));
                            txt_trade_rate_num.setText(String.valueOf(tradeRatesInfo.getPercent()));

                            if (tradeRatesInfo.getLists() != null && tradeRatesInfo.getLists().size() > 0) {
                                CustomerTradeItemAdapter adapter = new CustomerTradeItemAdapter(getContext(), R.layout.trade_item, tradeRatesInfo.getLists());
                                rlv_trade_list.setAdapter(adapter);
                                rlv_trade_list.setVisibility(View.VISIBLE);
                                no_value_view.setVisibility(View.GONE);
                            } else {
                                rlv_trade_list.setVisibility(View.GONE);
                                no_value_view.setVisibility(View.VISIBLE);
                            }
                        } else {
                            txt_arrive_num.setText("0");
                            txt_deal_num.setText("0");
                            txt_trade_rate_num.setText("0.0");

                            rlv_trade_list.setVisibility(View.GONE);
                            no_value_view.setVisibility(View.VISIBLE);
                        }
                    } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                        Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        txt_arrive_num.setText("0");
                        txt_deal_num.setText("0");
                        txt_trade_rate_num.setText("0.0");
                        rlv_trade_list.setVisibility(View.GONE);
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
                rlv_trade_list.setVisibility(View.GONE);
                txt_arrive_num.setText("0");
                txt_deal_num.setText("0");
                txt_trade_rate_num.setText("0.0");
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loading_progress.setVisibility(View.GONE);
                no_value_view.setVisibility(View.VISIBLE);
                rlv_trade_list.setVisibility(View.GONE);
                txt_arrive_num.setText("0");
                txt_deal_num.setText("0");
                txt_trade_rate_num.setText("0.0");
                refreshLayout.setRefreshing(false);
            }
        });
    }
}
