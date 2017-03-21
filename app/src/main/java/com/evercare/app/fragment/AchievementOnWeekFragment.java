package com.evercare.app.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.evercare.app.Activity.BaseFragment;
import com.evercare.app.Activity.LoginActivity;
import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.AddressListAdapter;
import com.evercare.app.adapter.TradeItemAdapter;
import com.evercare.app.model.AchievmentInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.DividerItemDecoration;
import com.evercare.app.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * 作者：xlren on 2016-11-15 16:03
 * 邮箱：renxianliang@126.com
 * 销售额业绩显示   周
 */
public class AchievementOnWeekFragment extends BaseFragment {
    private RecyclerView achievRecyclerView;
    private AchievmentInfo achievmentInfos;


    private View no_value_view;
    private TextView txt_title_info;
    private TradeItemAdapter tradeItemAdapter;
    private ProgressBar loading_progress;
    private SwipeRefreshLayout refreshLayout;
    private int page = 1;
    private boolean loadMore = true;
    private Context mContext;

    private TextView sales_volume;
    private TextView received_volume;


    protected void initView(View view) {
        no_value_view = view.findViewById(R.id.no_value_view);
        txt_title_info = (TextView) view.findViewById(R.id.txt_title_info);
        txt_title_info.setText("当周销售额");

        loading_progress = (ProgressBar) view.findViewById(R.id.loading_progress);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        achievRecyclerView = (RecyclerView) view.findViewById(R.id.achievRecyclerView);
        achievRecyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        achievRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        achievRecyclerView.addItemDecoration(decoration);

        sales_volume = (TextView) view.findViewById(R.id.sales_volume);
        received_volume = (TextView) view.findViewById(R.id.received_volume);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_achievment;
    }

    protected void initData() {
        mContext = getActivity();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "2");
            jsonObject.put("page", String.valueOf(page));
            jsonObject.put("row", Constant.TEXT_ROW);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.postWithJson(mContext, Constant.ACHIEVEMENT_INDEX, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {

                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonResult<AchievmentInfo>>() {
                        }.getType();
                        JsonResult<AchievmentInfo> jsonResult = gson.fromJson(response.toString(), type);
                        String code = jsonResult.getCode();
                        achievmentInfos = jsonResult.getData();
                        if (TextUtils.equals(code, Result.SUCCESS) && achievmentInfos != null) {
                            sales_volume.setText(achievmentInfos.getSale_count());
                            received_volume.setText(achievmentInfos.getCash());
                            if (achievmentInfos.getSale_list() != null && achievmentInfos.getSale_list().size() > 0) {
                                if (page > 1) {
                                    tradeItemAdapter.addFooterItem(achievmentInfos.getSale_list());
                                } else {
                                    tradeItemAdapter = new TradeItemAdapter(getContext(), achievmentInfos.getSale_list(), achievmentInfos, "周");
                                    achievRecyclerView.setAdapter(tradeItemAdapter);
                                }

                                if (achievmentInfos.getSale_list().size() >= Constant.TEXT_ROW) {
                                    //设置回到上拉加载更多
                                    tradeItemAdapter.changeMoreStatus(Constant.PULLUP_LOAD_MORE);
                                    loadMore = true;
                                } else {
                                    //设置回到无数据加载
                                    tradeItemAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                    loadMore = false;
                                }
                            } else {
                                if (tradeItemAdapter == null) {
                                    no_value_view.setVisibility(View.VISIBLE);
                                } else {
                                    //设置回到无数据加载
                                    tradeItemAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                    loadMore = false;
                                }
                            }

                           /* if(page>1){
                                tradeItemAdapter.addFooterItem(achievmentInfos.getSale_list());
                                //设置回到上拉加载更多
                                tradeItemAdapter.changeMoreStatus(Constant.PULLUP_LOAD_MORE);
                                loadMore = true;
                            }else if(achievmentInfos.getSale_list().size()>0){
                                tradeItemAdapter = new TradeItemAdapter(getContext(),  achievmentInfos.getSale_list(),achievmentInfos,"周");
                                achievRecyclerView.setAdapter(tradeItemAdapter);
                            }else{
                                no_value_view.setVisibility(View.VISIBLE);
                            }*/
                        } else {
                            if (tradeItemAdapter == null) {
                                no_value_view.setVisibility(View.VISIBLE);
                            } else {
                                //设置回到无数据加载
                                tradeItemAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                loadMore = false;
                            }
                        }
                    } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                        Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else if (page > 1) {
                        //没有加载更多了
                        tradeItemAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                        loadMore = false;
                    } else {
                        no_value_view.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                showProgress(false);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                no_value_view.setVisibility(View.VISIBLE);
                showProgress(false);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                no_value_view.setVisibility(View.VISIBLE);
                showProgress(false);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 下拉刷新
     */
    private void onRefresh() {
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        refreshLayout.setProgressViewOffset(true, 80, 320);
        refreshLayout.setProgressViewEndTarget(true, 130);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                loadMore = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                }, Constant.ON_REFRESH);

            }
        });
    }

    /**
     * 加载更多
     */
    private void initLoadMoreListener() {
        achievRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (tradeItemAdapter == null)
                    return;
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == tradeItemAdapter.getItemCount()) {
                    if (tradeItemAdapter.getItemCount() < Constant.TEXT_ROW) {
                        tradeItemAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                        return;
                    }
                    if (loadMore) {
                        loadMore = false;//防止多次点击
                        page++;
                        //设置正在加载更多
                        tradeItemAdapter.changeMoreStatus(Constant.LOADING_MORE);
                        //改为网络请求
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initData();
                            }
                        }, 1000);
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh();
        initLoadMoreListener();
        showProgress(true);
    }

    @Override
    public void fetchData() {

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
