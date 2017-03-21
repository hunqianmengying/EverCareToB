package com.evercare.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Activity.BaseFragment;
import com.evercare.app.Activity.LoginActivity;
import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.OrderProjectAdapter;
import com.evercare.app.adapter.SaleCureAdapter;
import com.evercare.app.model.OrderProjectInfo;
import com.evercare.app.model.SaleCureInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-11-3 15:31
 * 邮箱：842202389@qq.com
 * 客户详情  消费记录
 */
public class Sale_CureFragment extends BaseFragment {
    RecyclerView mRecyclerView;
    private static final String KEY = "key";
    private static String ARGUMENT = "custom_id";
    private String title = "消费记录";
    private SaleCureAdapter adapter;

    private ListView lsv_type;
    private List<String> typeList;

    private ArrayAdapter typeAdapter;

    private PopupWindow typePopupWindow;

    private ImageView img_select_item;

    //private List<SaleCureInfo> saleCureInfoList;

    private Context context;
    private View no_value_view;

    private String custom_id;

    private String type = "0";

    private ProgressBar loading_progress;
    private TextView txt_title_info;

    private int page = 1;
    private boolean loadMore = true;


    public static Sale_CureFragment newInstance(String custom_id) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, custom_id);
        Sale_CureFragment contentFragment = new Sale_CureFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }


    /**
     * 加载更多
     */
    private void initLoadMoreListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (adapter != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                        if (adapter.getItemCount() < Constant.TEXT_ROW) {
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
                                    //搜索时隐藏无数据画面  显示动画
                                    no_value_view.setVisibility(View.GONE);
                                    searchData();

                                }
                            }, 1000);
                        }
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
    protected void initView(View view) {
        context = getContext();

        Bundle arguments = getArguments();
        if (arguments != null) {
            custom_id = arguments.getString(ARGUMENT);
        }

        txt_title_info = (TextView) view.findViewById(R.id.txt_title_info);
        txt_title_info.setText(title);

        no_value_view = (View) view.findViewById(R.id.no_value_view);

        loading_progress = (ProgressBar) view.findViewById(R.id.loading_progress);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        img_select_item = (ImageView) view.findViewById(R.id.img_select_item);
        img_select_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypePopupWindow(v);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        //DividerItemDecoration itemDecoration = new DividerItemDecoration(mContext,LinearLayoutManager.VERTICAL);
        //mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);
        initLoadMoreListener();
        loading_progress.setVisibility(View.VISIBLE);

        searchData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sale_cure_list;
    }

    @Override
    public void fetchData() {

    }

    private void searchData() {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("custom_id", custom_id);
            //jsonObject.put("custom_id", "412525");
            jsonObject.put("row", Constant.TEXT_ROW);
            jsonObject.put("page", String.valueOf(page));

            if (!TextUtils.equals(type, "0")) {
                jsonObject.put("type", type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.CUSTOM_CONSUMPTION, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonListResult<SaleCureInfo>>() {
                                }.getType();
                                JsonListResult<SaleCureInfo> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                List<SaleCureInfo> data = jsonResult.getData();

                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && data != null && data.size() > 0) {
                                    if (page > 1) {
                                        if (data.size() < Constant.TEXT_ROW) {
                                            //没有加载更多了
                                            adapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                            loadMore = false;
                                        } else {
                                            loadMore = true;
                                        }
                                        adapter.addFooterItem(data);
                                    } else {
                                        if (adapter != null) {
                                            adapter.initData(data);
                                        } else {
                                            adapter = new SaleCureAdapter(context, data);
                                            mRecyclerView.setAdapter(adapter);
                                        }
                                        if (data.size() < Constant.TEXT_ROW) {
                                            //没有加载更多了
                                            adapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                            loadMore = false;
                                        }
                                    }

                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    no_value_view.setVisibility(View.GONE);

                                } else {
                                    if (adapter != null && adapter.getItemCount() > 0) {
                                        //没有加载更多了
                                        adapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                        loadMore = false;
                                    } else {
                                        mRecyclerView.setVisibility(View.GONE);
                                        no_value_view.setVisibility(View.VISIBLE);
                                    }
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
                                //无数据
                                mRecyclerView.setVisibility(View.GONE);
                                no_value_view.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        loading_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        loading_progress.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        loading_progress.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                    }
                }
        );
    }


    public void initData() {
        typeList = new ArrayList<>();

        typeList.add("全部");
        typeList.add("产品、药品收款");
        typeList.add("治疗");
        typeList.add("换卡费用");
        typeList.add("项目收款");
        typeList.add("辅助项目收款");
        typeList.add("内部用料");

        typeAdapter = new ArrayAdapter(getContext(), R.layout.listtext_item, typeList);

        initTypePopupWindow();
    }

    private void showTypePopupWindow(View view) {
        typePopupWindow.showAsDropDown(view);
    }

    private void initTypePopupWindow() {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.dateselectpopup, null);

        lsv_type = (ListView) contentView.findViewById(R.id.lsv_hospitals);

        lsv_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type = String.valueOf(position);

                page = 1;
                loadMore = true;
                loading_progress.setVisibility(View.VISIBLE);
                adapter = null;
                searchData();
                typePopupWindow.dismiss();
            }
        });
        lsv_type.setAdapter(typeAdapter);

        typePopupWindow = new PopupWindow(contentView,
                RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);

        typePopupWindow.setTouchable(true);
        typePopupWindow.setOutsideTouchable(true);
        typePopupWindow.setFocusable(true);
        typePopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }
}
