package com.evercare.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.evercare.app.util.CommonUtil;
import com.evercare.app.util.Constant;
import com.evercare.app.util.DateTool;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.RWTool;
import com.evercare.app.view.RadioButtonFlowLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

import static com.evercare.app.R.id.line_arrange;


/**
 * 作者：LXQ on 2016-11-15 11:34
 * 邮箱：842202389@qq.com
 * 今日工作   活动回访  其他回访
 */
public class Work_BackReviewActivity extends BaseActivity implements View.OnClickListener {
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
    private View line_arrange;
    private Button btn_open_order;
    private Button btn_delay;
    private Button btn_arrange_consult;
    private Button btn_done;
    private ImageView more_content;

    private View line_save;
    private View line_open;
    private View line_delay;


    private View line2;

    private RadioButtonFlowLayout flowlayout_detail;

    private TextView txt_phone;

    private List<ReasonInfo> detailList;

    private ArrayAdapter config_reasonAdapter;
    private ListView lsv_config_reasons;
    private boolean showDialog = false;

    private String module;//所属模块：今日可开单，今日工作，日程，活动回访，其他回访
    private String type;//类型：今日，已完成    今日、已过期、已完成      面诊任务、跟进任务     今日、已过期、已完成    今日、已过期
    private String tasktype;//跟进    预约
    private Context context;
    private TextView txt_project_name_tem;//
    private PopupWindow popupWindow;

    private String business_cuid;//客户id
    private String id;//id
    private String business_id;//任务源id

    private TaskDetailInfo taskDetailInfo;

