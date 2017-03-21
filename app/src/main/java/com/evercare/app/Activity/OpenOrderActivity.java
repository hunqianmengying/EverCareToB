package com.evercare.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;

import com.evercare.app.model.ReasonInfo;
import com.evercare.app.model.TaskDetailInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.RWTool;
import com.evercare.app.view.RadioButtonFlowLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-11-15 11:34
 * 邮箱：842202389@qq.com
 * 今日开单详情
 */
public class OpenOrderActivity extends BaseActivity implements View.OnClickListener {
    private TextView txt_left;
    private TextView txt_center;
    private ImageView img_arrow;

    private TextView txt_name;
    private TextView txt_type;
    private TextView txt_project_name;
    private EditText edt_remark;

    private RadioButton rdb_low;
    private RadioButton rdb_high;

    private Button btn_save;
    private Button btn_open_order;
    private Button btn_delay;
    private Button btn_arrange_consult;


    private View line_save;
    private View line_open;
    private View line_delay;

    private View line2;

    private RadioButtonFlowLayout flowlayout_detail;

    private TextView txt_phone;

    private List<ReasonInfo> detailList;

    private RelativeLayout title_view;

    private ArrayAdapter config_reasonAdapter;
    private ListView lsv_config_reasons;


    private String module;//所属模块：今日可开单，今日工作，日程，活动回访，其他回访
    private String type;//类型：今日，已完成    今日、已过期、已完成      面诊任务、跟进任务     今日、已过期、已完成    今日、已过期
    private String tasktype;//跟进    预约
    private Context context;

    private PopupWindow popupWindow;

    private String id;
    private String business_id;

    private TaskDetailInfo taskDetailInfo;

    private ProgressBar loading_progress;

    private boolean startFlag = false;

