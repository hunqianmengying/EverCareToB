package com.evercare.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.R;
import com.evercare.app.util.Constant;
import com.evercare.app.util.DateTool;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.RWTool;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-12-9 16:48
 * 邮箱：842202389@qq.com
 * 约咨询项目界面
 */
public class AppointmentProjectActivity extends BaseActivity {

    private TextView txt_left;
    private TextView txt_center;
    private RelativeLayout rl_content;
    private Button btn_confirm;
    private TextView txt_project_name;
    private String business_id;
    private TextView txt_time;
    private RelativeLayout rl_time;

    private Context context;

    private String custom_card;
    private String business_task_id;
    private String business_cuid;
    private String remark;
    private String business_project_id;

    private ProgressBar loading_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_project);
        context = AppointmentProjectActivity.this;
        RWTool.activityList.add(this);

        initView();
        initData();
    }

    private void initView() {
        txt_time = (TextView) findViewById(R.id.txt_time);
        rl_time = (RelativeLayout) findViewById(R.id.rl_time);

        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);

        txt_time.setText(DateTool.getCurrentTime("yyyy-MM-dd"));
        txt_center = (TextView) findViewById(R.id.center_text);
        txt_center.setText("咨询项目");
        txt_left = (TextView) findViewById(R.id.left_text);
        txt_left.setText("返回");
        txt_left.setVisibility(View.VISIBLE);
        txt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_content = (RelativeLayout) findViewById(R.id.rl_content);
        rl_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppointmentProjectActivity.this, SearchProjectActivity.class);
                startActivityForResult(intent, 6);
            }
        });
        txt_project_name = (TextView) findViewById(R.id.txt_project_name);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("请选择项目".equals(txt_project_name.getText().toString())) {
                    Toast.makeText(AppointmentProjectActivity.this, "请选择项目", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(custom_card)) {
                    showDialog();
                } else {
                    arrangeConsult();
                }
            }
        });

        rl_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppointmentProjectActivity.this, CalendarActivity.class);
                intent.putExtra("currentTime", DateTool.getCurrentTime("yyyy-MM-dd"));
                startActivityForResult(intent, 6);
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        custom_card = intent.getStringExtra("custom_card");
        business_task_id = intent.getStringExtra("business_task_id");
        business_cuid = intent.getStringExtra("business_cuid");
        remark = intent.getStringExtra("remark");
        business_project_id = intent.getStringExtra("business_project_id");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6) {
            if (resultCode == 6) {
                txt_project_name.setText(data.getStringExtra("name"));
                business_id = data.getStringExtra("business_id");
            } else if (resultCode == 1) {
                txt_time.setText(data.getStringExtra("selectDate"));
            }
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentProjectActivity.this);
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

    private void arrangeConsult() {
        if(loading_progress.getVisibility() == View.VISIBLE){
            return;
        }
        showProgress(true);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_task_id", business_task_id);
            jsonObject.put("business_cuid", business_cuid);
            jsonObject.put("start_time", txt_time.getText().toString());
            jsonObject.put("business_project_id", business_id);//选择项目返回的ID
            jsonObject.put("custom_code", custom_card);
            jsonObject.put("description", remark);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.APPOINTMENT_TASK, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                if (TextUtils.equals("ok", response.getString("data")) || response.getBoolean("data")) {
                                    Toast.makeText(context, "约咨询成功！", Toast.LENGTH_SHORT).show();

                                    RWTool.exitClient(AppointmentProjectActivity.this);

                                } else {
                                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "约咨询失败！", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        showProgress(false);
                        Toast.makeText(context, "约咨询失败！", Toast.LENGTH_SHORT).show();
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
