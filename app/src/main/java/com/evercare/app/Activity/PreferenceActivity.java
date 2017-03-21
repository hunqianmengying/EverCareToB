package com.evercare.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
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
import com.evercare.app.adapter.ActivityListAdapter;
import com.evercare.app.adapter.CaseListAdapter;
import com.evercare.app.model.ActivityItemInfo;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.util.Constant;
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
 * 作者：LXQ on 2016-9-26 11:46
 * 邮箱：842202389@qq.com
 * 活动和案例界面
 */
public class PreferenceActivity extends Activity {

    private RecyclerView contentRecyclerView;
    private Toolbar toolbar;
    private Animator mAnimator;
    private TextView txt_center;
    private TextView txt_left;
    private EditText edt_keyword;
    private View bottom_line;
    private TextView no_result;
    private int mTouchSlop;//获取系统认为的滑动的最小距离
    private float mFirstY;
    private float mCurrentY;
    private int direction;
    private boolean mShow = true;
    private String current_type;
    private Context context;
    private String TAG;//融云发送活动、案例
    private ImageView img_delete;

    private List<ActivityItemInfo> activityItemInfos = new ArrayList<ActivityItemInfo>();
    private List<ActivityItemInfo> caseItemInfos = new ArrayList<ActivityItemInfo>();

    private ProgressBar loading_progress;