    private ProgressBar loading_progress;
    private RelativeLayout title_view;
    private Handler myHandler;
    private String start_time;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openorder);

        module = getIntent().getStringExtra("module");
        type = getIntent().getStringExtra("type");
        tasktype = getIntent().getStringExtra("tasktype");
        business_cuid = getIntent().getStringExtra("business_cuid");
        business_id = getIntent().getStringExtra("business_id");
        start_time = getIntent().getStringExtra("start_time");
        id = getIntent().getStringExtra("id");
        context = Work_BackReviewActivity.this;

        RWTool.activityList.add(this);

        initView();
        getData();
    }


    private void addViewToFlowLayout() {
        flowlayout_detail.removeAllViews();
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        //RadioGroup.LayoutParams childLp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,30);
        lp.setMargins(48, 0, 0, 0);//设置边距

        for (int i = 0; i < detailList.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(detailList.get(i).getValue());
            radioButton.setTextColor(getResources().getColor(R.color.gray_7));
            radioButton.setTag(detailList.get(i).getAttribute_value());
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setLayoutParams(lp);
            radioButton.setTextSize(14);

            radioButton.setButtonDrawable(null);
            Drawable myImage = getResources().getDrawable(R.drawable.rdb_button);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(myImage, null, null, null);
            radioButton.setBackgroundResource(R.color.transparent);
            flowlayout_detail.addView(radioButton);

            radioButton.setCompoundDrawablePadding(10);
        }
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

        title_view = (RelativeLayout) findViewById(R.id.title_view);

        line2 = (View) findViewById(R.id.line2);

        edt_remark = (EditText) findViewById(R.id.edt_remark);
        edt_remark.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupWindow(v);
                return false;
            }
        });
        line_arrange = findViewById(R.id.line_arrange);
        rdb_low = (RadioButton) findViewById(R.id.rdb_low);
        rdb_high = (RadioButton) findViewById(R.id.rdb_high);

        rdb_high.setOnClickListener(this);
        rdb_low.setOnClickListener(this);
        more_content = (ImageView) findViewById(R.id.more_content);
        more_content.setOnClickListener(this);
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
        btn_done = (Button) findViewById(R.id.btn_done);
        btn_done.setOnClickListener(this);

        line_save = (View) findViewById(R.id.line_save);
        line_open = (View) findViewById(R.id.line_open);
        line_delay = (View) findViewById(R.id.line_delay);

        txt_project_name_tem = (TextView) findViewById(R.id.txt_project_name_tem);
        title_view.setOnClickListener(this);


        initPopupWindow();

        switch (module) {
            case "今日工作":
                if (!TextUtils.equals(type, "已完成")) {
                    if (TextUtils.equals(tasktype, "跟进")) {
                        btn_delay.setVisibility(View.VISIBLE);
                        btn_arrange_consult.setVisibility(View.VISIBLE);
                        line_delay.setVisibility(View.VISIBLE);
                    } else if (TextUtils.equals(tasktype, "预约")) {
                        btn_delay.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case "日程":
                if (TextUtils.equals(type, "跟进任务")) {
                    btn_arrange_consult.setVisibility(View.VISIBLE);
                    line_delay.setVisibility(View.VISIBLE);
                }
                btn_delay.setVisibility(View.VISIBLE);
                break;
            case "活动回访":
                if (!TextUtils.equals(type, "已完成")) {
                    btn_delay.setVisibility(View.VISIBLE);
                    btn_arrange_consult.setVisibility(View.VISIBLE);
                    line_delay.setVisibility(View.VISIBLE);
                }
                break;
            case "其他回访":
                if (!TextUtils.equals(type, "已完成")) {
                    btn_delay.setVisibility(View.VISIBLE);
                    btn_arrange_consult.setVisibility(View.VISIBLE);
                  //  btn_done.setVisibility(View.VISIBLE);
                    line_delay.setVisibility(View.VISIBLE);

                }
                break;
        }


        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x1222) {
                    if (isOverFlowed(txt_project_name_tem)) {
                        //if (txt_project_name_tem.getLineCount() > 2) {
                        more_content.setVisibility(View.VISIBLE);
                    } else {
                        more_content.setVisibility(View.GONE);
                    }
                }
            }
        };

        if (TextUtils.equals("今日工作", module)) {
            if (TextUtils.equals("跟进", tasktype)) {
                btn_done.setText("完成");
            } else if (TextUtils.equals("预约", tasktype)) {
                btn_done.setText("保存");
                btn_done.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 判断TextView的内容宽度是否超出其可用宽度
     *
     * @param tv
     * @return
     */
    public static boolean isOverFlowed(TextView tv) {
        //int lineNum = tv.getLineCount();
        int maxNum = tv.getMaxLines();
        //int maxW = tv.getMaxWidth();
        //int measureW = tv.getMeasuredWidth();
        //int w = tv.getWidth();
        int availableWidth = (tv.getMeasuredWidth() - tv.getPaddingLeft() - tv.getPaddingRight()) * maxNum;
        Paint textViewPaint = tv.getPaint();
        float textWidth = textViewPaint.measureText(tv.getText().toString());
        if (textWidth > availableWidth) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_arrow:
            case R.id.txt_phone:
                if (TextUtils.equals(tasktype, "跟进")) {
                    taskStart();
                }
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + txt_phone.getText()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.rdb_high:
                if (rdb_high.isChecked()) {
                    btn_done.setVisibility(View.VISIBLE);
                    flowlayout_detail.setVisibility(View.GONE);
                    if (TextUtils.equals("今日工作", module)) {
                        if (TextUtils.equals(tasktype, "跟进")) {
                            btn_delay.setVisibility(View.VISIBLE);
                            btn_arrange_consult.setVisibility(View.VISIBLE);
                            line_delay.setVisibility(View.VISIBLE);

                          //  btn_done.setVisibility(View.GONE);
                            btn_save.setVisibility(View.GONE);
                            btn_open_order.setVisibility(View.GONE);
                        } else if (TextUtils.equals(tasktype, "预约")) {
                            btn_delay.setVisibility(View.VISIBLE);

                            btn_open_order.setVisibility(View.GONE);
                            btn_save.setVisibility(View.GONE);
                            btn_done.setVisibility(View.GONE);
                            btn_arrange_consult.setVisibility(View.GONE);
                        }
                    } else {
                        if (TextUtils.equals(tasktype, "跟进")) {
                            btn_delay.setVisibility(View.VISIBLE);
                            line_delay.setVisibility(View.VISIBLE);
                            btn_arrange_consult.setVisibility(View.VISIBLE);

                            btn_save.setVisibility(View.GONE);
                            btn_open_order.setVisibility(View.GONE);
                            if (TextUtils.equals("其他回访", module)) {
                               // btn_done.setVisibility(View.VISIBLE);
                               // line_arrange.setVisibility(View.VISIBLE);
                            } else {
                                //btn_done.setVisibility(View.GONE);
                               // line_arrange.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                break;
            case R.id.rdb_low:
                if (rdb_low.isChecked()) {
                    flowlayout_detail.setVisibility(View.VISIBLE);
                    btn_done.setVisibility(View.GONE);
                    btn_delay.setVisibility(View.GONE);
                    btn_open_order.setVisibility(View.GONE);
                    btn_arrange_consult.setVisibility(View.GONE);

                    btn_save.setVisibility(View.VISIBLE);
                    line_arrange.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_arrange_consult://没问题
                MobclickAgent.onEvent(Work_BackReviewActivity.this, "arrange_consult");
                arrangeConsult();
                break;
            case R.id.btn_delay://没问题
                if (TextUtils.equals("跟进", tasktype)) {
                    MobclickAgent.onEvent(Work_BackReviewActivity.this, "task_delay");

                    Intent intent2 = new Intent(Work_BackReviewActivity.this, SelectDateActivity.class);
                    startActivityForResult(intent2, 1);
                } else if (TextUtils.equals("预约", tasktype)) {
                    MobclickAgent.onEvent(Work_BackReviewActivity.this, "appointment_delay");
                    Intent intent2 = new Intent(Work_BackReviewActivity.this, SelectDateActivity.class);
                    startActivityForResult(intent2, 2);
                }
                break;
            case R.id.btn_done:
                MobclickAgent.onEvent(Work_BackReviewActivity.this, "done_interface");
                showProgress(false);
                if (TextUtils.equals("今日工作", module)) {
                    if (TextUtils.equals("跟进", tasktype)) {
                        taskDone(1);
                    } else if (TextUtils.equals("预约", tasktype)) {
                        appointmentCancel();
                    }
                } else {
                    taskDone(1);
                }

                break;
            case R.id.btn_save:
                MobclickAgent.onEvent(Work_BackReviewActivity.this, "save_interface");
                saveOperation();
                break;
            case R.id.title_view:
                if (showDialog) {
                    showDialog();
                } else {
                    Intent intent2 = new Intent(Work_BackReviewActivity.this, CustomerInfoViewPagerActivity.class);
                    if (taskDetailInfo != null && !TextUtils.isEmpty(taskDetailInfo.getCustom_id())) {
                        intent2.putExtra("custom_id", taskDetailInfo.getCustom_id());
                        startActivity(intent2);
                    }
                }
                break;
            case R.id.more_content:
                if (txt_project_name_tem.getLineCount() > 2) {
                    txt_project_name_tem.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                    txt_project_name_tem.setLines(2);
                    more_content.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
                } else {
                    txt_project_name_tem.setEllipsize(null); // 展开
                    txt_project_name_tem.setSingleLine(false);
                    more_content.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_up));
                }
                break;
        }
    }

    private void taskStart() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_id", business_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.TASK_START, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                if (TextUtils.equals("ok", response.getString("data")) || response.getBoolean("data")) {
                                    //Toast.makeText(Work_BackReviewActivity.this, "！", Toast.LENGTH_SHORT).show();
                                } else {
                                    //Toast.makeText(Work_BackReviewActivity.this, "跟进保存失败！", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //Toast.makeText(Work_BackReviewActivity.this, "跟进保存失败！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                            errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        //Toast.makeText(Work_BackReviewActivity.this, "跟进保存失败！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        //Toast.makeText(Work_BackReviewActivity.this, "跟进保存失败！", Toast.LENGTH_SHORT).show();
                    }
                }

        );
    }


    /**
     * 保存操作
     */
    private void saveOperation() {
        if (TextUtils.equals("今日工作", module)) {
            //预约   -》低  appointment/finish  备注 和态度   至少有一项
            //跟进   -》低  task/finish         备注 和态度   至少有一项
            if (TextUtils.equals("跟进", tasktype)) {
                taskDone(0);
            } else if (TextUtils.equals("预约", tasktype)) {
                appointmentCancel();
            }
        } else {
            taskDone(0);
            //低   task/finish     备注 和态度   至少有一项
        }
    }

    private void showToast(Context context, String str) {
        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
        }
        mToast.show();
    }

    private void appointmentCancel() {
        String attributeValue = getAttributeValue();
        if (loading_progress.getVisibility() == View.VISIBLE) {
            return;
        }
        if (TextUtils.isEmpty(edt_remark.getText().toString()) && TextUtils.isEmpty(attributeValue)) {
            showToast(context, "添加记录和客户意愿不能同时为空!");
        } else {
            showProgress(true);
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("business_id", business_id);
                jsonObject.put("business_cuid", taskDetailInfo.getBusiness_cuid());
                jsonObject.put("custom_name", taskDetailInfo.getCustom_name());
                jsonObject.put("business_project_id", taskDetailInfo.getProject_class());
                jsonObject.put("customer_reasons", attributeValue);
                jsonObject.put("description", edt_remark.getText().toString());
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
                                        showToast(context, "保存成功！");
                                        finish();
                                    } else {
                                        showToast(context, "保存失败！");
                                    }
                                } else {
                                    showToast(context, "保存失败！");
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
                            showToast(context, "保存失败！");
                            showProgress(false);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                                throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            showToast(context, "保存失败！");
                            showProgress(false);
                        }
                    }
            );
        }
    }

    /**
     * 约咨询
     */
    private void arrangeConsult() {
        Intent intent = new Intent(Work_BackReviewActivity.this, AppointmentProjectActivity.class);
        if (taskDetailInfo != null) {
            intent.putExtra("custom_card", taskDetailInfo.getCustom_card());
            intent.putExtra("business_task_id", taskDetailInfo.getBusiness_id());//任务源id
            intent.putExtra("business_cuid", taskDetailInfo.getBusiness_cuid());//客户源id
            intent.putExtra("business_project_id", taskDetailInfo.getProject_class());//客户源id
            intent.putExtra("remark", edt_remark.getText().toString());
        }
        startActivity(intent);
    }

    /**
     * 创建咨询    保存
     */
    private void saveOrder() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_cuid", taskDetailInfo.getBusiness_id());
            jsonObject.put("custom_name", taskDetailInfo.getCustom_name());
            jsonObject.put("productType", taskDetailInfo.getProject_class());
            jsonObject.put("content", edt_remark.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.CONSULTANT_ADDCONSULT, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                if (TextUtils.equals("ok", response.getString("data"))) {
                                    Toast.makeText(Work_BackReviewActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(Work_BackReviewActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Work_BackReviewActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Work_BackReviewActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(Work_BackReviewActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }
                }

        );
    }

    /**
     * 跟进任务推迟
     *
     * @param date
     */
    private void taskDelay(String date) {
        if (loading_progress.getVisibility() == View.VISIBLE) {
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_id", business_id);
            jsonObject.put("start_time", date);
            if (!TextUtils.isEmpty(edt_remark.getText())) {
                jsonObject.put("description", edt_remark.getText().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showProgress(true);
        HttpUtils.postWithJson(context, Constant.TASK_DELAY, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                if (response.getBoolean("data")) {
                                    showToast(context, "跟进任务推迟成功！");
                                    setResult(3, new Intent());
                                    finish();
                                } else {
                                    showToast(context, "跟进任务推迟失败！");
                                }
                            } else {
                                showToast(context, "跟进任务推迟失败！");
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
                        showToast(context, "跟进任务推迟失败！");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        showProgress(false);
                        showToast(context, "跟进任务推迟失败！");
                    }
                }

        );
    }

    /**
     * 预约推迟
     *
     * @param date
     */
    private void appointmentDelay(String date) {
        if (loading_progress.getVisibility() == View.VISIBLE) {
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_id", business_id);
            jsonObject.put("description", edt_remark.getText().toString());
            jsonObject.put("start_time", date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        showProgress(true);
        HttpUtils.postWithJson(context, Constant.APPOINTMENT_DELAY, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                if (response.getBoolean("data")) {
                                    showToast(context, "预约推迟成功！");
                                    finish();
                                } else {
                                    showToast(context, "预约推迟失败！");
                                }
                            } else {
                                showToast(context, "预约推迟失败！");
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
                        showToast(context, "预约推迟失败！");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        showProgress(false);
                        showToast(context, "预约推迟失败！");
                    }
                }
        );
    }

    /**
     * 任务完成
     */
    private void taskDone(int buttonType) {
        if (loading_progress.getVisibility() == View.VISIBLE) {
            return;
        }
        String attributeValue = getAttributeValue();

//        if (TextUtils.isEmpty(edt_remark.getText().toString()) && TextUtils.isEmpty(attributeValue) && buttonType == 1) {
//            showToast(context, "添加记录不能为空");
//        } else if
        if(TextUtils.isEmpty(edt_remark.getText().toString()) && TextUtils.isEmpty(attributeValue) &&  buttonType != 1) {
            showToast(context, "添加记录和客户意愿不能同时为空");
        } else {
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
            HttpUtils.postWithJson(context, Constant.TASK_FINISH, jsonObject, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                if (!TextUtils.isEmpty(response.getString("data"))) {
                                    if (response.getBoolean("data")) {
                                        showToast(context, "任务完成！");
                                        setResult(3, new Intent());
                                        finish();
                                    } else {
                                        showToast(context, "任务失败！");
                                    }
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
                            showToast(context, "任务失败！");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                                throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            showProgress(false);
                            showToast(context, "任务失败！");
                        }
                    }
            );
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            String date = data.getStringExtra("selectDate");
            taskDelay(DateTool.converterTime(date));
        } else if (requestCode == 2 && resultCode == 1) {
            String date = data.getStringExtra("selectDate");
            appointmentDelay(DateTool.converterTime(date));
        }
    }

    private void showPopupWindow(View view) {
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.showAsDropDown(view);
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
            if (TextUtils.equals("今日工作", module)) {
                jsonObject.put("type", "work");
                jsonObject.put("flag", tasktype.equals("跟进") ? "2" : "1");
            } else if (TextUtils.equals("活动回访", module)) {
                jsonObject.put("type", "activity");
            } else {
                jsonObject.put("type", "otheractivity");
            }
            jsonObject.put("id", id);
            jsonObject.put("start_time", start_time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.TASK_DETAIL, jsonObject, new JsonHttpResponseHandler() {
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
                                if (TextUtils.equals(code, Result.SUCCESS) && taskDetailInfo != null) {
                                    txt_name.setText(taskDetailInfo.getCustom_name());
                                    txt_project_name.setText(taskDetailInfo.getProject_class_name());
                                    detailList = taskDetailInfo.getCustomer_reasons();
                                    if (TextUtils.equals(module, "其他回访") || TextUtils.equals(module, "活动回访")) {
                                        String str = "";
                                        // 1:未上门回访 2:未成交回访 3:术后回访 5:术后复查 6:术后调查 7:投诉回访 8:活动回访 10:注射回访 9:术前回访11:首次注射回访 12:治疗回访 13:首次治疗回访 4:其他
                                        switch (taskDetailInfo.getTask_type()) {
                                            case "1":
                                                str = "未上门回访 ";
                                                break;
                                            case "2":
                                                str = "未成交回访 ";
                                                break;
                                            case "3":
                                                str = "术后回访 ";
                                                break;
                                            case "4":
                                                str = "其他 ";
                                                break;
                                            case "5":
                                                str = "术后复查 ";
                                                break;
                                            case "6":
                                                str = "术后调查 ";
                                                break;
                                            case "7":
                                                str = "投诉回访 ";
                                                break;
                                            case "8":
                                                str = "活动回访 ";
                                                break;
                                            case "9":
                                                str = "术前回访 ";
                                                break;
                                            case "10":
                                                str = "注射回访 ";
                                                break;
                                            case "11":
                                                str = "注射回访 ";
                                                break;
                                            case "12":
                                                str = "治疗回访 ";
                                                break;
                                            case "13":
                                                str = "首次治疗回访 ";
                                                break;
                                            case "14":
                                                str = "客户满意度回访";
                                                break;
                                            case "15":
                                                str = "市场活动邀约任务";
                                                break;
                                        }
                                        txt_project_name_tem.setText(str + taskDetailInfo.getContent());
                                    } else {
                                        txt_project_name_tem.setText(taskDetailInfo.getContent());
                                    }
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            //新启动的线程无法访问该Activity里的组件
                                            //所以需要通过Handler发送信息
                                            Message msg = new Message();
                                            msg.what = 0x1222;
                                            //发送消息
                                            myHandler.sendMessage(msg);
                                        }
                                    }, (int) (0.2 * 1000));


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
                                    if (taskDetailInfo != null && !TextUtils.isEmpty(taskDetailInfo.getCustom_id())) {
                                        checkUser(taskDetailInfo.getCustom_id());
                                    }
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(Work_BackReviewActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(Work_BackReviewActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
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

    //产品管管说，进来先加载 不考虑用户在pc端被释放的情况
    private void checkUser(String custom_id) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("custom_id", custom_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.CUSTOM_DETAIL, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if ("11001".equals(response.getString("code"))) {
                        showDialog = true;
                    } else {
                        showDialog = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(Work_BackReviewActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(Work_BackReviewActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Work_BackReviewActivity.this);
        builder.setTitle("温馨提示");
        builder.setMessage("客户所属咨询师非本人");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(Work_BackReviewActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(Work_BackReviewActivity.this);
    }
}