    private String custom_id;
    private String business_cuid;
    private String statusType;//初诊复诊在消费.....初诊不显示开单
    private View line_arrange;
    private Button btn_done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openorder);
        Intent intent = getIntent();

        RWTool.activityList.add(this);

        module = intent.getStringExtra("module");
        type = intent.getStringExtra("type");
        tasktype = intent.getStringExtra("tasktype");
        statusType = intent.getStringExtra("statusType");
        id = intent.getStringExtra("id");
        business_id = intent.getStringExtra("business_id");
        business_cuid = getIntent().getStringExtra("business_cuid");
        startFlag = intent.getBooleanExtra("startFlag", false);
        custom_id = intent.getStringExtra("custom_id");
        context = OpenOrderActivity.this;
        initView();
        arriveTriageStart();
        getData();
    }

    private void arriveTriageStart() {
        if (startFlag) {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("business_id", business_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpUtils.postWithJson(context, Constant.ARRIVETRIAGE_START, jsonObject, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                                errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
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


    private void addViewToFlowLayout() {
        flowlayout_detail.removeAllViews();
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(48, 0, 0, 0);//设置边距
        for (int i = 0; i < detailList.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(detailList.get(i).getValue());
            radioButton.setTextColor(getResources().getColor(R.color.gray_7));
            radioButton.setTag(detailList.get(i).getAttribute_value());
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setTextSize(14);
            radioButton.setButtonDrawable(null);
            radioButton.setLayoutParams(lp);
            Drawable myImage = getResources().getDrawable(R.drawable.rdb_button);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(myImage, null, null, null);
            radioButton.setBackgroundResource(R.color.transparent);
            flowlayout_detail.addView(radioButton);
            radioButton.setCompoundDrawablePadding(10);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(OpenOrderActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(OpenOrderActivity.this);
    }

    /**
     * 获取客户低意愿中选中项
     *
     * @return
     */
    private String getAttributeValue() {

        if (flowlayout_detail.getChildCount() > 0) {
            //RadioGroup radioGroup = (RadioGroup) flowlayout_detail.getChildAt(0);
            for (int i = 0; i < flowlayout_detail.getChildCount(); i++) {
                RadioButton childAt = (RadioButton) flowlayout_detail.getChildAt(i);
                if (childAt.isChecked()) {
                    return childAt.getTag().toString();
                }
            }
        }
        return "";
    }

    private void initView() {
        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
        line_arrange = findViewById(R.id.line_arrange);
        line_arrange.setVisibility(View.GONE);
        btn_done = (Button) findViewById(R.id.btn_done);
        btn_done.setVisibility(View.GONE);
        title_view = (RelativeLayout) findViewById(R.id.title_view);

        line2 = (View) findViewById(R.id.line2);

        edt_remark = (EditText) findViewById(R.id.edt_remark);
        edt_remark.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // 设置好参数之后再show
//                popupWindow.showAsDropDown(view);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                popupWindow.showAsDropDown(view);
                return false;
            }
        });

        rdb_low = (RadioButton) findViewById(R.id.rdb_low);
        rdb_high = (RadioButton) findViewById(R.id.rdb_high);

        rdb_high.setOnClickListener(this);
        rdb_low.setOnClickListener(this);

        img_arrow = (ImageView) findViewById(R.id.img_arrow);
        img_arrow.setOnClickListener(this);

        flowlayout_detail = (RadioButtonFlowLayout) findViewById(R.id.flowlayout_detail);


        txt_phone = (TextView) findViewById(R.id.txt_phone);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_type = (TextView) findViewById(R.id.txt_type);
        txt_project_name = (TextView) findViewById(R.id.txt_project_name);


        txt_phone.setOnClickListener(this);

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
        txt_center.setText("工作记录");

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_open_order = (Button) findViewById(R.id.btn_open_order);
        btn_open_order.setOnClickListener(this);

        btn_delay = (Button) findViewById(R.id.btn_delay);
        btn_delay.setOnClickListener(this);
        btn_arrange_consult = (Button) findViewById(R.id.btn_arrange_consult);
        btn_arrange_consult.setOnClickListener(this);

        line_save = findViewById(R.id.line_save);
        line_open = findViewById(R.id.line_open);
        line_delay = findViewById(R.id.line_delay);
        initPopupWindow();

        btn_save.setVisibility(View.VISIBLE);
        btn_save.setOnClickListener(this);
        line_save.setVisibility(View.VISIBLE);
        btn_open_order.setVisibility(View.VISIBLE);


        txt_name.setOnClickListener(this);
        title_view.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_arrow:
            case R.id.txt_phone:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + txt_phone.getText()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.rdb_high:
                if (rdb_high.isChecked()) {
                    if (TextUtils.equals("今日可开单", module)) {
                        btn_open_order.setVisibility(View.VISIBLE);
                        btn_save.setVisibility(View.VISIBLE);
                        line_save.setVisibility(View.VISIBLE);
                    }
                    flowlayout_detail.setVisibility(View.GONE);
                }
                break;
            case R.id.rdb_low:
                if (rdb_low.isChecked()) {
                    btn_open_order.setVisibility(View.GONE);
                    line_save.setVisibility(View.GONE);
                    btn_save.setVisibility(View.VISIBLE);
                    flowlayout_detail.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_open_order:
                if (edt_remark.getText() != null) {
                    taskDetailInfo.setRemark(edt_remark.getText().toString());
                }
                MobclickAgent.onEvent(OpenOrderActivity.this, "open_order_interface");
                Intent intent1 = new Intent(OpenOrderActivity.this, PriceListActivity.class);
                intent1.putExtra("business_id", taskDetailInfo.getBusiness_id());
                intent1.putExtra("business_cuid", taskDetailInfo.getBusiness_cuid());
                intent1.putExtra("isSelectProject", true);
                intent1.putExtra("business_project_id", taskDetailInfo.getProject_class());
                intent1.putExtra("custom_name", taskDetailInfo.getCustom_name());
                intent1.putExtra("description", edt_remark.getText().toString());
                startActivity(intent1);
                break;

            case R.id.btn_save:
                MobclickAgent.onEvent(OpenOrderActivity.this, "save_interface");
                arriveTriageFinish();
                break;
            case R.id.title_view:
                Intent intent2 = new Intent(OpenOrderActivity.this, CustomerInfoViewPagerActivity.class);
                intent2.putExtra("custom_id", custom_id);
                startActivity(intent2);
                break;
        }
    }


    private void arriveTriageFinish() {
        if (loading_progress.getVisibility() == View.VISIBLE) {
            return;
        }

        String attributeValue = getAttributeValue();

        if (rdb_high.isChecked()) {
            if (TextUtils.isEmpty(edt_remark.getText().toString())) {
                Toast.makeText(OpenOrderActivity.this, "备注不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (rdb_low.isChecked()) {
            if (TextUtils.isEmpty(edt_remark.getText().toString()) && TextUtils.isEmpty(attributeValue)) {
                Toast.makeText(OpenOrderActivity.this, "添加记录和客户意愿不能同时为空", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        showProgress(true);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_id", business_id);//任务原id
            jsonObject.put("business_cuid", business_cuid);//客户原id
            if (taskDetailInfo != null) {
                jsonObject.put("custom_name", taskDetailInfo.getCustom_name());
                jsonObject.put("business_project_id", taskDetailInfo.getProject_class());
            }
            if (rdb_high.isChecked()) {
                jsonObject.put("customer_reasons", "0");
            } else {
                jsonObject.put("customer_reasons", attributeValue);
            }
            jsonObject.put("description", edt_remark.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.ARRIVETRIAGE_FINISH, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                if (response.getBoolean("data")) {
                                    Toast.makeText(OpenOrderActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(OpenOrderActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(OpenOrderActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                            errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        showProgress(false);
                        Toast.makeText(OpenOrderActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        showProgress(false);
                        Toast.makeText(OpenOrderActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void initPopupWindow() {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.dateselectpopup, null);

        lsv_config_reasons = (ListView) contentView.findViewById(R.id.lsv_hospitals);
        lsv_config_reasons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edt_remark.append(taskDetailInfo.getCustomer_config_reasons().get(position) + ",");
                popupWindow.dismiss();
            }
        });

        popupWindow = new PopupWindow(contentView,
                RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 获取数据
     */
    private void getData() {
        showProgress(true);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.ARRIVETRIAGEDETAIL, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<TaskDetailInfo>>() {
                                }.getType();
                                JsonResult<TaskDetailInfo> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                taskDetailInfo = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && taskDetailInfo != null) {
                                    txt_name.setText(taskDetailInfo.getCustom_name());
                                    txt_project_name.setText(taskDetailInfo.getProject_class_name());
                                    detailList = taskDetailInfo.getCustomer_reasons();
                                    switch (taskDetailInfo.getType()) {
                                        case "1":
                                            txt_type.setText("初诊");
                                            break;
                                        case "2":
                                            txt_type.setText("复诊");
                                            break;
                                        case "3":
                                            txt_type.setText("复查");
                                            break;
                                        case "4":
                                            txt_type.setText("再消费");
                                            break;
                                        case "5":
                                            txt_type.setText("其他");
                                            break;
                                        case "6":
                                            txt_type.setText("治疗");
                                            break;
                                    }
                                    if (!TextUtils.isEmpty(taskDetailInfo.getMobile())) {
                                        txt_phone.setText(taskDetailInfo.getMobile());
                                        txt_phone.setVisibility(View.VISIBLE);
                                    }
                                    if (taskDetailInfo.getCustomer_reasons() != null) {
                                        if (taskDetailInfo.getCustomer_config_reasons() != null && taskDetailInfo.getCustomer_config_reasons().size() > 0) {
                                            config_reasonAdapter = new ArrayAdapter(context, R.layout.listtext_item, taskDetailInfo.getCustomer_config_reasons());
                                            lsv_config_reasons.setAdapter(config_reasonAdapter);
                                        }
                                        addViewToFlowLayout();
                                    }
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(OpenOrderActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();

                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(OpenOrderActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }
                }
        );
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
