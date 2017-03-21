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
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.CustomerBirthdayAdapter;
import com.evercare.app.adapter.SlidingButtonAdapter;
import com.evercare.app.adapter.SpinnerAdapter;
import com.evercare.app.adapter.TodayWorkAdapter;
import com.evercare.app.model.BirthdayInfo;
import com.evercare.app.model.CustomerBirthdayInfo;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.TaskPlanInfo;
import com.evercare.app.model.TaskPlanItemInfo;
import com.evercare.app.model.TodayWorkInfo;
import com.evercare.app.model.TodayWorkItemInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.DateTool;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.evercare.app.MyApplication.mContext;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 活动回访/其他回访
 */
public class BackReviewActivity extends Activity implements SlidingButtonAdapter.IonSlidingViewClickListener {

    private RecyclerView rlv_customer_birthday;
    private TextView tvStickyHeaderView;
    private TextView txt_left;
    private TextView txt_center;

    private SwipeRefreshLayout refreshLayout;
    private Context context;

    private SlidingButtonAdapter adapter;
    private TodayWorkInfo todayWorkInfo;
    private List<TaskPlanItemInfo> taskPlanItemInfos;

    private View loading_progress;

    private Spinner sp_select_type;

    private String type;

    private View no_value_view;
    private TextView txt_title_info;

    private SpinnerAdapter spinnerAdapter;

    private String describe;
    private String business_id;
    private String taskPlanId;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(BackReviewActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(BackReviewActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backreview);
        type = getIntent().getStringExtra("type");
        context = BackReviewActivity.this;
        initView();
        loading_progress.setVisibility(View.VISIBLE);
    }

    private void initView() {
        no_value_view = findViewById(R.id.no_value_view);
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);
        loading_progress = (View) findViewById(R.id.loading_progress);

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

