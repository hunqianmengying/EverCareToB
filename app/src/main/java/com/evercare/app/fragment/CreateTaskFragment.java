package com.evercare.app.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Activity.BaseFragment;
import com.evercare.app.Activity.CalendarActivity;
import com.evercare.app.Activity.LoginActivity;
import com.evercare.app.Activity.SearchProjectActivity;
import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.util.CommonUtil;
import com.evercare.app.util.Constant;
import com.evercare.app.util.DateTool;
import com.evercare.app.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

import static android.view.View.VISIBLE;


/**
 * 作者：LXQ on 2016-12-6 13:37
 * 邮箱：842202389@qq.com
 * 创建任务Fragment
 */
public class CreateTaskFragment extends BaseFragment {

    private TextView txt_projectname;
    private TextView txt_taskdate;
    private EditText edt_remark;

    private RadioButton rdb_followin;
    private RadioButton rdb_facediagnose;
    private View mProgressView;
    private Context context;
    private String custom_id;//后台需要的客户详情中的business_id
    private String custom_card_id;
    private String custom_name;

    private Button btn_savetask;

    private RelativeLayout layout_tem;
    private RelativeLayout time_layout;
    private String business_id;//选择项目的id
    private String project_name;
    private Toast toast = null;

    private Toast mToast;


    public static CreateTaskFragment newInstance(String business_cuid, String custom_card_id, String custom_name) {
        CreateTaskFragment fragment = new CreateTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putString("business_cuid", business_cuid);
        bundle.putString("custom_card_id", custom_card_id);
        bundle.putString("custom_name", custom_name);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected void initView(View view) {
        Bundle bundle = getArguments();
        custom_card_id = bundle.getString("custom_card_id");
        custom_name = bundle.getString("custom_name");
        custom_id = bundle.getString("business_cuid");
        context = getContext();
        txt_projectname = (TextView) view.findViewById(R.id.txt_projectname);
        time_layout = (RelativeLayout) view.findViewById(R.id.time_layout);
        layout_tem = (RelativeLayout) view.findViewById(R.id.layout_tem);
        txt_taskdate = (TextView) view.findViewById(R.id.txt_taskdate);
        edt_remark = (EditText) view.findViewById(R.id.edt_remark);
        txt_taskdate.setText(DateTool.getCurrentTime("yyyy-MM-dd"));
        rdb_followin = (RadioButton) view.findViewById(R.id.rdb_followin);
        mProgressView = view.findViewById(R.id.login_progress);
        rdb_facediagnose = (RadioButton) view.findViewById(R.id.rdb_facediagnose);
        btn_savetask = (Button) view.findViewById(R.id.btn_savetask);
        btn_savetask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rdb_facediagnose.isChecked()) {
                    MobclickAgent.onEvent(context, "create_appointment");
                    createAppointment();//创建预约
                } else {
                    MobclickAgent.onEvent(context, "create_task");
                    createTask();//创建跟进
                }
            }
        });

        //选择关注项目
        layout_tem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchProjectActivity.class);
                startActivityForResult(intent, 6);
            }
        });

        //时间选择
        time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CalendarActivity.class);
                intent.putExtra("currentTime", txt_taskdate.getText());
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            txt_taskdate.setText(data.getStringExtra("selectDate"));
        } else if (requestCode == 6 && data != null) {
            business_id = data.getStringExtra("business_id");
            project_name = data.getStringExtra("name");
            txt_projectname.setText(project_name);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_createtask;
    }

    @Override
    public void fetchData() {

    }

    private void showToast(Context context, String str) {
        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
        }
        mToast.show();
    }

    /**
     * 创建预约
     */
    private void createAppointment() {
        if (TextUtils.isEmpty(project_name) || "请选择关注项目".equals(project_name)) {
            showToast(context, "请选择关注项目");
            return;
        }
        if (mProgressView.getVisibility() == VISIBLE) {
            return;
        }
        showProgress(true);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_cuid", custom_id);//客户源id
            jsonObject.put("start_time", DateTool.converterTime(txt_taskdate.getText().toString()));//时间
            jsonObject.put("business_project_id", business_id);//项目类别源id
            jsonObject.put("custom_code", custom_card_id);//客户卡id
            jsonObject.put("description", edt_remark.getText());//备注
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.APPOINTMENT_CREATE, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<Boolean>>() {
                                }.getType();
                                JsonResult<Boolean> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                boolean isSuccess = jsonResult.getData().booleanValue();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && isSuccess) {
                                    showToast(context, "创建预约成功!");
                                    getActivity().finish();
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                showToast(context, "您的帐号在其他设备登录");
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                showToast(context, response.getString("msg"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        showToast(context, "网络不稳定稍后再试");
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        showToast(context, "网络不稳定稍后再试");
                        showProgress(false);
                    }
                }
        );
    }

    /**
     * 创建跟进
     */
    private void createTask() {
        if (TextUtils.isEmpty(project_name) || "请选择关注项目".equals(project_name)) {
            showToast(context, "请选择关注项目");
            return;
        }
        if (mProgressView.getVisibility() == VISIBLE) {
            return;
        }
        showProgress(true);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_cuid", custom_id);
            jsonObject.put("start_time", DateTool.converterTime(txt_taskdate.getText().toString()));
            jsonObject.put("project_name", project_name);
            jsonObject.put("description", edt_remark.getText());
            jsonObject.put("custom_name", custom_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.TASK_CREATE, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<Boolean>>() {
                                }.getType();
                                JsonResult<Boolean> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                boolean isSuccess = jsonResult.getData().booleanValue();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && isSuccess) {
                                    showToast(context, "创建跟进成功");
                                    getActivity().finish();
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                showToast(context, "您的帐号在其他设备登录");
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                showToast(context, response.getString("msg"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgress(false);
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        showToast(context, "网络不稳定稍后再试");
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        showToast(context, "网络不稳定稍后再试");
                        showProgress(false);
                    }
                }
        );
    }

    private void showProgress(final boolean show) {
        if (!isAdded()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? VISIBLE : View.GONE);
        }
    }
}
