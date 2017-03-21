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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.NewsAdapter;
import com.evercare.app.adapter.TodayWorkAdapter;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.NewsItemInfo;
import com.evercare.app.model.TodayWorkInfo;
import com.evercare.app.model.TodayWorkItemInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-11-23 10:19
 * 邮箱：842202389@qq.com
 * 首页消息界面
 */
public class NewsActivity extends BaseActivity {

    private List<NewsItemInfo> mDatas = new ArrayList<>();

    private TextView txt_left;
    private TextView txt_center;
    private RecyclerView rlv_news;

    private ProgressBar loading_progress;

    private Context context;
    private boolean loadMore = true;
    private NewsAdapter adapter;
    private int page = 1;

    private View no_value_view;
    private TextView txt_title_info;
    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        context = NewsActivity.this;
        initView();

        showProgress(true);
        getNews();
        initLoadMoreListener();
        onRefresh();
    }

    private void initView() {

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);

        no_value_view = findViewById(R.id.no_value_view);

        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);
        txt_title_info.setText("消息");
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
        txt_center.setText("消息");

        rlv_news = (RecyclerView) findViewById(R.id.rlv_news);
        rlv_news.setLayoutManager(new LinearLayoutManager(this));
    }


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
                        getNews();
                    }
                }, Constant.ON_REFRESH);
            }
        });
    }


    private void getNews() {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("row", String.valueOf(Constant.TEXT_ROW));
            //jsonObject.put("row", "5");
            jsonObject.put("page", String.valueOf(page));
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.PUSH_MESSAGE, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonListResult<NewsItemInfo>>() {
                                }.getType();
                                JsonListResult<NewsItemInfo> jsonResult = gson.fromJson(response.toString(), type);
                                String code = jsonResult.getCode();
                                List<NewsItemInfo> jsonResultData = jsonResult.getData();
                                if (TextUtils.equals(code, Result.SUCCESS) && jsonResultData != null && jsonResultData.size() > 0) {
                                    if (page > 1) {
                                        //mDatas.addAll(jsonResultData);
                                        adapter.addFooterItem(jsonResultData);
                                        //设置回到上拉加载更多
                                        adapter.changeMoreStatus(Constant.PULLUP_LOAD_MORE);
                                        loadMore = true;

                                    } else {
                                        mDatas.clear();
                                        mDatas = jsonResultData;
                                        adapter = new NewsAdapter(NewsActivity.this, jsonResultData);

                                        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                switch (view.getId()) {
                                                    case R.id.txt_turn:
                                                        if (!TextUtils.isEmpty(mDatas.get(position).getUrl())) {
                                                            Intent intent = new Intent(NewsActivity.this, InformationMessageActivity.class);
                                                            intent.putExtra("name", mDatas.get(position).getTitle());
                                                            intent.putExtra("url", mDatas.get(position).getUrl());
                                                            startActivity(intent);
                                                        }
                                                        break;
                                                    default:
                                                        final TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
                                                        final View red_point = view.findViewById(R.id.red_point);
                                                        red_point.setVisibility(View.INVISIBLE);
                                                        NewsItemInfo item = mDatas.get(position);
                                                        if (TextUtils.equals(item.getStatus(), "0")) {
                                                            setNewsRead(item.getId());
                                                        }

                                                        if (mDatas.get(position).isSelected) {
                                                            mDatas.get(position).isSelected = false;
                                                            txt_content.setEllipsize(null); // 展开
                                                            txt_content.setSingleLine(false);
                                                        } else {
                                                            mDatas.get(position).isSelected = true;
                                                            txt_content.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                                                            txt_content.setLines(2);
                                                        }
                                                        break;
                                                }
                                            }
                                        });
                                        rlv_news.setAdapter(adapter);
                                    }
                                    no_value_view.setVisibility(View.GONE);
                                    rlv_news.setVisibility(View.VISIBLE);
                                } else if (page > 1) {
                                    //没有加载更多了
                                    adapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                    loadMore = false;
                                } else {
                                    no_value_view.setVisibility(View.VISIBLE);
                                    rlv_news.setVisibility(View.GONE);
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else if (page > 1) {
                                //没有加载更多了
                                adapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                loadMore = false;
                            } else {
                                no_value_view.setVisibility(View.VISIBLE);
                                rlv_news.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgress(false);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable
                            throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        no_value_view.setVisibility(View.VISIBLE);
                        rlv_news.setVisibility(View.GONE);
                        showProgress(false);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String
                            responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        no_value_view.setVisibility(View.VISIBLE);
                        rlv_news.setVisibility(View.GONE);
                        showProgress(false);
                        refreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void setNewsRead(String id) {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.PUSH_READ, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                if (response.getBoolean("data")) {
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        loading_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable
                            throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        loading_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String
                            responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        loading_progress.setVisibility(View.GONE);
                    }
                }
        );
    }

    /**
     * 加载更多
     */
    private void initLoadMoreListener() {
        rlv_news.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(adapter==null)
                    return;
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    if(adapter.getItemCount()<Constant.TEXT_ROW){
                        adapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                        return;
                    }
                    if (loadMore) {
                        loadMore = false;//防止多次点击
                        page++;
                        //设置正在加载更多
                        adapter.changeMoreStatus(Constant.LOADING_MORE);
                        //改为网络请求
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getNews();
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