    private View no_value_view;
    private TextView txt_title_info;
    private SwipeRefreshLayout refreshLayout;
    private int page = 1;
    private boolean loadMore = true;
    private CaseListAdapter caseListAdapter;
    private ActivityListAdapter activityListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        context = this;
        Intent intent = getIntent();
        current_type = intent.getStringExtra("type");
        TAG = intent.getStringExtra("TAG");
        initView();
        showProgress(true);
        initData();
        onRefresh();//案例刷新
        initLoadMoreListener();//案例加载更多

    }

    private void initView() {
        bottom_line = (View) findViewById(R.id.bottom_line);
        bottom_line.setVisibility(View.GONE);
        img_delete = (ImageView) findViewById(R.id.img_delete);
        no_value_view = findViewById(R.id.no_value_view);
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        txt_center = (TextView) findViewById(R.id.center_text);
        txt_center.setText(current_type);
        txt_title_info.setText(current_type);
        no_result = (TextView) findViewById(R.id.no_result);
        edt_keyword = (EditText) findViewById(R.id.edt_keyword);
        if (current_type.equals("活动")) {
            edt_keyword.setHint("请输入活动名称");
        } else {
            edt_keyword.setHint("请输入案例名称");
        }

        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
        txt_left = (TextView) findViewById(R.id.left_text);
        txt_left.setVisibility(View.VISIBLE);
        txt_left.setText(" 返回");
        txt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edt_keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) edt_keyword.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(PreferenceActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    no_value_view.setVisibility(View.GONE);
                    showProgress(true);
                    page = 1;
                    searchData(edt_keyword.getText().toString());
                    return true;
                }
                return false;
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        contentRecyclerView = (RecyclerView) findViewById(R.id.contentRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PreferenceActivity.this, LinearLayoutManager.VERTICAL, false);
        contentRecyclerView.setLayoutManager(layoutManager);
        //获取系统认为的最低滑动距离
        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        //对滑动时间进行判断
        View.OnTouchListener myTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mFirstY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurrentY = event.getY();
                        if (mCurrentY - mFirstY > mTouchSlop) {
                            direction = 0;//down
                        } else if (mFirstY - mCurrentY > mTouchSlop) {
                            direction = 1;//up
                        }

                        if (direction == 1) {
                            if (mShow) {
                                toolbarAnimal(1);//隐藏toolBar
                                mShow = !mShow;
                            }
                        } else if (direction == 0) {
                            if (!mShow) {
                                toolbarAnimal(0);//显示toolBar
                                mShow = !mShow;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        };
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_keyword.setText("");
            }
        });

        contentRecyclerView.setOnTouchListener(myTouchListener);
    }


    /**
     * 隐藏toolbar动画
     *
     * @param flag
     */
    private void toolbarAnimal(int flag) {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }

        if (flag == 0) {
            mAnimator = ObjectAnimator.ofFloat(toolbar, "translationY", toolbar.getTranslationY(), 0);
        } else {
            mAnimator = ObjectAnimator.ofFloat(toolbar, "translationY", toolbar.getTranslationY(), -toolbar.getHeight());
        }

        mAnimator.start();
    }

    private void initData() {
        no_result.setText("未获取到");
        if (current_type.equals("活动")) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("page", String.valueOf(page));
                jsonObject.put("row", Constant.IMG_ROW);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getActivity(jsonObject);
        } else if (current_type.equals("案例")) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("page", String.valueOf(page));
                jsonObject.put("row", Constant.IMG_ROW);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCases(jsonObject);

        }
    }

    private void searchData(String key_word) {
        no_result.setText("未搜索到");
        if (current_type.equals("活动")) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("page", String.valueOf(page));
                jsonObject.put("row", Constant.IMG_ROW);
                jsonObject.put("key_word", key_word);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getActivity(jsonObject);
        } else if (current_type.equals("案例")) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("page", String.valueOf(page));
                jsonObject.put("row", Constant.IMG_ROW);
                jsonObject.put("key_word", key_word);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCases(jsonObject);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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

    /**
     * 获取活动列表和搜索活动
     *
     * @param jsonObject
     */
    private void getActivity(JSONObject jsonObject) {
        HttpUtils.postWithJson(context, Constant.GET_ACTIVITY_LIST, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    String data = response.getString("data");
                    if (!TextUtils.isEmpty(data) && !"[]".equals(data)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonListResult<ActivityItemInfo>>() {
                        }.getType();
                        JsonListResult<ActivityItemInfo> jsonResult = gson.fromJson(response.toString(), type);
                        String code = jsonResult.getCode();
                        List<ActivityItemInfo> activityItem = jsonResult.getData();
                        if (TextUtils.equals(code, Result.SUCCESS) && activityItem != null && activityItem.size() > 0) {
                            if (page > 1) {
                                activityListAdapter.addFooterItem(activityItem);
                                //设置回到上拉加载更多
                                activityListAdapter.changeMoreStatus(Constant.PULLUP_LOAD_MORE);
                                loadMore = true;
                            } else {
                                activityItemInfos = activityItem;
                                activityListAdapter = new ActivityListAdapter(PreferenceActivity.this, activityItem, new CustomerItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int postion) {
                                        if ("ActivityProvider".equals(TAG) || "ProjectCaseProvider".equals(TAG)) {
                                            Intent resulstIntent = new Intent();
                                            resulstIntent.putExtra("name", activityItemInfos.get(postion).getTitle());
                                            resulstIntent.putExtra("url", activityItemInfos.get(postion).getLink());
                                            resulstIntent.putExtra("image", activityItemInfos.get(postion).getImage());
                                            setResult(0x121, resulstIntent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(PreferenceActivity.this, InformationMessageActivity.class);
                                            intent.putExtra("name", "活动详情");
                                            intent.putExtra("url", activityItemInfos.get(postion).getLink());
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onItemLongClick(View view, int position) {

                                    }
                                });

                                if (activityItem.size()< 3) {
                                    activityListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                    loadMore = false;
                                }

                                contentRecyclerView.setAdapter(activityListAdapter);
                            }
                            no_value_view.setVisibility(View.GONE);
                            contentRecyclerView.setVisibility(View.VISIBLE);
                        } else if (page > 1) {
                            //没有加载更多了
                            activityListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                            loadMore = false;
                        } else {
                            no_value_view.setVisibility(View.VISIBLE);
                            contentRecyclerView.setVisibility(View.GONE);
                        }
                    } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                        Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else if (page > 1) {
                        //没有加载更多了
                        activityListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                        loadMore = false;
                    } else {
                        no_value_view.setVisibility(View.VISIBLE);
                        contentRecyclerView.setVisibility(View.GONE);
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
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loading_progress.setVisibility(View.GONE);
                no_value_view.setVisibility(View.VISIBLE);
                refreshLayout.setRefreshing(false);
            }
        });

    }

    /**
     * 获取活动列表和搜索活动
     *
     * @param jsonObject
     */
    private void getCases(JSONObject jsonObject) {
        HttpUtils.postWithJson(context, Constant.GET_CASE_LIST, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    String data = response.getString("data");
                    if (!TextUtils.isEmpty(data) && !"[]".equals(data)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonListResult<ActivityItemInfo>>() {
                        }.getType();
                        JsonListResult<ActivityItemInfo> jsonResult = gson.fromJson(response.toString(), type);
                        String code = jsonResult.getCode();
                        List<ActivityItemInfo> dataItem = jsonResult.getData();
                        if (TextUtils.equals(code, Result.SUCCESS) && dataItem != null && dataItem.size() > 0) {
                            if (page > 1) {
                                caseListAdapter.addFooterItem(dataItem);
                                //设置回到上拉加载更多
                                caseListAdapter.changeMoreStatus(Constant.PULLUP_LOAD_MORE);
                                loadMore = true;
                            } else {
                                caseItemInfos = dataItem;
                                caseListAdapter = new CaseListAdapter(PreferenceActivity.this, dataItem, new CustomerItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int postion) {
                                        if ("ActivityProvider".equals(TAG) || "ProjectCaseProvider".equals(TAG)) {
                                            Intent resulstIntent = new Intent();
                                            resulstIntent.putExtra("name", caseItemInfos.get(postion).getTitle());
                                            resulstIntent.putExtra("url", caseItemInfos.get(postion).getLink());
                                            resulstIntent.putExtra("image", caseItemInfos.get(postion).getImage());
                                            setResult(0x121, resulstIntent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(PreferenceActivity.this, InformationMessageActivity.class);
                                            intent.putExtra("name", "案例详情");
                                            intent.putExtra("url", caseItemInfos.get(postion).getLink());
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onItemLongClick(View view, int position) {

                                    }
                                });
                                if (caseItemInfos.size()< 3) {
                                    caseListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                    loadMore = false;
                                }
                                contentRecyclerView.setAdapter(caseListAdapter);

                            }
                            no_value_view.setVisibility(View.GONE);
                            contentRecyclerView.setVisibility(View.VISIBLE);
                        } else if (page > 1) {
                            //没有加载更多了
                            caseListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                            loadMore = false;
                        } else {
                            no_value_view.setVisibility(View.VISIBLE);
                            contentRecyclerView.setVisibility(View.GONE);
                        }
                    } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                        Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else if (page > 1) {
                        //没有加载更多了
                        caseListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                        loadMore = false;
                    } else {
                        no_value_view.setVisibility(View.VISIBLE);
                        contentRecyclerView.setVisibility(View.GONE);
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
                contentRecyclerView.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loading_progress.setVisibility(View.GONE);
                no_value_view.setVisibility(View.VISIBLE);
                contentRecyclerView.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
            }
        });

    }


    /**
     * 案例  下拉刷新
     */
    private void onRefresh() {
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        refreshLayout.setProgressViewOffset(true, 80, 320);
        refreshLayout.setProgressViewEndTarget(true, 260);
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
     * 案例  加载更多
     */
    private void initLoadMoreListener() {
        contentRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                int itemCount;
                if (current_type.equals("活动")) {
                    if (activityListAdapter == null)
                        return;
                    itemCount = activityListAdapter.getItemCount();
                } else {
                    if (caseListAdapter == null)
                        return;
                    itemCount = caseListAdapter.getItemCount();
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == itemCount) {
                    if (itemCount < Constant.IMG_ROW) {
                        if (current_type.equals("活动")) {
                            activityListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                            return;
                        } else {
                            caseListAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                            return;
                        }
                    }
                    if (loadMore) {
                        loadMore = false;//防止多次点击
                        page++;
                        //设置正在加载更多
                        if (current_type.equals("活动")) {
                            activityListAdapter.changeMoreStatus(Constant.LOADING_MORE);
                        } else {
                            caseListAdapter.changeMoreStatus(Constant.LOADING_MORE);
                        }
                        //改为网络请求
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(edt_keyword.getText().toString())) {
                                    initData();
                                } else {
                                    searchData(edt_keyword.getText().toString());
                                }
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
}
