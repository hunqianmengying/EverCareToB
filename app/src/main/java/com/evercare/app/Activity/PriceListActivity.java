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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.PriceListAdapter;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.PriceInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.RWTool;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-11-2 10:28
 * 邮箱：842202389@qq.com
 * 价目表
 */
public class PriceListActivity extends Activity {
    private EditText edt_keyword;
    private ImageView img_delete;
    private RecyclerView rlv_price_list;
    private List<PriceInfo> priceInfoList;
    private TextView txt_left;
    //private List<PriceInfo> priceInfoList_tag;
    private TextView txt_center;
    private View view_bottom_line;
    private Context context;
    private ProgressBar loading_progress;

    private View no_value_view;
    private TextView txt_title_info;
    private TextView no_result;
    private boolean isSelectProject = false;
    private String business_id;
    private String business_cuid;
    private String business_project_id;
    private String custom_name;
    private String description;
    private boolean startFlag;
    private PriceListAdapter priceListAdapter;
    private boolean isFromProjectList;
    private SwipeRefreshLayout refreshLayout;
    private int page = 1;
    private boolean loadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricelist);
        context = this;
        isSelectProject = getIntent().getBooleanExtra("isSelectProject", false);

        RWTool.activityList.add(this);

        Intent intent = getIntent();
        business_cuid = intent.getStringExtra("business_cuid");
        business_id = intent.getStringExtra("business_id");
        business_project_id = intent.getStringExtra("business_project_id");
        custom_name = intent.getStringExtra("custom_name");
        description = intent.getStringExtra("description");


        startFlag = intent.getBooleanExtra("startFlag", false);
        isFromProjectList = intent.getBooleanExtra("isFromProjectList", false);
        initView();
        arriveTriageStart();

        showProgress(true);
        initData();
        onRefresh();
        initLoadMoreListener();
    }

    private void arriveTriageStart() {
        if (startFlag) {
            final JSONObject jsonObject = new JSONObject();
            try {
                //jsonObject.put("business_id", "0f26af49-e0bd-e611-80c6-82b73a4d1c19");
                jsonObject.put("business_id", business_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpUtils.postWithJson(context, Constant.ARRIVETRIAGE_START, jsonObject, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            try {
                                if (!TextUtils.isEmpty(response.getString("data"))) {
                                    if (TextUtils.equals("ok", response.getString("data")) || response.getBoolean("data")) {
                                        //Toast.makeText(OpenOrderActivity.this, "跟进任务推迟成功！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //Toast.makeText(OpenOrderActivity.this, "跟进任务推迟失败！", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    //Toast.makeText(OpenOrderActivity.this, "跟进任务推迟失败！", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //loading_progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                                errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            //loading_progress.setVisibility(View.GONE);
                            //Toast.makeText(OpenOrderActivity.this, "跟进任务推迟失败！", Toast.LENGTH_SHORT).show();
                            //no_value_view.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                                throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);

                        }
                    }
            );
        }
    }

    private void initView() {

        no_value_view = findViewById(R.id.no_value_view);
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        edt_keyword = (EditText) findViewById(R.id.edt_keyword);
        img_delete = (ImageView) findViewById(R.id.img_delete);
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
        txt_center.setText("价目");
        txt_title_info.setText("价目");
        no_result = (TextView) findViewById(R.id.no_result);
        view_bottom_line = (View) findViewById(R.id.bottom_line);
        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
        view_bottom_line.setVisibility(View.INVISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PriceListActivity.this, LinearLayoutManager.VERTICAL, false);
        rlv_price_list.setLayoutManager(layoutManager);
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_keyword.setText("");
            }
        });
        edt_keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) edt_keyword.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(PriceListActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                    showProgress(true);
                    page = 1;
                    if (TextUtils.isEmpty(edt_keyword.getText())) {
                        initData();
                    } else {
                        searchData(edt_keyword.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void initData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("page", String.valueOf(page));
            jsonObject.put("row", Constant.TEXT_ROW);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.PROJECTS_LIST, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data")) && !"[]".equals(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonListResult<PriceInfo>>() {
                                }.getType();
                                JsonListResult<PriceInfo> jsonResult = gson.fromJson(response.toString(), type);
                                String code = jsonResult.getCode();
                                List<PriceInfo> priceInfos = jsonResult.getData();
                                if (TextUtils.equals(code, Result.SUCCESS) && priceInfos.size() > 0) {
                                    if (page > 1) {
                                        //priceInfoList_tag.addAll(priceInfos);
                                        if (priceInfos.size() < Constant.TEXT_ROW) {
                                            //没有加载更多了
                                            priceListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                            loadMore = false;
                                        } else {
                                            loadMore = true;
                                        }
                                        priceListAdapter.addFooterItem(priceInfos);
                                    } else {
                                        no_value_view.setVisibility(View.GONE);
                                        priceInfoList = priceInfos;
                                      //  priceInfoList_tag = priceInfos;
                                        priceListAdapter = new PriceListAdapter(PriceListActivity.this, priceInfoList, new CustomerItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                if (isSelectProject) {
                                                    if (position < priceInfoList.size()) {
                                                        PriceInfo item = priceInfoList.get(position);
                                                        if (isFromProjectList) {
                                                            Intent intent = new Intent();
                                                            intent.putExtra("name", item.getName());
                                                            intent.putExtra("price", item.getPrice());
                                                            intent.putExtra("ProductID", item.getBusiness_id());
                                                            intent.putExtra("frequency", item.getFrequency());
                                                            intent.putExtra("isPackage", TextUtils.equals(item.getProductstructure(), "3"));

                                                            setResult(1, intent);
                                                            finish();
                                                        } else {
                                                            Intent intent = new Intent(PriceListActivity.this, ProjectListActivity.class);
                                                            intent.putExtra("name", item.getName());
                                                            intent.putExtra("price", item.getPrice());
                                                            intent.putExtra("ProductID", item.getBusiness_id());
                                                            intent.putExtra("frequency", item.getFrequency());
                                                            intent.putExtra("isPackage", TextUtils.equals(item.getProductstructure(), "3"));

                                                            intent.putExtra("business_cuid", business_cuid);
                                                            intent.putExtra("business_id", business_id);
                                                            intent.putExtra("business_project_id", business_project_id);
                                                            intent.putExtra("custom_name", custom_name);
                                                            intent.putExtra("description", description);

                                                            startActivity(intent);
                                                        }
                                                    }
                                                }
                                                if (priceInfoList.get(position).isSelected) {
                                                    priceInfoList.get(position).isSelected = false;
                                                    view.findViewById(R.id.rlv_price_list).setVisibility(View.GONE);
                                                    ImageView imageView = (ImageView) view.findViewById(R.id.expend_view);
                                                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
                                                } else {
                                                    if (priceInfoList.get(position).getPackAge() != null && priceInfoList.get(position).getPackAge().size() > 0) {
                                                        priceInfoList.get(position).isSelected = true;
                                                        view.findViewById(R.id.rlv_price_list).setVisibility(View.VISIBLE);
                                                        ImageView imageView = (ImageView) view.findViewById(R.id.expend_view);
                                                        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_up));
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onItemLongClick(View view, int position) {

                                            }
                                        });
                                        if (priceInfoList.size() < Constant.TEXT_ROW) {
                                            //没有加载更多了
                                            priceListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                            loadMore = false;
                                        }
                                        rlv_price_list.setAdapter(priceListAdapter);

                                        rlv_price_list.setVisibility(View.VISIBLE);
                                    }


                                } else if (page > 1) {
                                    //没有加载更多了
                                    priceListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                    loadMore = false;
                                } else {
                                    no_value_view.setVisibility(View.VISIBLE);
                                    no_result.setText("为获取到");
                                    rlv_price_list.setVisibility(View.GONE);
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else if (page > 1) {
                                //没有加载更多了
                                priceListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                loadMore = false;
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
                        if (page <= 1) {
                            rlv_price_list.setVisibility(View.GONE);
                            no_value_view.setVisibility(View.VISIBLE);
                        }
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
                        if (page <= 1) {
                            rlv_price_list.setVisibility(View.GONE);
                            no_value_view.setVisibility(View.VISIBLE);
                        }
                        loading_progress.setVisibility(View.GONE);
                        no_result.setText("未获取到");
                        refreshLayout.setRefreshing(false);
                        showProgress(false);
                        Toast.makeText(context, getResources().getString(R.string.net_work_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void searchData(String keyWord) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key_word", keyWord);
            jsonObject.put("page", String.valueOf(page));
            jsonObject.put("row", Constant.TEXT_ROW);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.PRODUCT_LIST, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String data = response.getString("data");
                    if (!TextUtils.isEmpty(data) && !"[]".equals(data)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonListResult<PriceInfo>>() {
                        }.getType();
                        JsonListResult<PriceInfo> jsonResult = gson.fromJson(response.toString(), type);
                        String code = jsonResult.getCode();
                        List<PriceInfo> priceInfos = jsonResult.getData();
                        if (TextUtils.equals(code, Result.SUCCESS) && priceInfos.size() > 0) {
                            if (page > 1) {
                                //priceInfoList_tag.addAll(priceInfos);
                                if (priceInfos.size() < Constant.TEXT_ROW) {
                                    //没有加载更多了
                                    priceListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                    loadMore = false;
                                } else {
                                    loadMore = true;
                                }
                                priceListAdapter.addFooterItem(priceInfos);
                            } else {
                                no_value_view.setVisibility(View.GONE);
                                priceInfoList = priceInfos;
                                //  priceInfoList_tag = priceInfos;
                                priceListAdapter = new PriceListAdapter(PriceListActivity.this, priceInfoList, new CustomerItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        if (isSelectProject) {
                                            if (position < priceInfoList.size()) {
                                                PriceInfo item = priceInfoList.get(position);
                                                if (isFromProjectList) {
                                                    Intent intent = new Intent();
                                                    intent.putExtra("name", item.getName());
                                                    intent.putExtra("price", item.getPrice());
                                                    intent.putExtra("ProductID", item.getBusiness_id());
                                                    intent.putExtra("frequency", item.getFrequency());
                                                    intent.putExtra("isPackage", TextUtils.equals(item.getProductstructure(), "3"));

                                                    setResult(1, intent);
                                                    finish();
                                                } else {
                                                    Intent intent = new Intent(PriceListActivity.this, ProjectListActivity.class);
                                                    intent.putExtra("name", item.getName());
                                                    intent.putExtra("price", item.getPrice());
                                                    intent.putExtra("ProductID", item.getBusiness_id());
                                                    intent.putExtra("frequency", item.getFrequency());
                                                    intent.putExtra("isPackage", TextUtils.equals(item.getProductstructure(), "3"));

                                                    intent.putExtra("business_cuid", business_cuid);
                                                    intent.putExtra("business_id", business_id);
                                                    intent.putExtra("business_project_id", business_project_id);
                                                    intent.putExtra("custom_name", custom_name);
                                                    intent.putExtra("description", description);

                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                        if (priceInfoList.get(position).isSelected) {
                                            priceInfoList.get(position).isSelected = false;
                                            view.findViewById(R.id.rlv_price_list).setVisibility(View.GONE);
                                            ImageView imageView = (ImageView) view.findViewById(R.id.expend_view);
                                            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
                                        } else {
                                            if (priceInfoList.get(position).getPackAge() != null && priceInfoList.get(position).getPackAge().size() > 0) {
                                                priceInfoList.get(position).isSelected = true;
                                                view.findViewById(R.id.rlv_price_list).setVisibility(View.VISIBLE);
                                                ImageView imageView = (ImageView) view.findViewById(R.id.expend_view);
                                                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_up));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onItemLongClick(View view, int position) {

                                    }
                                });
                                if (priceInfoList.size() < Constant.TEXT_ROW) {
                                    //没有加载更多了
                                    priceListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                    loadMore = false;
                                }
                                rlv_price_list.setAdapter(priceListAdapter);

                                rlv_price_list.setVisibility(View.VISIBLE);
                            }


                        } else {
                            Toast.makeText(context, jsonResult.getMsg() + code, Toast.LENGTH_LONG).show();
                        }

                    } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                        Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else if (page > 1) {
                        //没有加载更多了
                        priceListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                        loadMore = false;
                    } else {
                        no_value_view.setVisibility(View.VISIBLE);
                        no_result.setText("未搜索到");
                        rlv_price_list.setVisibility(View.GONE);
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
                loading_progress.setVisibility(View.GONE);
                no_value_view.setVisibility(View.VISIBLE);
                no_result.setText("未搜索到");
                refreshLayout.setRefreshing(false);
                rlv_price_list.setVisibility(View.GONE);
                showProgress(false);
                Toast.makeText(context, getResources().getString(R.string.net_work_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loading_progress.setVisibility(View.GONE);
                no_value_view.setVisibility(View.VISIBLE);
                no_result.setText("未搜索到");
                refreshLayout.setRefreshing(false);
                rlv_price_list.setVisibility(View.GONE);
                showProgress(false);
                Toast.makeText(context, getResources().getString(R.string.net_work_error), Toast.LENGTH_SHORT).show();
            }
        });
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
                        if (TextUtils.isEmpty(edt_keyword.getText().toString())) {
                            initData();
                        } else {
                            searchData(edt_keyword.getText().toString());
                        }
                    }
                }, Constant.ON_REFRESH);
            }
        });
    }

    /**
     * 加载更多
     */
    private void initLoadMoreListener() {
        rlv_price_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (priceListAdapter != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == priceListAdapter.getItemCount()) {
                        if (priceListAdapter.getItemCount() < Constant.TEXT_ROW) {
                            priceListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                            return;
                        }
                        if (loadMore) {
                            loadMore = false;//防止多次点击
                            page++;
                            //设置正在加载更多
                            priceListAdapter.changeMoreStatus(Constant.LOADING_MORE);
                            //改为网络请求
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (TextUtils.isEmpty(edt_keyword.getText().toString())) {
                                        initData();
                                    } else {
                                        //搜索时隐藏无数据画面  显示动画
                                        no_value_view.setVisibility(View.GONE);
                                        searchData(edt_keyword.getText().toString());
                                    }
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
