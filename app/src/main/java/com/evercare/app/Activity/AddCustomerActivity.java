package com.evercare.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.evercare.app.adapter.AddCustomerAdapter;
import com.evercare.app.adapter.CustomerBirthdayAdapter;
import com.evercare.app.adapter.SlidingButtonAdapter;
import com.evercare.app.model.AddCustomerInfo;
import com.evercare.app.model.CustomerInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

import static android.view.View.VISIBLE;

/**
 * 作者：LXQ on 2016-11-25 10:20
 * 邮箱：842202389@qq.com
 * 新增客户界面
 */
public class AddCustomerActivity extends Activity implements SlidingButtonAdapter.IonSlidingViewClickListener {

    private RecyclerView rlv_add_customer;
    private TextView tvStickyHeaderView;
    private TextView txt_left;
    private TextView txt_center;

    private String type = Constant.PRIVATE_SEA;

    private AddCustomerInfo addcustomerInfo;

    private SwipeRefreshLayout refreshLayout;
    private Context context;

    private AddCustomerAdapter adapter;

    private View loading_progress;
    private View no_value_view;
    private TextView txt_title_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerbirthday);
        initView();
    }

    private void initView() {
        context = AddCustomerActivity.this;
        no_value_view = findViewById(R.id.no_value_view);
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);
        rlv_add_customer = (RecyclerView) findViewById(R.id.rlv_customer_birthday);
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
        txt_center.setText("新分客户");
        txt_title_info.setText("新分客户");

        loading_progress = findViewById(R.id.loading_progress);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setEnabled(false);


        rlv_add_customer.setLayoutManager(new LinearLayoutManager(this));

        rlv_add_customer.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        HttpUtils.postWithJson(context, Constant.CUSTOM_NEW, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<AddCustomerInfo>>() {
                                }.getType();
                                JsonResult<AddCustomerInfo> jsonResult = gson.fromJson(response.toString(), type);
                                String code = jsonResult.getCode();
                                addcustomerInfo = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && addcustomerInfo != null) {
                                    if ((addcustomerInfo.getNewlist() != null && addcustomerInfo.getNewlist().size() > 0) ||
                                            (addcustomerInfo.getOldlist() != null && addcustomerInfo.getOldlist().size() > 0)) {
                                        adapter = new AddCustomerAdapter(context, addcustomerInfo);
                                        rlv_add_customer.setAdapter(adapter);
                                        setHeaderText();
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
                            showProgress(false);
                        }

                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        showProgress(false);
                        no_value_view.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        showProgress(false);
                        no_value_view.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    @Override
    public void onItemClick(View view, int position) {
        if (addcustomerInfo != null) {
            int newNum = addcustomerInfo.getNewlist() == null ? 0 : addcustomerInfo.getNewlist().size();
            int oldNum = addcustomerInfo.getOldlist() == null ? 0 : addcustomerInfo.getOldlist().size();
            CustomerInfo item = null;
            if (position < newNum) {
                item = addcustomerInfo.getNewlist().get(position);

            } else if (position < newNum + oldNum) {
                item = addcustomerInfo.getOldlist().get(position - newNum);
            }

            if (item != null) {
                Intent intent2 = new Intent(AddCustomerActivity.this, CustomerInfoViewPagerActivity.class);
                intent2.putExtra("custom_id", item.getCustom_id());
                startActivity(intent2);
                //Toast.makeText(context, "item点击", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        Toast.makeText(context, "item删除", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditTimeBtnClick(View view, int position) {
        if (addcustomerInfo != null) {
            int newNum = addcustomerInfo.getNewlist() == null ? 0 : addcustomerInfo.getNewlist().size();
            int oldNum = addcustomerInfo.getOldlist() == null ? 0 : addcustomerInfo.getOldlist().size();
            CustomerInfo item = null;
            if (position < newNum) {
                item = addcustomerInfo.getNewlist().get(position);

            } else if (position < newNum + oldNum) {
                item = addcustomerInfo.getOldlist().get(position - newNum);
            }

            if (item != null) {
                backToPublic(item, position);
            }
        }
    }

    private void setHeaderText() {
        if (addcustomerInfo != null) {
            if (addcustomerInfo.getNewlist() != null && addcustomerInfo.getNewlist().size() > 0) {
                tvStickyHeaderView.setText("今日新增");
            } else if (addcustomerInfo.getOldlist() != null && addcustomerInfo.getOldlist().size() > 0) {
                tvStickyHeaderView.setText("往期新增");
            }
        }
    }

    private void backToPublic(CustomerInfo item, final int position) {
        if (loading_progress.getVisibility() == VISIBLE) {
            return;
        }
        showProgress(true);
        final int todayNum = addcustomerInfo.getNewlist() == null ? 0 : addcustomerInfo.getNewlist().size();
        final int weekNum = addcustomerInfo.getOldlist() == null ? 0 : addcustomerInfo.getOldlist().size();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_id", item.getBusiness_id());//客户源id
            jsonObject.put("ocean", item.getOcean());
            jsonObject.put("name", item.getName());
            jsonObject.put("targetocean", "3");//1临时 2私有 3公海，将要去的
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.postWithJson(context, Constant.CUSTOM_RELEASE, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {
                        if (TextUtils.equals("ok", response.getString("data")) || response.getBoolean("data")) {
                            Toast.makeText(context, "客户已经退回公海！", Toast.LENGTH_SHORT).show();
                            if (adapter != null)
                                adapter.closeMenu();
                            getData();
                        } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                            Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "客户退回公海失败！", Toast.LENGTH_LONG).show();
                        }
                        showProgress(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showProgress(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                showProgress(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                showProgress(false);
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
