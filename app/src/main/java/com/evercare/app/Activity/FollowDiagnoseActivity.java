package com.evercare.app.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.DiagnoseConnectAdapter;
import com.evercare.app.model.DiagnoseConnectionInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.facebook.drawee.view.SimpleDraweeView;
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
 * 作者：LXQ on 2016-11-10 17:53
 * 邮箱：842202389@qq.com
 * 随诊客户
 */
public class FollowDiagnoseActivity extends Activity {
    private ViewPager img_viewpager;

    private List<DiagnoseConnectionInfo> connectionInfoList;

    private List<String> imageList;
    private List<View> viewList;
    private LayoutInflater layoutInflater;

    private PagerAdapter pagerAdapter;
    private DiagnoseConnectAdapter diagnoseConnectAdapter;
    private RecyclerView rlv_connection;

    private TextView txt_left;
    private TextView txt_center;

    private LinearLayout rg_points;

    private int currentItemIndex = -1;

    private Context context;

    private DiagnoseConnectAdapter.ReplyAndReadListener replyAndReadListener;
    private DiagnoseConnectAdapter.GridViewToViewPagerListener gridViewToViewPagerListener;

    private ProgressBar loading_progress;
    private View no_value_view;
    private TextView txt_title_info;
    private SwipeRefreshLayout refreshLayout;
    private int page = 1;
    private boolean loadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followdiagnose);
        layoutInflater = getLayoutInflater();
        context = FollowDiagnoseActivity.this;
        connectionInfoList = new ArrayList<DiagnoseConnectionInfo>();
        initView();
        initData();
        loading_progress.setVisibility(View.VISIBLE);
        page = 1;
        getAnswerList();
        initLoadMoreListener();
    }

    private void initView() {

        no_value_view = findViewById(R.id.no_value_view);
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);

        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);

        txt_left = (TextView) findViewById(R.id.left_text);
        txt_left.setText("返回");
        txt_left.setVisibility(View.VISIBLE);
        txt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txt_center = (TextView) findViewById(R.id.center_text);
        txt_center.setText("随诊回话");
        txt_title_info.setText("随诊回话");
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        rg_points = (LinearLayout) findViewById(R.id.rg_points);

        rlv_connection = (RecyclerView) findViewById(R.id.rlv_connection);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FollowDiagnoseActivity.this, LinearLayoutManager.VERTICAL, false);
        rlv_connection.setLayoutManager(layoutManager);
        img_viewpager = (ViewPager) findViewById(R.id.img_viewpager);

    }

    private void initData() {
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
                        getAnswerList();
                    }
                }, Constant.ON_REFRESH);

            }
        });

        imageList = new ArrayList<>();
        viewList = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            View view = layoutInflater.inflate(R.layout.image_layout, null);
            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.img_container);
            simpleDraweeView.setImageURI(imageList.get(i));
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    img_viewpager.setVisibility(View.GONE);
                    rg_points.setVisibility(View.INVISIBLE);
                }
            });
            viewList.add(view);
        }

        pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                if (viewList.size() > position) {
                    container.removeView(viewList.get(position));
                }
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));

                return viewList.get(position);
            }

            @Override
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
            }

            @Override
            public int getItemPosition(Object object) {
                if (getCount() > 0) {
                    return POSITION_NONE;
                }
                return super.getItemPosition(object);
            }
        };


        gridViewToViewPagerListener = new DiagnoseConnectAdapter.GridViewToViewPagerListener() {
            @Override
            public void onImageClickListener(int itemIndex, int imageIndex) {
                if (currentItemIndex != itemIndex) {

                    DiagnoseConnectionInfo connectionInfo = connectionInfoList.get(itemIndex);
                    String[] temImageList = connectionInfo.getImages().split(",");
                    if (viewList == null) {
                        viewList = new ArrayList<>();
                    }
                    viewList.clear();
                    for (int i = 0; i < temImageList.length; i++) {
                        View view = layoutInflater.inflate(R.layout.image_layout, null);
                        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.img_container);
                        simpleDraweeView.setImageURI(Constant.BASEURL_IMG + temImageList[i]);
                        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                img_viewpager.setVisibility(View.GONE);
                                rg_points.setVisibility(View.INVISIBLE);
                            }
                        });
                        viewList.add(view);
                    }
                    pagerAdapter.notifyDataSetChanged();


                    while (rg_points.getChildCount() > temImageList.length) {
                        rg_points.removeViewAt(0);
                    }

                    while (rg_points.getChildCount() < temImageList.length) {
                        ImageView imageView = new ImageView(FollowDiagnoseActivity.this);
                        imageView.setLayoutParams(new ViewGroup.LayoutParams(15, 15));
                        imageView.setBackground(ContextCompat.getDrawable(FollowDiagnoseActivity.this, R.drawable.rdb_unselected_bg));

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
                        layoutParams.leftMargin = 5;
                        layoutParams.rightMargin = 5;

                        rg_points.addView(imageView, layoutParams);
                    }
                    currentItemIndex = itemIndex;
                }

                for (int i = 0; i < rg_points.getChildCount(); i++) {
                    ImageView currentBt = (ImageView) rg_points.getChildAt(i);
                    if (currentBt != null) {
                        if (i == imageIndex) {
                            currentBt.setBackground(ContextCompat.getDrawable(FollowDiagnoseActivity.this, R.drawable.rdb_selected_bg));
                        } else {
                            currentBt.setBackground(ContextCompat.getDrawable(FollowDiagnoseActivity.this, R.drawable.rdb_unselected_bg));
                        }
                    }
                }

                img_viewpager.setCurrentItem(imageIndex, false);
                img_viewpager.setVisibility(View.VISIBLE);
                rg_points.setVisibility(View.VISIBLE);
            }
        };

        replyAndReadListener = new DiagnoseConnectAdapter.ReplyAndReadListener() {
            @Override
            public void onItemOperationListener(boolean isRead, int index, String reply) {
                DiagnoseConnectionInfo temItem = connectionInfoList.get(index);

                if (isRead) {
                    if (TextUtils.equals(temItem.getStatus(), "20")) {
                        answerRead(temItem.getId());
                    }
                } else {
                    answerReply(temItem.getId(), reply);
                }
            }
        };
        img_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Toast.makeText(FollowDiagnoseActivity.this, "onPageScrolled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(FollowDiagnoseActivity.this, "onPageSelected", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < rg_points.getChildCount(); i++) {
                    ImageView currentBt = (ImageView) rg_points.getChildAt(i);
                    if (currentBt != null) {
                        if (i == position) {
                            currentBt.setBackground(ContextCompat.getDrawable(FollowDiagnoseActivity.this, R.drawable.rdb_selected_bg));
                        } else {
                            currentBt.setBackground(ContextCompat.getDrawable(FollowDiagnoseActivity.this, R.drawable.rdb_unselected_bg));
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        img_viewpager.setAdapter(pagerAdapter);
    }


    private void getAnswerList() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("page", String.valueOf(page));
            jsonObject.put("row", String.valueOf(Constant.IMG_ROW));
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.ANSWERS_LIST, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            String data = response.getString("data");
                            if (!TextUtils.isEmpty(data)) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonListResult<DiagnoseConnectionInfo>>() {
                                }.getType();
                                JsonListResult<DiagnoseConnectionInfo> jsonResult = gson.fromJson(response.toString(), type);
                                String code = jsonResult.getCode();
                                List<DiagnoseConnectionInfo> responseHandler = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && responseHandler != null && responseHandler.size() > 0) {
                                    if (page > 1) {
                                        //connectionInfoList.addAll(responseHandler);
                                        diagnoseConnectAdapter.addFooterItem(responseHandler);
                                        //设置回到上拉加载更多
                                        diagnoseConnectAdapter.changeMoreStatus(Constant.PULLUP_LOAD_MORE);
                                        loadMore = true;
                                    } else {
                                        connectionInfoList.clear();
                                        connectionInfoList = responseHandler;
                                        diagnoseConnectAdapter = new DiagnoseConnectAdapter(FollowDiagnoseActivity.this, responseHandler, gridViewToViewPagerListener, replyAndReadListener);
                                        rlv_connection.setAdapter(diagnoseConnectAdapter);
                                    }
                                    no_value_view.setVisibility(View.GONE);
                                } else {
                                    no_value_view.setVisibility(View.VISIBLE);
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else if (page > 1) {
                                //没有加载更多了
                                diagnoseConnectAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                                loadMore = false;
                            } else {
                                no_value_view.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
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


    private void answerReply(String id, String reply) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("reply", reply);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.ANSWERS_REPLY, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<String>>() {
                                }.getType();
                                JsonResult<String> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                String result = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && TextUtils.equals(result, "提交成功")) {
                                    Toast.makeText(context, "随诊回复成功！", Toast.LENGTH_LONG).show();
                                    //刷新列表
                                    page = 1;
                                    getAnswerList();
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        //Toast.makeText(FollowDiagnoseActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        //Toast.makeText(FollowDiagnoseActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * 加载更多
     */
    private void initLoadMoreListener() {
        rlv_connection.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (diagnoseConnectAdapter != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == diagnoseConnectAdapter.getItemCount()) {
                        if(diagnoseConnectAdapter.getItemCount()<Constant.IMG_ROW){
                            diagnoseConnectAdapter.changeMoreStatus(Constant.NO_LOAD_MORE);
                            return;
                        }
                        if (loadMore) {
                            loadMore = false;//防止多次点击
                            page++;
                            //设置正在加载更多
                            diagnoseConnectAdapter.changeMoreStatus(Constant.LOADING_MORE);
                            //改为网络请求
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getAnswerList();
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

    private void answerRead(String id) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.ANSWERS_READ, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<String>>() {
                                }.getType();
                                JsonResult<String> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                String result = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && TextUtils.equals(result, "更新成功")) {
                                    Toast.makeText(context, "随诊问题更新已读状态,成功！", Toast.LENGTH_LONG).show();
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        loading_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        loading_progress.setVisibility(View.GONE);
                    }
                }
        );
    }
}
