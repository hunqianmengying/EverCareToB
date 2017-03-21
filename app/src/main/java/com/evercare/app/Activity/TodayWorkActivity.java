package com.evercare.app.Activity;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.CustomerBirthdayAdapter;
import com.evercare.app.adapter.SlidingButtonAdapter;
import com.evercare.app.model.TodayWorkInfo;
import com.evercare.app.model.TodayWorkItemInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.DateTool;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

import static com.evercare.app.MyApplication.mContext;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 今日工作界面
 */
public class TodayWorkActivity extends Activity implements SlidingButtonAdapter.IonSlidingViewClickListener {

    private RecyclerView rlv_customer_birthday;
    private TextView tvStickyHeaderView;

    private TextView txt_left;
    private TextView txt_center;
    private ImageView img_right;


    private SwipeRefreshLayout refreshLayout;
    private Context context;

    private SlidingButtonAdapter adapter;
    private TodayWorkInfo todayWorkInfo;

    private View loading_progress;

    private View no_value_view;
    private TextView txt_title_info;
    private String describe;
    private String business_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerbirthday);
        initView();
    }

    private void initView() {
        no_value_view = findViewById(R.id.no_value_view);
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);

        loading_progress = (View) findViewById(R.id.loading_progress);
        img_right = (ImageView) findViewById(R.id.right_image);
        img_right.setImageResource(R.drawable.ic_calendar);
        img_right.setVisibility(View.VISIBLE);
        img_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayWorkActivity.this, WorkCalendarActivity.class);
                startActivity(intent);
            }
        });

        rlv_customer_birthday = (RecyclerView) findViewById(R.id.rlv_customer_birthday);
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
        txt_center.setText("今日工作");
        txt_title_info.setText("今日工作");

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);

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
                        getData();
                    }
                }, Constant.ON_REFRESH);
            }
        });
        assert rlv_customer_birthday != null;
        assert tvStickyHeaderView != null;
        rlv_customer_birthday.setLayoutManager(new LinearLayoutManager(this));
        rlv_customer_birthday.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
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

                boolean isSignificantDelta = Math.abs(dy) > 3;
                if (isSignificantDelta) {
                    if (adapter != null && adapter.menuIsOpen()) {
                        adapter.closeMenu();
                    }
                }
            }
        });
        loading_progress.setVisibility(View.VISIBLE);
        context = TodayWorkActivity.this;
    }


    private void getData() {
        if(!NetUtil.isNetConnected(context)){
            refreshLayout.setRefreshing(false);
            Toast.makeText(context,"请检查网络连接",Toast.LENGTH_LONG).show();
            return;
        }
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("type", "work");
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.TASK_INDEX, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            String data = response.getString("data");
                            if (!TextUtils.isEmpty(data)) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<TodayWorkInfo>>() {
                                }.getType();
                                JsonResult<TodayWorkInfo> jsonResult = gson.fromJson(response.toString(), type);
                                String code = jsonResult.getCode();
                                todayWorkInfo = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && todayWorkInfo != null) {

                                    final int num1 = todayWorkInfo.getDaydata() == null ? 0 : todayWorkInfo.getDaydata().size();
                                    final int num2 = todayWorkInfo.getFinishdata() == null ? 0 : todayWorkInfo.getFinishdata().size();
                                    final int num3 = todayWorkInfo.getStaledata() == null ? 0 : todayWorkInfo.getStaledata().size();

                                    if ((num1 + num2 + num3) > 0) {
                                        adapter = new SlidingButtonAdapter(TodayWorkActivity.this, todayWorkInfo, "");
                                        rlv_customer_birthday.setAdapter(adapter);
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
                        }
                        loading_progress.setVisibility(View.GONE);
                        //刷新完成
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        loading_progress.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                        //刷新完成
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        loading_progress.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                        //刷新完成
                        refreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void setHeaderText() {
        if (todayWorkInfo.getDaydata() != null && todayWorkInfo.getDaydata().size() > 0) {
            tvStickyHeaderView.setText("今日");
        } else if (todayWorkInfo.getStaledata() != null && todayWorkInfo.getStaledata().size() > 0) {
            tvStickyHeaderView.setText("已过期");
        } else if (todayWorkInfo.getFinishdata() != null && todayWorkInfo.getFinishdata().size() > 0) {
            tvStickyHeaderView.setText("已完成");
        }
    }

    /**
     * 取消预约
     */
    private void appointmentCancel(String business_cuid, String custom_name, String business_project_id, String customer_reasons, String description) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_id", business_id);
            jsonObject.put("business_cuid", business_cuid);
            jsonObject.put("custom_name", custom_name);
            jsonObject.put("business_project_id", business_project_id);
            jsonObject.put("customer_reasons", customer_reasons);
            jsonObject.put("description", description);

        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.APPOINTMENT_CANCEL, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                if (TextUtils.equals("ok", response.getString("data")) || response.getBoolean("data")) {
                                    Toast.makeText(context, "预约取消成功", Toast.LENGTH_LONG).show();
                                    getData();
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(context, "预约取消失败", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        loading_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                        loading_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                        loading_progress.setVisibility(View.GONE);
                    }
                }
        );
    }

    /**
     * 取消任务
     */
    private void taskCancel() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_id", business_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.TASK_CANCEL, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                if (TextUtils.equals("ok", response.getString("data")) || response.getBoolean("data")) {
                                    Toast.makeText(context, "任务取消成功", Toast.LENGTH_LONG).show();
                                    getData();
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(context, "任务取消失败", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        loading_progress.setVisibility(View.GONE);
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                        loading_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                        loading_progress.setVisibility(View.GONE);
                    }
                }
        );
    }

    /**
     * 修改跟进任务时间
     */
    private void updateTaskTime(String dataTime) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_id", business_id);
            jsonObject.put("start_time", dataTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.TASK_MODIFY, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {

                                if (TextUtils.equals("ok", response.getString("data")) || response.getBoolean("data")) {
                                    Toast.makeText(context, "时间修改成功！", Toast.LENGTH_LONG).show();
                                    getData();
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(context, "时间修改失败！", Toast.LENGTH_LONG).show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        loading_progress.setVisibility(View.GONE);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                        loading_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                        loading_progress.setVisibility(View.GONE);
                    }
                }
        );
    }

    /**
     * 修改预约时间
     */
    private void updateAppointmentTime(String dataTime) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_id", business_id);
            jsonObject.put("start_time", dataTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.APPOINTMENT_MODIFY, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                if (TextUtils.equals("ok", response.getString("data")) || response.getBoolean("data")) {
                                    Toast.makeText(context, "时间修改成功", Toast.LENGTH_LONG).show();
                                    getData();
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(context, "时间修改失败", Toast.LENGTH_LONG).show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        loading_progress.setVisibility(View.GONE);
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                        loading_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                        loading_progress.setVisibility(View.GONE);
                    }
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            loading_progress.setVisibility(View.VISIBLE);
            String dataTime = data.getStringExtra("selectDate");
            if ("跟进".equals(describe)) {
                updateTaskTime(DateTool.converterTime(dataTime));
            } else if ("预约".equals(describe)) {
                updateAppointmentTime(DateTool.converterTime(dataTime));
            }
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        int num1 = todayWorkInfo.getDaydata() == null ? 0 : todayWorkInfo.getDaydata().size();
        int num2 = todayWorkInfo.getStaledata() == null ? 0 : todayWorkInfo.getStaledata().size();
        int num3 = todayWorkInfo.getFinishdata() == null ? 0 : todayWorkInfo.getFinishdata().size();
        TodayWorkItemInfo itemInfo = null;
        if (position < num1) {
            itemInfo = todayWorkInfo.getDaydata().get(position);
        } else if (position < (num1 + num2)) {
            itemInfo = todayWorkInfo.getStaledata().get(position - num1);
        } else if (position < (num1 + num2 + num3)) {
            itemInfo = todayWorkInfo.getFinishdata().get(position - num1 - num2);
        }
        if (!TextUtils.equals("已完成", itemInfo.getDate_state())) {

            Intent intent = new Intent(TodayWorkActivity.this, Work_BackReviewActivity.class);

            intent.putExtra("type", itemInfo.getDate_state());
            intent.putExtra("module", "今日工作");
            intent.putExtra("tasktype", itemInfo.getDescribe());
            intent.putExtra("business_cuid", itemInfo.getBusiness_cuid());
            intent.putExtra("id", itemInfo.getId());
            intent.putExtra("business_id", itemInfo.getBusiness_id());
            intent.putExtra("start_time", itemInfo.getStart_time());
            startActivity(intent);
        }
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        int num1 = todayWorkInfo.getDaydata() == null ? 0 : todayWorkInfo.getDaydata().size();
        int num2 = todayWorkInfo.getStaledata() == null ? 0 : todayWorkInfo.getStaledata().size();
        int num3 = todayWorkInfo.getFinishdata() == null ? 0 : todayWorkInfo.getFinishdata().size();
        TodayWorkItemInfo itemInfo = null;
        if (position < num1) {
            itemInfo = todayWorkInfo.getDaydata().get(position);
        } else if (position < (num1 + num2)) {
            itemInfo = todayWorkInfo.getStaledata().get(position - num1);
        } else if (position < (num1 + num2 + num3)) {
            itemInfo = todayWorkInfo.getFinishdata().get(position - num1 - num2);
        }
        describe = itemInfo.getDescribe();
        business_id = itemInfo.getBusiness_id();

        //Toast.makeText(context, "删除事件", Toast.LENGTH_SHORT).show();
        //删除任务
        loading_progress.setVisibility(View.VISIBLE);
        if ("跟进".equals(describe)) {
            taskCancel();
        } else if ("预约".equals(describe)) {
            appointmentCancel(itemInfo.getBusiness_cuid(), itemInfo.getCustom_name(), "", "", "");
        }
    }

    @Override
    public void onEditTimeBtnClick(View view, int position) {
        //Toast.makeText(context, "编辑事件", Toast.LENGTH_SHORT).show();

        int num1 = todayWorkInfo.getDaydata() == null ? 0 : todayWorkInfo.getDaydata().size();
        int num2 = todayWorkInfo.getStaledata() == null ? 0 : todayWorkInfo.getStaledata().size();
        int num3 = todayWorkInfo.getFinishdata() == null ? 0 : todayWorkInfo.getFinishdata().size();
        TodayWorkItemInfo itemInfo = null;
        if (position < num1) {
            itemInfo = todayWorkInfo.getDaydata().get(position);
        } else if (position < (num1 + num2)) {
            itemInfo = todayWorkInfo.getStaledata().get(position - num1);
        } else if (position < (num1 + num2 + num3)) {
            itemInfo = todayWorkInfo.getFinishdata().get(position - num1 - num2);
        }

        describe = itemInfo.getDescribe();
        business_id = itemInfo.getBusiness_id();

        Intent intent = new Intent(mContext, CalendarActivity.class);
        intent.putExtra("currentTime", DateTool.getCurrentTime("yyyy-MM-dd"));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}