        sp_select_type = (Spinner) findViewById(R.id.sp_select_type);
        sp_select_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taskPlanId = taskPlanItemInfos.get(position).getTask_plan_id();
                getReviewData(taskPlanId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                        getReviewData(taskPlanId);
                    }
                }, Constant.ON_REFRESH);
            }
        });

        if (TextUtils.equals(type, "activity")) {
            txt_center.setText("活动回访");
            txt_title_info.setText("活动回访");
        } else {
            txt_center.setText("其他回访");
            txt_title_info.setText("其他回访");
        }

        assert rlv_customer_birthday != null;
        assert tvStickyHeaderView != null;

        rlv_customer_birthday.setLayoutManager(new LinearLayoutManager(this));
        //rlv_customer_birthday.setAdapter(adapter);
        rlv_customer_birthday.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        getTaskPlan();
    }

    private void setHeaderText() {
        if (todayWorkInfo != null) {
            if (todayWorkInfo.getDaydata() != null && todayWorkInfo.getDaydata().size() > 0) {
                tvStickyHeaderView.setText("今日");
            } else if (todayWorkInfo.getStaledata() != null && todayWorkInfo.getStaledata().size() > 0) {
                tvStickyHeaderView.setText("已过期");
            } else if (todayWorkInfo.getFinishdata() != null && todayWorkInfo.getFinishdata().size() > 0) {
                tvStickyHeaderView.setText("已完成");
            } else {
                tvStickyHeaderView.setText("");
            }
        } else {
            tvStickyHeaderView.setText("");
        }
    }

    private void getTaskPlan() {
        final JSONObject jsonObject = new JSONObject();
        try {
            if (TextUtils.equals(type, "activity")) {
                jsonObject.put("type", "1");
            } else {
                jsonObject.put("type", "2");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.postWithJson(context, Constant.TASKPLAN_INDEX, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type modelType = new TypeToken<JsonListResult<TaskPlanItemInfo>>() {
                                }.getType();
                                JsonListResult<TaskPlanItemInfo> jsonResult = gson.fromJson(response.toString(), modelType);

                                String code = jsonResult.getCode();
                                taskPlanItemInfos = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && taskPlanItemInfos != null && taskPlanItemInfos.size() > 0) {
                                    spinnerAdapter = new SpinnerAdapter(BackReviewActivity.this, taskPlanItemInfos);
                                    sp_select_type.setAdapter(spinnerAdapter);
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
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        loading_progress.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        loading_progress.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                    }
                }
        );
    }


    private void getReviewData(String planid) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", type);
            jsonObject.put("task_plan_id", planid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.TASK_INDEX, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<TodayWorkInfo>>() {
                                }.getType();
                                JsonResult<TodayWorkInfo> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                todayWorkInfo = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && todayWorkInfo != null) {

                                    final int num1 = todayWorkInfo.getDaydata() == null ? 0 : todayWorkInfo.getDaydata().size();
                                    final int num2 = todayWorkInfo.getFinishdata() == null ? 0 : todayWorkInfo.getFinishdata().size();
                                    int num3 = todayWorkInfo.getStaledata() == null ? 0 : todayWorkInfo.getStaledata().size();

                                    if ((num1 + num2 + num3) > 0) {
                                        no_value_view.setVisibility(View.GONE);

                                        rlv_customer_birthday.setVisibility(View.VISIBLE);
                                        adapter = new SlidingButtonAdapter(BackReviewActivity.this, todayWorkInfo, txt_center.getText().toString());
                                        rlv_customer_birthday.setAdapter(adapter);
                                    } else {
                                        if (spinnerAdapter != null) {
                                            taskPlanItemInfos.remove(sp_select_type.getSelectedItemPosition());
                                            spinnerAdapter.notifyDataSetChanged();
                                            if (taskPlanItemInfos.size() > 0) {
                                                if (sp_select_type.getSelectedItemPosition() != 0) {
                                                    sp_select_type.setSelection(0);
                                                } else {
                                                    taskPlanId = taskPlanItemInfos.get(0).getTask_plan_id();
                                                    getReviewData(taskPlanId);
                                                }
                                            }
                                        }
                                        rlv_customer_birthday.setVisibility(View.GONE);
                                        no_value_view.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    if (spinnerAdapter != null) {
                                        taskPlanItemInfos.remove(sp_select_type.getSelectedItemPosition());
                                        spinnerAdapter.notifyDataSetChanged();
                                        if (taskPlanItemInfos.size() > 0) {
                                            if (sp_select_type.getSelectedItemPosition() != 0) {
                                                sp_select_type.setSelection(0);
                                            } else {
                                                taskPlanId = taskPlanItemInfos.get(0).getTask_plan_id();
                                                getReviewData(taskPlanId);
                                            }
                                        }
                                    }
                                    rlv_customer_birthday.setVisibility(View.GONE);
                                    no_value_view.setVisibility(View.VISIBLE);
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                if (spinnerAdapter != null) {
                                    taskPlanItemInfos.remove(sp_select_type.getSelectedItemPosition());
                                    spinnerAdapter.notifyDataSetChanged();
                                    if (taskPlanItemInfos.size() > 0) {
                                        if (sp_select_type.getSelectedItemPosition() != 0) {
                                            sp_select_type.setSelection(0);
                                        } else {
                                            taskPlanId = taskPlanItemInfos.get(0).getTask_plan_id();
                                            getReviewData(taskPlanId);
                                        }
                                    }
                                }
                                if (todayWorkInfo != null) {
                                    if (todayWorkInfo.getStaledata() != null) {
                                        todayWorkInfo.getStaledata().clear();
                                    }
                                    if (todayWorkInfo.getFinishdata() != null) {
                                        todayWorkInfo.getFinishdata().clear();
                                    }
                                    if (todayWorkInfo.getDaydata() != null) {
                                        todayWorkInfo.getDaydata().clear();
                                    }
                                }
                                rlv_customer_birthday.setVisibility(View.GONE);
                                no_value_view.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setHeaderText();
                        loading_progress.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                            errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        loading_progress.setVisibility(View.GONE);
                        rlv_customer_birthday.setVisibility(View.GONE);
                        tvStickyHeaderView.setText("");
                        no_value_view.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        loading_progress.setVisibility(View.GONE);
                        rlv_customer_birthday.setVisibility(View.GONE);
                        tvStickyHeaderView.setText("");
                        no_value_view.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);
                    }
                }
        );
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


        if (TextUtils.equals("活动回访", txt_center.getText().toString())) {
            if (!TextUtils.equals("已完成", itemInfo.getDate_state())) {
                Intent intent = new Intent(BackReviewActivity.this, Work_BackReviewActivity.class);

                intent.putExtra("type", itemInfo.getDate_state());
                intent.putExtra("module", "活动回访");
                intent.putExtra("tasktype", itemInfo.getDescribe());
                intent.putExtra("business_cuid", itemInfo.getBusiness_cuid());
                intent.putExtra("id", itemInfo.getId());
                intent.putExtra("business_id", itemInfo.getBusiness_id());
                intent.putExtra("start_time", itemInfo.getStart_time());
                startActivityForResult(intent, 3);
            }
        } else if (TextUtils.equals("其他回访", txt_center.getText().toString())) {
            //v1.0.0其他回访已过期不能点击看详情进行操作
            if (!TextUtils.equals("已完成", itemInfo.getDate_state())) {
                Intent intent = new Intent(BackReviewActivity.this, Work_BackReviewActivity.class);

                intent.putExtra("type", itemInfo.getDate_state());
                intent.putExtra("module", "其他回访");
                intent.putExtra("tasktype", itemInfo.getDescribe());
                intent.putExtra("business_cuid", itemInfo.getBusiness_cuid());
                intent.putExtra("id", itemInfo.getId());
                intent.putExtra("business_id", itemInfo.getBusiness_id());
                intent.putExtra("start_time", itemInfo.getStart_time());
                startActivityForResult(intent, 3);
            }
        }
    }


    @Override
    public void onDeleteBtnCilck(View view, int position) {
        MobclickAgent.onEvent(BackReviewActivity.this, "delete_item");

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
        MobclickAgent.onEvent(BackReviewActivity.this, "edit_time");

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
        } else if (requestCode == 3) {
            getReviewData(taskPlanId);
        }
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
                                    //getData();
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
                                    //getData();
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
                                    getReviewData(taskPlanId);
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
                                    getReviewData(taskPlanId);
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
}
